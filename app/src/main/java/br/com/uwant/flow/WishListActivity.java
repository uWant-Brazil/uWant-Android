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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import org.lucasr.twowayview.TwoWayView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.flow.fragments.ProgressFragmentDialog;
import br.com.uwant.models.adapters.WishListProductAdapter;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.utils.PictureUtil;

public class WishListActivity extends ActionBarActivity implements View.OnClickListener,
        IRequest.OnRequestListener<Boolean> {

    private static final int REQUEST_CAMERA = 984;
    private static final int REQUEST_GALLERY = 989;

    private List<Product> mProducts;
    private WishListProductAdapter mAdapter;
    private File mLastProductPicture;

    private Switch mSwitchView;
    private EditText mEditTextComment;
    private EditText mEditTextStore;
    private EditText mEditTextWishList;
    private ProgressFragmentDialog mProgressDialog;
    private TwoWayView mTwoWayView;

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
                // TODO Requisição...
                return true;

            default:
                return super.onOptionsItemSelected(item);
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

        Product product = new Product();
        product.setPicture(multimedia);
        this.mProducts.add(product);
        this.mAdapter.notifyDataSetChanged();

        if (!mTwoWayView.isShown()) {
            mTwoWayView.setVisibility(View.VISIBLE);
            ImageView imageViewPresent = (ImageView) findViewById(R.id.wishList_imageView_present);
            imageViewPresent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wishList_imageButton_picture:
                mLastProductPicture = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES),
                        "product-" + System.currentTimeMillis());

                PictureUtil.takePicture(this, this.mLastProductPicture, REQUEST_CAMERA);
                break;

            case R.id.wishList_imageButton_gallery:
                PictureUtil.openGallery(this, REQUEST_GALLERY);
                break;

            default:
                break;
        }
    }

    @Override
    public void onPreExecute() {
        this.mProgressDialog = ProgressFragmentDialog.show(R.string.text_wait, getSupportFragmentManager());
    }

    @Override
    public void onExecute(Boolean result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }

        };

        AlertFragmentDialog alertDialog = AlertFragmentDialog.create(getString(R.string.text_attention),
                "A sua lista de desejos foi criada com sucesso.",
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
