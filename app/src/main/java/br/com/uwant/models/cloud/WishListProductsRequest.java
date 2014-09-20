package br.com.uwant.models.cloud;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.cloud.models.WishListProductsModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class WishListProductsRequest extends AbstractRequest<List<Product>> implements IRequest<WishListProductsModel, List<Product>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/wishlist/product/list";

    private WishListProductsModel mModel;

    public WishListProductsRequest() {
        super();
    }

    public WishListProductsRequest(Context context) {
        super(context);
    }

    @Override
    public void executeAsync(WishListProductsModel data, OnRequestListener listener) {
        this.mModel = data;
        execute(data, listener);
    }

    @Override
    public Class<WishListProductsModel> getDataClass() {
        return WishListProductsModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected List<Product> parse(String response) {
        List<Product> products = new ArrayList<Product>();

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.PRODUCTS)) {
                JsonElement jsonObjElement = jsonObject.get(Requester.ParameterKey.PRODUCTS);
                if (jsonObjElement.isJsonArray()) {
                    JsonArray arrayProducts = jsonObjElement.getAsJsonArray();
                    for (int i = 0;i < arrayProducts.size();i++) {
                        JsonObject jsonProduct = arrayProducts.get(i).getAsJsonObject();

                        if (jsonProduct.has(Requester.ParameterKey.ID)
                                && jsonProduct.has(Requester.ParameterKey.NAME)) {
                            long id = jsonProduct.get(Requester.ParameterKey.ID).getAsInt();
                            String name = jsonProduct.get(Requester.ParameterKey.NAME).getAsString();

                            Product product = new Product();
                            product.setId(id);
                            product.setName(name);
                            product.setWishListId(mModel.getWishList().getId());

                            if (jsonProduct.has(Requester.ParameterKey.NICK_NAME)) {
                                JsonElement jsonNickElem = jsonProduct.get(Requester.ParameterKey.NICK_NAME);
                                if (!jsonNickElem.isJsonNull()) {
                                    String nickName = jsonNickElem.getAsString();
                                    product.setNickName(nickName);
                                }
                            }

                            if (jsonProduct.has(Requester.ParameterKey.PICTURE)) {
                                JsonElement jsonPicElem = jsonProduct.get(Requester.ParameterKey.PICTURE);
                                if (!jsonPicElem.isJsonNull() && jsonPicElem.isJsonObject()) {
                                    JsonObject jsonProductPic = jsonPicElem.getAsJsonObject();
                                    if (jsonProductPic.has(Requester.ParameterKey.URL)) {
                                        String url = jsonProductPic.get(Requester.ParameterKey.URL).getAsString();

                                        Multimedia picture = new Multimedia();
                                        picture.setUrl(url);
                                        product.setPicture(picture);
                                    }
                                }
                            }

//                            wlpdb.createOrUpdate(product);

                            if (jsonProduct.has(Requester.ParameterKey.MANUFACTURER)) {
                                JsonElement jsonManuElem = jsonProduct.get(Requester.ParameterKey.MANUFACTURER);
                                if (!jsonManuElem.isJsonNull() && jsonManuElem.isJsonObject()) {
                                    JsonObject jsonManufacturer = jsonManuElem.getAsJsonObject();
                                    if (jsonManufacturer.has(Requester.ParameterKey.ID)
                                            && jsonManufacturer.has(Requester.ParameterKey.NAME)) {
                                        long manufacturerId = jsonManufacturer.get(Requester.ParameterKey.ID).getAsInt();
                                        String manufacturerName = jsonManufacturer.get(Requester.ParameterKey.NAME).getAsString();

                                        Manufacturer manufacturer = new Manufacturer();
                                        manufacturer.setId(manufacturerId);
                                        manufacturer.setProductId(product.getId());
                                        manufacturer.setName(manufacturerName);
                                        product.setManufacturer(manufacturer);
                                    }
                                }
                            }

                            products.add(product);
                        }
                    }
                }
            }
        }

        return products;
    }

    @Override
    protected List<Product> debugParse() {
        return null;
    }
}
