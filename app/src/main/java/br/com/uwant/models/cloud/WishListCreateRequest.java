package br.com.uwant.models.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.cloud.models.WishListCreateModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class WishListCreateRequest extends AbstractRequest<List<Product>> implements IRequest<WishListCreateModel, List<Product>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/wishlist/create";

    private WishListCreateModel mModel;

    @Override
    public void executeAsync(WishListCreateModel data, OnRequestListener listener) {
        this.mModel = data;
        execute(data, listener);
    }

    @Override
    public Class<WishListCreateModel> getDataClass() {
        return WishListCreateModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected List<Product> parse(String response) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.WISHLIST_ID)) {
                long id = jsonObject.get(Requester.ParameterKey.WISHLIST_ID).getAsLong();
                mModel.getWishList().setId(id);
                if (jsonObject.has(Requester.ParameterKey.PRODUCTS)) {
                    JsonElement jsonWishLists = jsonObject.get(Requester.ParameterKey.PRODUCTS);
                    if (jsonWishLists.isJsonObject()) {
                        JsonObject arrayWishLists = jsonWishLists.getAsJsonObject();
                        for (int i = 0; i < mModel.getProducts().size(); i++) {
                            if (arrayWishLists.has(String.valueOf(i))) {
                                long productId = arrayWishLists.get(String.valueOf(i)).getAsLong();
                                Product product = mModel.getProducts().get(i);
                                product.setId(productId);
                            }
                        }
                    }
                }
            }
        }

        return mModel.getProducts();
    }

    @Override
    protected List<Product> debugParse() {
        int productsSize = (int)(Math.random() * 15);
        List<Product> products = new ArrayList<Product>(productsSize + 5);
        for (int i = 0;i < productsSize;i++) {
            Manufacturer manufacturer = new Manufacturer();
            manufacturer.setId(i);
            manufacturer.setName("Manufacturer # " + i);

            Product product = new Product();
            product.setId(i);
            product.setName("Product # " + i);
            product.setNickName("Product NickName # " + i);
            product.setManufacturer(manufacturer);
            products.add(product);
        }

        return products;
    }
}
