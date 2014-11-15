package br.com.uwant.models.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.picasso.Picasso;

import org.lucasr.twowayview.TwoWayView;

import java.io.IOException;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.WishListActivity;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;

public class FeedWishListProductAdapter extends BaseAdapter {

    private static int SIZE;
    private static float DEFAULT_RADIUS;
    private final Context mContext;
    private final DisplayImageOptions mOptions;
    private final ImageSize mTargetSize;
    private List<Product> mProducts;

    public FeedWishListProductAdapter(Context context, List<Product> products) {
        Resources res = context.getResources();
        SIZE = (res.getDisplayMetrics().heightPixels) / 3;
        DEFAULT_RADIUS = res.getDimension(R.dimen.cardview_default_radius);

        this.mContext = context;
        this.mProducts = products;
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
        return this.mProducts != null ? mProducts.size() : 0;
    }

    @Override
    public Product getItem(int i) {
        return this.mProducts != null ? this.mProducts.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_feed_wish_list_product, viewGroup, false);
            holder.hImageViewProduct = (ImageView) view.findViewById(R.id.adapter_feedWishlistProduct_imageView);
            holder.mProgressBar = (ProgressBar) view.findViewById(R.id.adapter_feedWishlist_product_loading);
            holder.hPosition = i;

            TwoWayView.LayoutParams params = new TwoWayView.LayoutParams(TwoWayView.LayoutParams.MATCH_PARENT, TwoWayView.LayoutParams.WRAP_CONTENT);

            CardView cardView = new CardView(this.mContext);
            cardView.setLayoutParams(params);
            cardView.setRadius(DEFAULT_RADIUS);
            cardView.addView(view);

            view = cardView;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Product product = getItem(i);
        final Multimedia picture = product.getPicture();
        Uri uri = picture.getUri();
        String url = picture.getUrl();
        Bitmap bitmap = picture.getBitmap();
        if (bitmap != null) {
            holder.hImageViewProduct.setImageBitmap(bitmap);
        } else if (uri != null){
            load(i, holder, uri);
        } else if (url != null) {
            load(i, holder, url);
        }

        return view;
    }

    private void load(final int position, ViewHolder vh, String url) {
        new AsyncTask<Object, Void, Bitmap>() {

            private ViewHolder viewHolder;

            @Override
            protected Bitmap doInBackground(Object... objects) {
                viewHolder = (ViewHolder) objects[0];
                String url = (String) objects[1];

                return ImageLoader.getInstance().loadImageSync(url, mTargetSize, mOptions);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    if (viewHolder.hPosition == position) {
                        viewHolder.hImageViewProduct.setImageBitmap(bitmap);
                        viewHolder.hImageViewProduct.setVisibility(View.VISIBLE);
                        viewHolder.mProgressBar.setVisibility(View.GONE);
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
                            .resize(SIZE, SIZE)
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
                    if (viewHolder.hPosition == position) {
                        viewHolder.hImageViewProduct.setImageBitmap(bitmap);
                        viewHolder.hImageViewProduct.setVisibility(View.VISIBLE);
                        viewHolder.mProgressBar.setVisibility(View.GONE);
                    }
                }
            }

        }.execute(vh, uri);
    }

    private static class ViewHolder {
        int hPosition;
        ImageView hImageViewProduct;
        ProgressBar mProgressBar;
    }

}
