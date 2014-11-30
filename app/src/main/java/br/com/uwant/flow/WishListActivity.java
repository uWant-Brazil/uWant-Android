package br.com.uwant.flow;

import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Session;
import com.facebook.SessionState;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.TwoWayView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.adapters.WishListProductAdapter;
import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.helpers.UWFileBodyListener;
import br.com.uwant.models.cloud.models.WishListCreateModel;
import br.com.uwant.models.cloud.models.WishListProductPictureModel;
import br.com.uwant.models.cloud.models.WishListUpdateModel;
import br.com.uwant.utils.PictureUtil;
import br.com.uwant.utils.UserUtil;
import br.com.uwant.utils.WishListUtil;

public class WishListActivity extends ActionBarActivity implements View.OnClickListener,
        IRequest.OnRequestListener<List<Product>>, CompoundButton.OnCheckedChangeListener, UWFileBodyListener {

    private static final int RQ_OPEN_CAMERA = 984;
    private static final int RQ_OPEN_GALLERY = 989;
    private static final int RQ_FACEBOOK_LINK = 823;
    private static final int NOTIFICATION_ID = 0x200;
    private static final String CONST_SWITCH_FACEBOOK_DIALOG = "SwitchFacebookDialog";
    private static final String CONST_HEADS_UP_WIHLIST = "heads_up_wihlist";
    private static final String CONST_LINK_TAG = "link_tag";
    private final List<String> FACEBOOK_PERMISSIONS = Arrays.asList("publish_actions");
    private Multimedia mMultimedia;
    private Uri mUri;

    public static enum EXTRA_MODE{CREATE, EDIT, DELETE}

    private WishList mWishList;
    private WishList mWishListExtra;
    private List<Product> mProducts;
    private WishListProductAdapter mAdapter;
    private File mLastProductPicture;

    private Switch mSwitchView;
    private EditText mEditTextComment;
    private EditText mEditTextStore;
    private EditText mEditTextWishList;
    private EditText mEditTextLink;
    private ProgressFragmentDialog mProgressDialog;
    private TwoWayView mTwoWayView;
    private AlertFragmentDialog mAlertLink;
    private NotificationCompat.Builder mBuilder;

    private List<Product> mProductDeleted = null;
    private OnProductListener onProductListener = null;

    public interface OnProductListener {
        void onRemove(Product product);
    }

    final Session.StatusCallback callback = new Session.StatusCallback() {

        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }

                shareFacebook(mMultimedia);
            } else if (session.isClosed() && !session.isOpened()) {
                if (mProgressDialog != null) {
                    mProgressDialog.dismiss();
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Session session = Session.getActiveSession();
        if (session == null && savedInstanceState != null) {
            session = Session.restoreSession(this, null, callback, savedInstanceState);
            Session.setActiveSession(session);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_wishlist);

        mEditTextStore = (EditText) findViewById(R.id.wishList_editText_store);
        mEditTextComment = (EditText) findViewById(R.id.wishList_editText_comment);
        mEditTextWishList = (EditText) findViewById(R.id.wishList_editText_list);

        mSwitchView = (Switch) findViewById(R.id.wishList_switch_share);
        mSwitchView.setOnCheckedChangeListener(this);

        final ImageButton buttonCamera = (ImageButton) findViewById(R.id.wishList_imageButton_picture);
        buttonCamera.setOnClickListener(this);
        final ImageButton buttonGallery = (ImageButton) findViewById(R.id.wishList_imageButton_gallery);
        buttonGallery.setOnClickListener(this);
        final ImageButton buttonLink = (ImageButton) findViewById(R.id.wishList_imageButton_link);
        buttonLink.setOnClickListener(this);
        boolean isExtra = getIntent().hasExtra(WishList.EXTRA);

        try {
            if (isExtra) {
                onProductListener = new OnProductListener() {
                    @Override
                    public void onRemove(Product product) {
                        if (mProductDeleted == null)
                            mProductDeleted = new ArrayList<Product>();

                        mProductDeleted.add(product);
                    }
                };

                mWishListExtra = (WishList) getIntent().getSerializableExtra(WishList.EXTRA);
                mEditTextStore.setText(
                        mWishListExtra.getProducts() != null && mWishListExtra.getProducts().size() >= 0 ?
                               mWishListExtra.getProducts().get(0).getManufacturer().getName() : " ");
                mEditTextComment.setText(mWishListExtra.getDescription());
                mEditTextWishList.setText(mWishListExtra.getTitle());
                mProducts = mWishListExtra.getProducts();
                mAdapter = new WishListProductAdapter(this, mProducts, onProductListener);
                mEditTextWishList.setTag(mWishListExtra);

            } else {
                mProducts = new ArrayList<Product>(10);
                mAdapter = new WishListProductAdapter(this, mProducts, onProductListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTwoWayView = (TwoWayView) findViewById(R.id.wishList_twoWayView);
        mTwoWayView.setAdapter(mAdapter);
        mTwoWayView.setOrientation(TwoWayView.Orientation.HORIZONTAL);
        mTwoWayView.setItemMargin(10);

        if (isExtra)
            fillProduct();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_wishlist_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_wishlist_accept:
                verify();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void verify() {
        String store = mEditTextStore.getText().toString();
        String comment = mEditTextComment.getText().toString();
        String wishListName = mEditTextWishList.getText().toString();

        if (!store.isEmpty()
                && !comment.isEmpty()
                && !wishListName.isEmpty()
                && !mProducts.isEmpty()) {
            WishList wishList = (WishList) mEditTextWishList.getTag();
            List<Product> produtosInseridos = null;

            // Criar lista de desejos
            for (Product product : this.mProducts) {
                if (product.getId() == 0) {
                    Manufacturer manufacturer = new Manufacturer();
                    manufacturer.setName(store);
                    product.setManufacturer(manufacturer);
                    product.setName(String.format("Product#%d", mProducts.indexOf(product)));
                    product.setNickName(String.format("Product#%d", mProducts.indexOf(product)));

                    if (produtosInseridos == null)
                        produtosInseridos = new ArrayList<Product>();

                    produtosInseridos.add(product);
                }

            }

            mWishList = new WishList();
            mWishList.setTitle(wishListName);
            mWishList.setDescription(comment);

            if (wishList == null) {
                WishListCreateModel model = new WishListCreateModel();
                model.setWishList(mWishList);
                model.setProducts(this.mProducts);
                Requester.executeAsync(model, this);
            } else {
                HashMap<WishListUpdateModel.Type, List<Product>> produtosEdit = new HashMap<WishListUpdateModel.Type, List<Product>>();
                produtosEdit.put(WishListUpdateModel.Type.INSERT, produtosInseridos);
                produtosEdit.put(WishListUpdateModel.Type.DELETE, mProductDeleted);

                mWishList.setId(wishList.getId());
                WishListUpdateModel model = new WishListUpdateModel();
                model.setWishList(mWishList);
                model.setmUpdateProducts(produtosEdit);
                Requester.executeAsync(model, this);
            }
        } else {
            Toast.makeText(this, R.string.text_field_all_fields_correctly, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == RQ_OPEN_CAMERA) {
                saveProduct(this.mLastProductPicture);
                this.mLastProductPicture = null;
            } else if (requestCode == RQ_OPEN_GALLERY) {
                mUri = data.getData();
                String[] filePathColumn = {
                        MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(
                        mUri, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(
                        filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                if ((filePath == null || filePath.isEmpty())
                        && data.getType().startsWith("image/")
                        && data.getData() != null
                        && data.getDataString() != null && data.getDataString().contains("docs.file")) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(mUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        Multimedia multimedia = new Multimedia();
                        multimedia.setBitmap(bitmap);
                        fillProduct(multimedia);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    try {
                        mLastProductPicture = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES),
                                String.format("uw-product-%d.jpg", System.currentTimeMillis()));

                        InputStream inputStream = getContentResolver().openInputStream(mUri);
                        byte[] buffer = new byte[inputStream.available()];
                        inputStream.read(buffer);

                        OutputStream outStream = new FileOutputStream(mLastProductPicture);
                        outStream.write(buffer);
                        mUri = Uri.fromFile(mLastProductPicture);

                        saveProduct(mLastProductPicture);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (filePath.startsWith("http")) {
                    saveProduct(filePath);
                } else {
                    this.mLastProductPicture = new File(filePath);
                    saveProduct(this.mLastProductPicture);
                }
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == RQ_OPEN_CAMERA) {
                saveProduct(this.mLastProductPicture);
                this.mLastProductPicture = null;
            } else if (requestCode == RQ_OPEN_GALLERY) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {
                        MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(
                        filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                this.mLastProductPicture = new File(filePath);
                saveProduct(this.mLastProductPicture);
            }
        } else {
            if ((resultCode != RESULT_OK || !UserUtil.hasFacebook()) && requestCode == RQ_FACEBOOK_LINK) {
                mSwitchView.setChecked(false);
            } else {
                Session session = Session.getActiveSession();
                if (session != null) {
                    session.onActivityResult(this, requestCode, resultCode, data);
                }
            }
        }
    }

    private void saveProduct(File pictureFile) {
        Multimedia multimedia = new Multimedia();
        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            byte[] bitmapData = bos.toByteArray();

            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        multimedia.setUri(Uri.fromFile(pictureFile));
        fillProduct(multimedia);
    }

    private void saveProduct(String url) {
        Multimedia multimedia = new Multimedia();
        multimedia.setUrl(url);

        fillProduct(multimedia);
    }

    private void fillProduct(Multimedia multimedia) {
        Product product = new Product();
        product.setPicture(multimedia);
        this.mProducts.add(product);
        this.mAdapter.notifyDataSetChanged();

        if (!mTwoWayView.isShown()) {
            mTwoWayView.setVisibility(View.VISIBLE);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.wishList_linearLayout_present);
            linearLayout.setVisibility(View.GONE);
        }

        mTwoWayView.post(new Runnable() {
            @Override
            public void run() {
                mTwoWayView.setSelection(mAdapter.getCount());
            }
        });

    }

    private void fillProduct() {

        if (!mTwoWayView.isShown()) {
            mTwoWayView.setVisibility(View.VISIBLE);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.wishList_linearLayout_present);
            linearLayout.setVisibility(View.GONE);
        }

        if (this.mAdapter != null) {
            this.mAdapter.notifyDataSetChanged();
            mTwoWayView.post(new Runnable() {
                @Override
                public void run() {
                    mTwoWayView.setSelection(mAdapter.getCount());
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wishList_imageButton_picture:
                mLastProductPicture = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                        String.format("uw-product-%d.jpg", System.currentTimeMillis()));

                PictureUtil.takePicture(this, RQ_OPEN_CAMERA);
                break;

            case R.id.wishList_imageButton_gallery:
                PictureUtil.openGallery(this, RQ_OPEN_GALLERY);
                break;

            case R.id.wishList_imageButton_link:
                configureLinkDialog();
                break;

            default:
                break;
        }
    }

    private void configureLinkDialog() {
        if (this.mEditTextLink == null) {
            final float scale = getResources().getDisplayMetrics().density;
            int marginDP = (int) (10 * scale + 0.5f);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(marginDP, marginDP, marginDP, marginDP);

            mEditTextLink = new EditText(this);
            mEditTextLink.setLayoutParams(params);
            mEditTextLink.setHint(R.string.text_hint_link);
            mEditTextLink.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mEditTextLink.setInputType(InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT);
            mEditTextLink.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (mAlertLink != null) {
                            mAlertLink.dismiss();
                        }

                        loadLink();
                        return true;
                    }

                    return false;
                }

            });
        } else {
            this.mEditTextLink.setText("");
        }

        DialogInterface.OnClickListener listenerP = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadLink();
            }

        };

        mAlertLink = AlertFragmentDialog.create(
                getString(R.string.app_name),
                mEditTextLink,
                getString(R.string.text_ok),
                listenerP,
                getString(R.string.text_cancel),
                null
        );
        mAlertLink.show(getSupportFragmentManager(), CONST_LINK_TAG);
    }

    private void loadLink() {
        String url = mEditTextLink.getText().toString();
        if (url != null && !url.isEmpty()) {
            saveProduct(url);
        } else {
            Toast.makeText(this, R.string.text_field_all_fields_correctly, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPreExecute() {
        this.mProgressDialog = ProgressFragmentDialog.show(R.string.text_wait, getSupportFragmentManager());
    }

    @Override
    public void onExecute(final List<Product> result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        new Thread() {

            @Override
            public void run() {
                super.run();
                for (int i = 0;i < result.size();i++) {
                    final Product product = result.get(i);
                    WishListProductPictureModel model = new WishListProductPictureModel();
                    model.setProduct(product);
                    model.setListener(WishListActivity.this);
                    if (i == 0) {
                        Requester.executeAsync(model, new IRequest.OnRequestListener<Multimedia>() {

                            @Override
                            public void onPreExecute() {
                                // notification sharing...
                            }

                            @Override
                            public void onExecute(Multimedia result) {
                                // close notification...
                                shareFacebook(product.getPicture());
                            }

                            @Override
                            public void onError(RequestError error) {
                                // close notification...
                            }

                        });
                    } else {
                        Requester.executeAsync(model);
                    }
                }
            }

        }.start();

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }

        };

        AlertFragmentDialog alertDialog = AlertFragmentDialog.create(getString(R.string.text_attention),
                getString(R.string.text_wishlist_created),
                getString(R.string.text_ok),
                listener);
        alertDialog.show(getSupportFragmentManager(), CONST_HEADS_UP_WIHLIST);
    }

    @Override
    public void onError(RequestError error) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCheckedChanged(final CompoundButton compoundButton, boolean checked) {
        if (compoundButton == mSwitchView && checked) {
            if (!UserUtil.hasFacebook()) {
                DialogInterface.OnClickListener lp = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent it = new Intent(WishListActivity.this, ConfigurationsActivity.class);
                        startActivityForResult(it, RQ_FACEBOOK_LINK);
                    }

                };
                DialogInterface.OnClickListener ln = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        compoundButton.setChecked(false);
                    }

                };

                AlertFragmentDialog afd = AlertFragmentDialog.create(getString(R.string.text_attention), getString(R.string.text_facebook_link), getString(R.string.text_yes, lp, getString(R.string.text_no), ln), lp, getString(R.string.text_no), ln);
                afd.show(getSupportFragmentManager(), CONST_SWITCH_FACEBOOK_DIALOG);
            }
        }
    }

    @Override
    public void preWrite(int totalAmount) {
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(false)
                        .setSmallIcon(R.drawable.ic_action_uwant)
                        .setContentTitle(getString(R.string.app_name))
                        .setProgress(totalAmount, 0, false)
                        .setContentText(getString(R.string.text_sending_pictures));

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public void written(int totalBytes, int amountOfBytes) {
        mBuilder.setProgress(totalBytes, amountOfBytes, false);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (totalBytes > amountOfBytes) {
            mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
        } else {
            mNotifyMgr.cancel(NOTIFICATION_ID);
            mBuilder = null;
        }
    }

    private void shareFacebook(Multimedia result) {
        mMultimedia = result;

        Session session = Session.getActiveSession();
        if (session != null) {
            // Check for publish permissions
            List<String> permissions = session.getPermissions();
            if (!isSubsetOf(FACEBOOK_PERMISSIONS, permissions)) {
                Session.NewPermissionsRequest newPermissionsRequest = new Session
                        .NewPermissionsRequest(WishListActivity.this, FACEBOOK_PERMISSIONS);
                session.requestNewPublishPermissions(newPermissionsRequest);
            } else {
                //WishListUtil.shareAction(WishListActivity.this.mWishList, result);
                WishListUtil.share(mWishList, result);
            }
        } else {
            User user = User.getInstance();
            AccessToken accessToken = AccessToken.createFromExistingAccessToken(user.getFacebookToken(), null, null, null, FACEBOOK_PERMISSIONS);
            Session.openActiveSessionWithAccessToken(WishListActivity.this, accessToken, callback);
        }
    }

    private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

}
