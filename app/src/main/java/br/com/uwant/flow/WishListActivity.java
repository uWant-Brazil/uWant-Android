package br.com.uwant.flow;

import android.app.AlertDialog;
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
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.Session;
import com.facebook.SessionState;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
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
import br.com.uwant.models.watchers.ManufacturerWatcher;
import br.com.uwant.models.watchers.WishListWatcher;
import br.com.uwant.utils.PictureUtil;
import br.com.uwant.utils.UserUtil;
import br.com.uwant.utils.WishListUtil;

public class WishListActivity extends UWActivity implements View.OnClickListener,
        IRequest.OnRequestListener<List<Product>>, CompoundButton.OnCheckedChangeListener, UWFileBodyListener {

    private static final int NOTIFICATION_ID = 200;
    private static final int RQ_CAMERA = 300;
    private static final int RQ_GALLERY = 400;
    private static final String CONST_HEADS_UP_WIHLIST = "heads_up_wihlist";
    private static final String CONST_LINK_TAG = "link_tag";

    private Multimedia mMultimedia;
    private ImageView mImageViewPicture;
    private ImageView mImageViewPictureCircle;
    private DisplayImageOptions mOptions;

    public static enum EXTRA_MODE{CREATE, EDIT, DELETE}

    private WishList mWishList;
    private WishList mWishListExtra;
    private List<Product> mProducts;
    private WishListProductAdapter mAdapter;

    private Switch mSwitchView;
    private EditText mEditTextComment;
    private AutoCompleteTextView mEditTextStore;
    private AutoCompleteTextView mEditTextWishList;
    private EditText mEditTextLink;
    private ProgressFragmentDialog mProgressDialog;
    private TwoWayView mTwoWayView;
    private AlertFragmentDialog mAlertLink;
    private NotificationCompat.Builder mBuilder;

    private List<Product> mProductDeleted = null;
    private OnProductListener onProductListener = null;
    private ImageView mImageViewPresentetoClick;
    private Bitmap mBitmap;
    private Uri mUri;
    private ImageButton mImageButtonRemove;

    public interface OnProductListener {
        void onRemove(Product product);
    }

    final Session.StatusCallback callback = new Session.StatusCallback() {

        @Override
        public void call(final Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                UserUtil.shareFacebook(WishListActivity.this, this, mProgressDialog, mWishList, mMultimedia);
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
        this.mOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        Session session = Session.getActiveSession();
        if (session == null && savedInstanceState != null) {
            session = Session.restoreSession(this, null, callback, savedInstanceState);
            Session.setActiveSession(session);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_wishlist);

        User user = User.getInstance();
        Multimedia multimedia = user.getPicture();

        if (multimedia != null) {
            mImageViewPicture = (ImageView) findViewById(R.id.wishlist_imageView_picture);
            mImageViewPictureCircle = (ImageView) findViewById(R.id.wishlist_imageView_pictureCircle);

            Uri uri = multimedia.getUri();
            String url = multimedia.getUrl();
            if (uri != null) {
                load(uri);
            } else if (url != null) {
                load(url);
            }
        }

        TextView textViewLogin = (TextView) findViewById(R.id.wishlist_textView_login);
        textViewLogin.setText(String.format("@%s", user.getLogin()));

        mEditTextStore = (AutoCompleteTextView) findViewById(R.id.wishList_editText_store);
        mEditTextComment = (EditText) findViewById(R.id.wishList_editText_comment);
        mEditTextWishList = (AutoCompleteTextView) findViewById(R.id.wishList_editText_list);

        mEditTextStore.addTextChangedListener(new ManufacturerWatcher(this, mEditTextStore));
        mEditTextWishList.addTextChangedListener(new WishListWatcher(this, mEditTextWishList));

        mSwitchView = (Switch) findViewById(R.id.wishList_switch_share);
        mSwitchView.setOnCheckedChangeListener(this);

        final ImageButton buttonCamera = (ImageButton) findViewById(R.id.wishList_imageButton_picture);
        buttonCamera.setOnClickListener(this);
        final ImageButton buttonGallery = (ImageButton) findViewById(R.id.wishList_imageButton_gallery);
        buttonGallery.setOnClickListener(this);
        final ImageButton buttonLink = (ImageButton) findViewById(R.id.wishList_imageButton_link);
        buttonLink.setOnClickListener(this);
        mImageViewPresentetoClick = (ImageView) findViewById(R.id.wishlist_imageView_presente);
        mImageViewPresentetoClick.setOnClickListener(this);
        mImageButtonRemove = (ImageButton) findViewById(R.id.wishlist_button_remove);
        mImageButtonRemove.setOnClickListener(this);
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

    private boolean mPictureUpdated;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null)
            if (requestCode == PICTURE_REQUEST_CODE) {
                mPictureUpdated = true;
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                mPicturePath = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                        "uwant_picture");

                OutputStream os;
                try {
                    os = new FileOutputStream(mPicturePath);
                    photo.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                    Multimedia multimedia = new Multimedia();
                    multimedia.setUri(Uri.fromFile(mPicturePath));
                    fillProduct(multimedia);
                    mBitmap = PictureUtil.decodePicture(mPicturePath, mImageViewPresentetoClick, false);
                    mImageButtonRemove.setVisibility(View.VISIBLE);
                } catch (Exception e) {}

            } else if (requestCode == GALLERY_REQUEST_CODE) {
                mPictureUpdated = true;
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
                        && data.getData() != null
                        && data.getDataString() != null) {
                    try {
                        mPicturePath = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES),
                                "uwant_picture");

                        InputStream inputStream = getContentResolver().openInputStream(mUri);
                        byte[] buffer = new byte[inputStream.available()];
                        inputStream.read(buffer);

                        OutputStream outStream = new FileOutputStream(mPicturePath);
                        outStream.write(buffer);
                        mUri = Uri.fromFile(mPicturePath);

                        loadPictureAsync(mUri);
                        mImageButtonRemove.setVisibility(View.VISIBLE);
                        Multimedia multimedia = new Multimedia();
                        multimedia.setUri(mUri);
                        fillProduct(multimedia);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (filePath != null && filePath.startsWith("http")) {
                    loadPictureAsync(filePath);
                    mImageButtonRemove.setVisibility(View.VISIBLE);
                    Multimedia multimedia = new Multimedia();
                    multimedia.setUrl(filePath);
                    fillProduct(multimedia);
                } else {
                    mPicturePath = new File(filePath);
                    mUri = Uri.fromFile(mPicturePath);
                    mImageButtonRemove.setVisibility(View.VISIBLE);
                    mBitmap = PictureUtil.decodePicture(mPicturePath, mImageViewPresentetoClick, false);
                    Multimedia multimedia = new Multimedia();
                    multimedia.setUri(mUri);
                    fillProduct(multimedia);
                }

        } else if ((resultCode != RESULT_OK || !UserUtil.hasFacebook())
                && requestCode == UserUtil.RQ_FACEBOOK_LINK) {
            mSwitchView.setChecked(false);
        } else {
            Session session = Session.getActiveSession();
            if (session != null) {
                session.onActivityResult(this, requestCode, resultCode, data);
            }
        }
    }

    private void loadPictureAsync(Uri uri) {
        new AsyncTask<Object, Void, Bitmap>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressDialog = ProgressFragmentDialog.show(getSupportFragmentManager());
            }

            @Override
            protected Bitmap doInBackground(Object... objects) {
                Uri uri = (Uri) objects[0];

                try {
                    Bitmap bitmap = Picasso.with(WishListActivity.this)
                            .load(uri)
                            .placeholder(R.drawable.ic_semfoto).get();

                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    //bitmap = PictureUtil.cropToFit(bitmap);
                    //bitmap = PictureUtil.scale(bitmap, mImageViewPresentetoClick);
                    //bitmap = PictureUtil.circle(bitmap);

                    mImageButtonRemove.setVisibility(View.VISIBLE);
                    mImageViewPresentetoClick.setImageBitmap(bitmap);

                    mBitmap = bitmap;
                }
                mProgressDialog.dismiss();
            }

        }.execute(uri);
    }

    private void loadPictureAsync(final String url) {
        float dpi = getResources().getDisplayMetrics().density;
        int size = (int) (dpi * 76);
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        ImageSize imageSize = new ImageSize(size, size);

        final ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.loadImage(url, imageSize, options, new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressDialog = ProgressFragmentDialog.show(getSupportFragmentManager());
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressDialog.dismiss();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                File file = DiskCacheUtils.findInCache(url, imageLoader.getDiskCache());
                mUri = Uri.fromFile(file);

                bitmap = PictureUtil.cropToFit(bitmap);
                bitmap = PictureUtil.scale(bitmap, mImageViewPicture);
                bitmap = PictureUtil.circle(bitmap);

                mImageButtonRemove.setVisibility(View.VISIBLE);
                mImageViewPicture.setImageBitmap(bitmap);

                mBitmap = bitmap;
                mProgressDialog.dismiss();
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressDialog.dismiss();
            }

        });
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
/*        this.mAdapter.notifyDataSetChanged();

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
        });*/
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
                Intent intent = new Intent(this, WishListProductActivity.class);
                intent.putExtra(WishListProductActivity.EXTRA_MODE, WishListProductActivity.CAMERA);
                startActivityForResult(intent, RQ_CAMERA);
                break;

            case R.id.wishList_imageButton_gallery:
                Intent intentG = new Intent(this, WishListProductActivity.class);
                intentG.putExtra(WishListProductActivity.EXTRA_MODE, WishListProductActivity.GALLERY);
                startActivityForResult(intentG, RQ_GALLERY);
                break;

            case R.id.wishList_imageButton_link:
                configureLinkDialog();
                break;

            case R.id.wishlist_imageView_presente:
                showPictureOptions();
                break;

            case R.id.wishlist_button_remove:
                mImageButtonRemove.setVisibility(View.INVISIBLE);
                mImageViewPresentetoClick.setImageResource(R.drawable.ic_post_presente);
                this.mProducts = new ArrayList<Product>();
                break;

            default:
                break;
        }
    }

    private static final int PICTURE_REQUEST_CODE = 9898;
    private static final int GALLERY_REQUEST_CODE = 9797;
    private File mPicturePath;

    private void showPictureOptions() {
        final String[] options = getResources().getStringArray(R.array.options_register_picture);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //Intent intentG = new Intent(WishListActivity.this, WishListProductActivity.class);
                    //intentG.putExtra(WishListProductActivity.EXTRA_MODE, WishListProductActivity.GALLERY);
                    //startActivityForResult(intentG, RQ_GALLERY);
                    PictureUtil.openGallery(WishListActivity.this, GALLERY_REQUEST_CODE, false);
                } else {
//                    mPicturePath = new File(Environment.getExternalStoragePublicDirectory(
//                            Environment.DIRECTORY_PICTURES),
//                            "uwant_picture");
//                    PictureUtil.takePicture(WishListActivity.this, PICTURE_REQUEST_CODE);
                    //Intent intenta = new Intent(WishListActivity.this, WishListProductActivity.class);
                    //intenta.putExtra(WishListProductActivity.EXTRA_MODE, WishListProductActivity.CAMERA);
                    //startActivityForResult(intenta, RQ_CAMERA);
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, PICTURE_REQUEST_CODE);
                }
            }

        });
        builder.create().show();
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
                    model.setWishList(mWishList);
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
                                mMultimedia = product.getPicture();
                                UserUtil.shareFacebook(WishListActivity.this, callback, mProgressDialog, mWishList, product.getPicture());
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
                DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        compoundButton.setChecked(false);
                    }

                };

                UserUtil.showFacebookDialog(this, negative);
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

    private void load(Uri uri) {
        new AsyncTask<Object, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Object... objects) {
                Uri uri = (Uri) objects[1];

                try {
                    Bitmap bitmap = Picasso.with(WishListActivity.this)
                            .load(uri)
                            .placeholder(R.drawable.ic_contatos_semfoto).get();

                    bitmap = PictureUtil.cropToFit(bitmap);
                    bitmap = PictureUtil.scale(bitmap, mImageViewPicture);
                    bitmap = PictureUtil.circle(bitmap);

                    return bitmap;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    mImageViewPicture.setImageBitmap(bitmap);
                    mImageViewPictureCircle.setVisibility(View.VISIBLE);
                }
            }

        }.execute(uri);
    }

    private void load(String url) {
        new AsyncTask<Object, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Object... objects) {
                String url = (String) objects[0];

                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(url, mOptions);

                bitmap = PictureUtil.cropToFit(bitmap);
                bitmap = PictureUtil.scale(bitmap, mImageViewPicture);
                bitmap = PictureUtil.circle(bitmap);

                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    mImageViewPicture.setImageBitmap(bitmap);
                    mImageViewPictureCircle.setVisibility(View.VISIBLE);
                }
            }

        }.execute(url);
    }

}
