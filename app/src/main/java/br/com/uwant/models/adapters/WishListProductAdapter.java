package br.com.uwant.models.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.utils.PictureUtil;

public class WishListProductAdapter extends BaseAdapter implements View.OnClickListener {

    private final Context mContext;
    private final DisplayImageOptions mOptions;
    private final ImageSize mTargetSize;
    private List<Product> mProducts;

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
        this.mTargetSize = new ImageSize(300, 300);
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
            holder.hButtonRemove.setOnClickListener(this);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.hButtonRemove.setTag(i);
        Product product = getItem(i);
        Multimedia picture = product.getPicture();
        Uri uri = picture.getUri();
        if (uri == null) {
            String url = picture.getUrl();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(uri.toString(), this.mOptions, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    holder.hImageViewProduct.setImageResource(R.drawable.ic_semfoto);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    holder.hImageViewProduct.setImageResource(R.drawable.ic_semfoto);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                    bitmap = PictureUtil.cropToFit(bitmap);
                    bitmap = PictureUtil.scale(bitmap, holder.hImageViewProduct);
                    bitmap = PictureUtil.circle(bitmap);
                    holder.hImageViewProduct.setImageBitmap(bitmap);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    holder.hImageViewProduct.setImageResource(R.drawable.ic_semfoto);
                }

            });
        } else {
            Picasso.with(this.mContext).load(uri).into(holder.hImageViewProduct);
        }

        return view;
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
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder {
        ImageView hImageViewProduct;
        ImageButton hButtonRemove;
    }

}
