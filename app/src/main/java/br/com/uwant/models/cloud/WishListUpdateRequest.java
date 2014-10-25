package br.com.uwant.models.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.cloud.models.WishListCreateModel;
import br.com.uwant.models.cloud.models.WishListUpdateModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class WishListUpdateRequest extends AbstractRequest<List<Product>> implements IRequest<WishListUpdateModel, List<Product>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/wishlist/update";

    private WishListUpdateModel mModel;

    @Override
    public void executeAsync(WishListUpdateModel data, OnRequestListener listener) {
        this.mModel = data;
        execute(data, listener);
    }

    @Override
    public Class<WishListUpdateModel> getDataClass() {
        return WishListUpdateModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected List<Product> parse(String response) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        List<Product> produtosInseridos = mModel.getmUpdateProducts().get(WishListUpdateModel.Type.INSERT);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.PRODUCTS)) {
                JsonElement jsonWishLists = jsonObject.get(Requester.ParameterKey.PRODUCTS);
                if (jsonWishLists.isJsonObject()) {
                    JsonObject arrayWishLists = jsonWishLists.getAsJsonObject();
                    for (int i = 0;i < produtosInseridos.size();i++) {
                        if (arrayWishLists.has(String.valueOf(i))) {
                            long productId = arrayWishLists.get(String.valueOf(i)).getAsLong();
                            Product product = produtosInseridos.get(i);
                            product.setId(productId);
                        }
                    }
                }
            }
        }

        return produtosInseridos;
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

        return null;
    }
}
