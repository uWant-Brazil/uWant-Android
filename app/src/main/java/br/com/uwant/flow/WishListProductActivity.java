package br.com.uwant.flow;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.models.adapters.ProductAdapter;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.utils.PictureUtil;

public class WishListProductActivity extends UWActivity implements View.OnClickListener {

    public static final int CAMERA = 199;
    public static final int GALLERY = 399;
    public static final String EXTRA_MODE = "extra_mode";

    private static final int RQ_GALLERY = 9382;
    private static final int RQ_CAMERA = 9322;
    private static final String TAG_CLOSE_DIALOG = "Close_Dialog";

    private List<Product> mProducts;
    private ProductAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_product);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mProducts = new ArrayList<Product>(5);
        mAdapter = new ProductAdapter(this, mProducts, this);

        final ListView listView = (ListView) findViewById(R.id.wishlist_product_listView);
        listView.setAdapter(mAdapter);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_MODE)) {
            int mode = intent.getIntExtra(EXTRA_MODE, CAMERA);
            switch (mode) {
                case GALLERY:
                    PictureUtil.openGallery(this, RQ_GALLERY, true);
                    break;

                default:
                    PictureUtil.takePicture(this, RQ_CAMERA, null);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RQ_CAMERA:
//                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    String path = data.getStringExtra("data");
                    File f = new File(path);

                    Multimedia multimedia = new Multimedia();
                    multimedia.setUri(Uri.fromFile(f));
                    fillProduct(multimedia);
                    break;

                case RQ_GALLERY:
                    getGalleryData(data);
                    break;

                default:
                    finish();
                    break;
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_wishlist_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_wishlist_accept:
                Intent data = new Intent();
                data.putExtra(Product.EXTRA, (ArrayList) this.mProducts);

                setResult(RESULT_OK, data);
                finish();
                break;

            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getGalleryData(Intent data) {
        List<String> images = (List<String>) data.getSerializableExtra(Product.EXTRA);
        for (String filePath : images) {
            if (filePath.startsWith("http")) {
                saveProduct(filePath);
            } else {
                File pictureFile = new File(filePath);
                saveProduct(pictureFile);
            }
        }
    }

    private void saveProduct(String url) {
        Multimedia multimedia = new Multimedia();
        multimedia.setUrl(url);

        fillProduct(multimedia);
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

    private void fillProduct(Multimedia multimedia) {
        Product product = new Product();
        product.setPicture(multimedia);

        this.mProducts.add(product);
        this.mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        final Integer position = (Integer) v.getTag();

        switch (v.getId()) {
            case R.id.adapter_product_imageView_close:
                closePicture(position);
                break;

            default:
                break;
        }
    }

    private void closePicture(final Integer position) {
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProducts.remove(position);

                if (mProducts.size() > 0) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    finish();
                }
            }

        };

        String msg = String.format("%s%s", getString(R.string.text_want_remove), (mProducts.size() > 1 ? "" : " " + getString(R.string.text_want_remove_2)));
        AlertFragmentDialog afd = AlertFragmentDialog.create(
                getString(R.string.text_attention),
                msg,
                listener
        );
        afd.show(getSupportFragmentManager(), TAG_CLOSE_DIALOG);
    }
}
