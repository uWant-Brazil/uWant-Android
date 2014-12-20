package br.com.uwant.models.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;

public class ProductAdapter extends BaseAdapter {

    private static int SIZE;
    private Context mContext;
    private View.OnClickListener mListener;
    private DisplayImageOptions mOptions;
    private ImageSize mTargetSize;
    private List<Product> mProducts = Collections.emptyList();

    public ProductAdapter(Context context, List<Product> products, View.OnClickListener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mProducts = products;

        Resources res = context.getResources();
        SIZE = ((res.getDisplayMetrics().heightPixels) / 3);
        this.mOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();
        this.mTargetSize = new ImageSize(SIZE, SIZE);
    }

    @Override
    public int getCount() {
        return this.mProducts.size();
    }

    @Override
    public Product getItem(int position) {
        return this.mProducts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_product, parent, false);
            holder = new ViewHolder();

            holder.editTextDescription = (EditText) convertView.findViewById(R.id.adapter_product_editText_description);
            holder.imageViewPicture = (ImageView) convertView.findViewById(R.id.adapter_product_imageView_picture);
            holder.imageViewClose = (ImageView) convertView.findViewById(R.id.adapter_product_imageView_close);

            holder.imageViewClose.setOnClickListener(this.mListener);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.position = position;
        holder.imageViewClose.setTag(position);

        Product product = getItem(position);
        Multimedia picture = product.getPicture();
        Uri uri = picture.getUri();
        String url = picture.getUrl();
        Bitmap bitmap = picture.getBitmap();
        if (bitmap != null) {
            holder.imageViewPicture.setImageBitmap(bitmap);
        } else if (uri != null){
            load(position, holder, uri);
        } else if (url != null) {
            load(position, holder, url);
        }

        return convertView;
    }

    private void load(final int position, ViewHolder vh, String url) {
        new AsyncTask<Object, Void, Bitmap>() {

            private ViewHolder viewHolder;

            @Override
            protected Bitmap doInBackground(Object... objects) {
                viewHolder = (ViewHolder) objects[0];
                String url = (String) objects[1];

                return ImageLoader.getInstance().loadImageSync(url, mOptions);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    if (viewHolder.position == position) {
                        viewHolder.imageViewPicture.setImageBitmap(bitmap);
                    }
                }
            }

        }.execute(vh, url);
    }

    private void load(final int position, ViewHolder vh, Uri uri) {
        new AsyncTask<Object, Void, Bitmap>() {

            private ViewHolder viewHolder;

            @Override
            protected Bitmap doInBackground(Object... objects) {
                viewHolder = (ViewHolder) objects[0];
                Uri uri = (Uri) objects[1];

                try {
                    Bitmap bitmap = Picasso.with(mContext)
                            .load(uri)
                            .placeholder(R.drawable.ic_semfoto)
                            .get();

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
                    if (viewHolder.position == position) {
                        viewHolder.imageViewPicture.setImageBitmap(bitmap);
                    }
                }
            }

        }.execute(vh, uri);
    }

    private static class ViewHolder {
        int position;
        ImageView imageViewClose;
        ImageView imageViewPicture;
        EditText editTextDescription;
    }

}
