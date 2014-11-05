package br.com.uwant.models.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.flow.WishListActivity;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.utils.PictureUtil;

public class WishListProductAdapter extends BaseAdapter implements View.OnClickListener {

    private static final int SIZE = 300;
    private final Context mContext;
    private final DisplayImageOptions mOptions;
    private final ImageSize mTargetSize;
    private List<Product> mProducts;
    private WishListActivity.OnProductListener onProductListener;

    public WishListProductAdapter(Context context, List<Product> products) {
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

    public WishListProductAdapter(Context context, List<Product> products, WishListActivity.OnProductListener onProductListener) {
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
        this.onProductListener = onProductListener;
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
            view = LayoutInflater.from(this.mContext).inflate(R.layout.adapter_wish_list_product, viewGroup, false);
            holder.hImageViewProduct = (ImageView) view.findViewById(R.id.adapter_wishlistProduct_imageView);
            holder.hButtonRemove = (ImageButton) view.findViewById(R.id.adapter_wishlistProduct_button_remove);
            holder.mProgressBar = (ProgressBar) view.findViewById(R.id.adapter_wishlist_product_loading);
            holder.hButtonRemove.setOnClickListener(this);
            holder.hPosition = i;
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.hButtonRemove.setTag(i);
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

                return ImageLoader.getInstance().loadImageSync(url, mOptions);
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
                    if (viewHolder.hPosition == position) {
                        viewHolder.hImageViewProduct.setImageBitmap(bitmap);
                        viewHolder.hImageViewProduct.setVisibility(View.VISIBLE);
                        viewHolder.mProgressBar.setVisibility(View.GONE);
                    }
                }
            }

        }.execute(vh, uri);
    }


    @Override
    public void onClick(View view) {
        Integer position = (Integer) view.getTag();
        Product product = null;
        if (position != null) {
            product = getItem(position);
        }

        switch (view.getId()) {
            case R.id.adapter_wishlistProduct_button_remove:
                remove(product);
                break;

            default:
                break;
        }
    }

    private void remove(Product product) {
        if (product != null) {
            mProducts.remove(product);

            if (onProductListener != null && product.getId() > 0) {
                onProductListener.onRemove(product);
            }

            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        int hPosition;
        ImageView hImageViewProduct;
        ImageButton hButtonRemove;
        ProgressBar mProgressBar;
    }

}
