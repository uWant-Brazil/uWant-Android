package br.com.uwant.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Settings;
import com.facebook.model.OpenGraphAction;
import com.facebook.model.OpenGraphObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.uwant.R;
import br.com.uwant.models.adapters.WishListAdapter;
import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.models.WishListUpdateModel;

/**
 * Classe utilitária responsável por métodos relacionados a classe WishList.
 */
public abstract class WishListUtil {

    private static final DisplayImageOptions OPTIONS = new DisplayImageOptions.Builder()
                                .resetViewBeforeLoading(true)
                                .cacheOnDisk(true)
                                .imageScaleType(ImageScaleType.EXACTLY)
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .considerExifParams(true)
                                .displayer(new FadeInBitmapDisplayer(500))
                                .build();
    public static final String FACEBOOK_ME_PHOTOS = "me/photos";
    public static final String TAG = "WishListUtil";

    /**
     * Método sincronizado responsável por realizar o compartilhamento da lista de desejos nas fotos.
     * @param context
     * @param wishList - Lista de Desejos
     * @param multimedia - Foto a ser compartilhada
     */
    public synchronized static void share(final Context context, WishList wishList, Multimedia multimedia) {
        DebugUtil.info(TAG, "Compartilhando...");
        
        Bundle postParams = new Bundle();
        postParams.putString("name", wishList.getDescription());
        postParams.putParcelable("picture", multimedia.getBitmap());

        final Request.Callback callback = new Request.Callback() {
            
            public void onCompleted(Response response) {
                FacebookRequestError error = response.getError();
                if (error != null) {
                    DebugUtil.error(TAG, "O compartilhamento falhou!");
                    DebugUtil.error(error.getErrorMessage());
                } else {
                    DebugUtil.info(TAG, "Compartilhado!");
                    Toast.makeText(context, "O compartilhamento foi efetuado!", Toast.LENGTH_LONG).show();
                }
            }
            
        };

        Request request = new Request(Session.getActiveSession(), FACEBOOK_ME_PHOTOS, postParams, HttpMethod.POST, callback);
        request.executeAsync();
    }

    /**
     * Método sincronizado responsável por realizar o compartilhamento da lista de desejos em ACTION MODE.
     * @param context
     * @param wishList - Lista de Desejos
     * @param multimedia - Foto
     */
    public synchronized static void shareAction(final Context context, WishList wishList, Multimedia multimedia) {
        DebugUtil.info(TAG, "Compartilhando action...");
        Settings.addLoggingBehavior(LoggingBehavior.REQUESTS);

        OpenGraphObject share = OpenGraphObject.Factory.createForPost("uwant-social:wish");
        share.setProperty("title", wishList.getTitle());
        share.setProperty("description", wishList.getDescription());
        share.setUrl("http://samples.ogp.me/347734502017922");
        share.setPostActionId("me/uwant-social:share");
        share.setCreatedTime(new Date());

        OpenGraphAction oga = OpenGraphAction.Factory.createForPost("uwant-social:share");
        oga.setProperty("wish", share);
        oga.setImageUrls(Arrays.asList(multimedia.getUrl()));
        oga.setExplicitlyShared(true);

        final Request.Callback callback = new Request.Callback() {

            @Override
            public void onCompleted(Response response) {
                FacebookRequestError error = response.getError();
                if (error != null) {
                    DebugUtil.error(TAG, "O compartilhamento falhou!");
                    DebugUtil.error(error.getErrorMessage());
                } else {
                    DebugUtil.info(TAG, "Compartilhado!");
                    Toast.makeText(context, "O compartilhamento foi efetuado!", Toast.LENGTH_LONG).show();
                }
            }

        };
        
        Request objectRequest = Request.newPostOpenGraphActionRequest(Session.getActiveSession(), oga, callback);

        RequestBatch request = new RequestBatch();
        request.add(objectRequest);
        request.executeAsync();
    }

