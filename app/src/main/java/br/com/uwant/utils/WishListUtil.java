package br.com.uwant.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;

public abstract class WishListUtil {

    private static final DisplayImageOptions OPTIONS = new DisplayImageOptions.Builder()
                                .resetViewBeforeLoading(true)
                                .cacheOnDisk(true)
                                .imageScaleType(ImageScaleType.EXACTLY)
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .considerExifParams(true)
                                .displayer(new FadeInBitmapDisplayer(500))
                                .build();

    public static void renderProducts(Context context, List<Product> products, GridLayout gridLayout) {
        if (products == null)
            return;

        List<Product> productsWithPictures = new ArrayList<Product>(products.size() + 5);
        for (Product product : products) {
            Multimedia multimedia = product.getPicture();
            if (multimedia != null) {
                String url = multimedia.getUrl();
                if (url != null && !url.isEmpty()) {
                    productsWithPictures.add(product);
                }
            }
        }

        if (productsWithPictures.size() == 0)
            return;

        int sizePerFour = productsWithPictures.size() > 4 ? 4 : productsWithPictures.size();
        List<Integer> indexes = new ArrayList<Integer>(productsWithPictures.size() + 5);

        int x = 2;
        int y = 2;
        gridLayout.setColumnCount(2);
        gridLayout.setRowCount(2);

        for (int i = 0;i < products.size();i++) {
            indexes.add(i);
        }

        for (int i = 0;i < x;i++) {
            for (int j = 0;j < y;j++) {
                if(indexes.size() == 0)
                    continue;

                int randomIndex = ((int)Math.random() * indexes.size());
                int index = indexes.get(randomIndex);
                indexes.remove(randomIndex);

                Product product = products.get(index);
                Multimedia multimedia = product.getPicture();
                String url = multimedia.getUrl();

                int id;
                switch (i) {
                    case 0:
                       id = R.id.adapter_wishlist_imageView1;
                        break;

                    case 1:
                        id = R.id.adapter_wishlist_imageView2;
                        break;

                    case 2:
                        id = R.id.adapter_wishlist_imageView3;
                        break;

                    default:
                        id = R.id.adapter_wishlist_imageView4;
                        break;
                }

                final ImageView imageView = (ImageView) gridLayout.findViewById(id);

                ImageLoader imageLoader = ImageLoader.getInstance();
                Bitmap bitmap = imageLoader.loadImageSync(url, OPTIONS);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
