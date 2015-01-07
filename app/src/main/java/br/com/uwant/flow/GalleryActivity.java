package br.com.uwant.flow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.fragments.AlertFragmentDialog;
import br.com.uwant.models.classes.Product;

public class GalleryActivity extends UWActivity implements AdapterView.OnItemClickListener {

    private static final String CONST_HEADS_UP_WIHLIST = "heads_up_wihlist";

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private List<String> imageUrls;
    private DisplayImageOptions options;
    private ImageAdapter adapter;
    private SparseBooleanArray mSparseBooleanArray;

    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        Cursor imagecursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, String.format("%s DESC", orderBy));

        this.imageUrls = new ArrayList<String>();

        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));

            System.out.println("=====> Array path => "+imageUrls.get(i));
        }

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.ic_perfil_semfoto)
                .build();

        adapter = new ImageAdapter(this, imageUrls);

        mGridView = (GridView) findViewById(R.id.gallery_gridview);
        mGridView.setAdapter(adapter);
        this.mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(this);
    }

    @Override
    protected void onStop() {
        imageLoader.stop();
        super.onStop();
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
                ArrayList<String> selectedItems = adapter.getCheckedItems();

                Intent data = new Intent();
                data.putExtra(Product.EXTRA, selectedItems);

                setResult(RESULT_OK, data);
                finish();
                break;

            case android.R.id.home:
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }

                };

                AlertFragmentDialog alertDialog = AlertFragmentDialog.create(
                        getString(R.string.text_attention),
                        getString(R.string.text_cancel_product),
                        listener);
                alertDialog.show(getSupportFragmentManager(), CONST_HEADS_UP_WIHLIST);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean checked = !mSparseBooleanArray.get(position);
        mSparseBooleanArray.put(position, checked);
        mGridView.setItemChecked(position, checked);
    }

    private class ImageAdapter extends BaseAdapter {

        private List<String> mList;
        private LayoutInflater mInflater;
        private Context mContext;
        private CompoundButton.OnCheckedChangeListener mCheckedChangeListener;

        public ImageAdapter(Context context, List<String> imageList) {
            mContext = context;
            mList = imageList;
            mInflater = LayoutInflater.from(mContext);
            mSparseBooleanArray = new SparseBooleanArray();
            mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
                }

            };
        }

        public ArrayList<String> getCheckedItems() {
            ArrayList<String> mTempArry = new ArrayList<String>();

            for(int i=0;i<mList.size();i++) {
                if(mSparseBooleanArray.get(i)) {
                    mTempArry.add(mList.get(i));
                }
            }

            return mTempArry;
        }

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.adapter_gallery, parent, false);
            }

            final ImageView imageView = (ImageView) convertView.findViewById(R.id.adapter_gallery_imageView);

            imageLoader.displayImage(String.format("file://%s", imageUrls.get(position)), imageView, options, new SimpleImageLoadingListener() {

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    Animation anim = AnimationUtils.loadAnimation(GalleryActivity.this, android.R.anim.fade_in);
                    view.setAnimation(anim);
                    anim.start();
                }

            });

            return convertView;
        }

    }

}