    /**
     * Método responsável por renderizar os produtos no GridLayout da lista de dejesos na tela de perfil.
     * @param context
     * @param products
     * @param gridLayout
     */
    public static void renderProducts(Context context, List<Product> products, GridLayout gridLayout) {
        if (products == null)
            return;

        List<Product> productsWithPictures = new ArrayList<Product>(products.size() + 5);
        for (Product product : products) {
            Multimedia multimedia = product.getPicture();
            if (multimedia != null) {
                Bitmap bitmap = multimedia.getBitmap();
                if (bitmap != null) {
                    productsWithPictures.add(product);
                }
            }
        }

        if (productsWithPictures.size() == 0)
            return;

        List<Integer> indexes = new ArrayList<Integer>(productsWithPictures.size() + 5);

        int x = 2;
        int y = 2;

        for (int i = 0;i < productsWithPictures.size();i++) {
            indexes.add(i);
        }

        for (int i = 0;i < x;i++) {
            for (int j = 0;j < y;j++) {
                if(indexes.size() == 0)
                    continue;

                int randomIndex = (int)(Math.random() * indexes.size());
                int index = indexes.get(randomIndex);
                indexes.remove(randomIndex);

                Product product = productsWithPictures.get(index);
                Multimedia multimedia = product.getPicture();

                int id;
                if (i == 0) {
                    if (j == 0) {
                        id = R.id.adapter_wishlist_imageView1;
                    } else {
                       id = R.id.adapter_wishlist_imageView2;
                    }
                } else {
                    if (j == 0) {
                        id = R.id.adapter_wishlist_imageView3;
                    } else {
                        id = R.id.adapter_wishlist_imageView4;
                    }
                }

                final ImageView imageView = (ImageView) gridLayout.findViewById(id);
                imageView.setImageBitmap(multimedia.getBitmap());
            }
        }
    }

    /**
     * Job para carregar as fotos dos produtos assíncronamente.
     * @param resources
     * @param adapter
     * @param wishList
     */
    public static void loadPicturesFromProducts(Resources resources, WishListAdapter adapter, WishList wishList) {
        if (wishList.getProducts() != null) {
            for (Product product : wishList.getProducts()) {
                Multimedia picture = product.getPicture();
                if (picture != null) {
                    AsyncPicture task = new AsyncPicture(resources, adapter);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, product);
                }
            }
        }
    }

    /**
     * Task assíncrona para carregar as fotos dos produtos da lista de desejos.
     */
    private static class AsyncPicture extends AsyncTask<Product, Void, Bitmap> {

        private final int mWidth;
        private final int mHeight;
        private WishListAdapter mAdapter;
        private Product mProduct;
        private Resources mResources;

        public AsyncPicture(Resources resources, WishListAdapter adapter) {
            this.mResources = resources;
            this.mAdapter = adapter;

            mWidth = (int) (65 * resources.getDisplayMetrics().density);
            mHeight = (int) (80 * resources.getDisplayMetrics().density);
        }

        @Override
        protected Bitmap doInBackground(Product... products) {
            this.mProduct = products[0];
            Multimedia multimedia = this.mProduct.getPicture();
            String url = multimedia.getUrl();

            ImageLoader imageLoader = ImageLoader.getInstance();
            Bitmap bitmap = imageLoader.loadImageSync(url, new ImageSize(mWidth, mHeight), OPTIONS);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Multimedia multimedia = mProduct.getPicture();
            if (bitmap != null) {
                multimedia.setBitmap(bitmap);
            } else {
                multimedia.setBitmap(BitmapFactory.decodeResource(this.mResources, R.drawable.ic_perfil_semfoto));
            }

            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Método responsável por transformar a lista de produtos no formato JSON.
     * @param type
     * @param products
     * @return JSON
     */
    public static JsonArray listProductsToJson(WishListUpdateModel.Type type, List<Product> products){
        JsonArray arrayProducts = new JsonArray();

        if (products != null) {
            for (Product product : products) {
                JsonObject jsonProduct = new JsonObject();

                switch (type){
                    case DELETE:
                        jsonProduct.addProperty(Requester.ParameterKey.ID, product.getId());
                        break;
                    case INSERT:
                        Manufacturer manufacturer = product.getManufacturer();
                        JsonObject jsonManufacturer = new JsonObject();
                        jsonManufacturer.addProperty(Requester.ParameterKey.NAME, manufacturer.getName());

                        jsonProduct.addProperty(Requester.ParameterKey.NAME, product.getName());
                        jsonProduct.addProperty(Requester.ParameterKey.NICK_NAME, product.getName());
                        jsonProduct.add(Requester.ParameterKey.MANUFACTURER, jsonManufacturer);
                }
                arrayProducts.add(jsonProduct);
            }

        }

        return arrayProducts;
    }

}
