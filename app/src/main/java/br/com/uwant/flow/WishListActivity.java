package br.com.uwant.flow;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.adapters.WishListProductAdapter;
import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.WishListCreateModelAbstract;
import br.com.uwant.models.cloud.models.WishListProductPicture;
import br.com.uwant.utils.PictureUtil;

public class WishListActivity extends ActionBarActivity implements View.OnClickListener,
        IRequest.OnRequestListener<List<Product>> {

    private static final int REQUEST_CAMERA = 984;
    private static final int REQUEST_GALLERY = 989;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mProducts = new ArrayList<Product>(10);
        mAdapter = new WishListProductAdapter(this, mProducts);

        setContentView(R.layout.activity_wishlist);

        mEditTextStore = (EditText) findViewById(R.id.wishList_editText_store);
        mEditTextComment = (EditText) findViewById(R.id.wishList_editText_comment);
        mEditTextWishList = (EditText) findViewById(R.id.wishList_editText_list);

        mSwitchView = (Switch) findViewById(R.id.wishList_switch_share);

        final ImageButton buttonCamera = (ImageButton) findViewById(R.id.wishList_imageButton_picture);
        buttonCamera.setOnClickListener(this);
        final ImageButton buttonGallery = (ImageButton) findViewById(R.id.wishList_imageButton_gallery);
        buttonGallery.setOnClickListener(this);
        final ImageButton buttonLink = (ImageButton) findViewById(R.id.wishList_imageButton_link);
        buttonLink.setOnClickListener(this);

        mTwoWayView = (TwoWayView) findViewById(R.id.wishList_twoWayView);
        mTwoWayView.setAdapter(mAdapter);
        mTwoWayView.setOrientation(TwoWayView.Orientation.HORIZONTAL);
        mTwoWayView.setItemMargin(10);
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
            if (wishList == null) {
                // Criar lista de desejos
                for (Product product : this.mProducts) {
                    Manufacturer manufacturer = new Manufacturer();
                    manufacturer.setName(store);
                    product.setManufacturer(manufacturer);
                    product.setName("Product#" + mProducts.indexOf(product));
                    product.setNickName("Product#" + mProducts.indexOf(product));
                }

                wishList = new WishList();
                wishList.setTitle(wishListName);
                wishList.setDescription(comment);

                WishListCreateModelAbstract model = new WishListCreateModelAbstract();
                model.setWishList(wishList);
                model.setProducts(this.mProducts);
                Requester.executeAsync(model, this);
            } else {
                // Atualizar produtos da lista de desejos
                // O comentário virá a mensagem(EXTRA) para o feed na atualização...

            }
        } else {
            Toast.makeText(this, R.string.text_field_all_fields_correctly, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK && this.mLastProductPicture.exists()) {
                saveProduct(this.mLastProductPicture);

                this.mLastProductPicture = null;
            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
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

                File pictureFile = new File(filePath);
                saveProduct(pictureFile);
            }
        }
    }

    private void saveProduct(File pictureFile) {
        Multimedia multimedia = new Multimedia();
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wishList_imageButton_picture:
                mLastProductPicture = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                        "uw-product-" + System.currentTimeMillis() + ".jpg");

                PictureUtil.takePicture(this, this.mLastProductPicture, REQUEST_CAMERA);
                break;

            case R.id.wishList_imageButton_gallery:
                PictureUtil.openGallery(this, REQUEST_GALLERY);
                break;

            case R.id.wishList_imageButton_link:
                if (this.mEditTextLink == null) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params.setMargins(5, 5, 5, 5);

                    mEditTextLink = new EditText(this);
                    mEditTextLink.setLayoutParams(params);
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
                mAlertLink.show(getSupportFragmentManager(), "link_tag");
                break;

            default:
                break;
        }
    }

    private void loadLink() {
        String url = mEditTextLink.getText().toString();
        if (!url.isEmpty()) {
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
    public void onExecute(List<Product> result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        WishListProductPicture model = new WishListProductPicture();
        model.setProducts(result);
        Requester.executeAsync(model);

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
        alertDialog.show(getSupportFragmentManager(), "heads_up_wihlist");
    }

    @Override
    public void onError(RequestError error) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
    }

}
