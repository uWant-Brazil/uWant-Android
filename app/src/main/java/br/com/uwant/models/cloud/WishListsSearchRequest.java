package br.com.uwant.models.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.models.WishListsSearchModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class WishListsSearchRequest extends AbstractRequest<List<WishList>> implements IRequest<WishListsSearchModel, List<WishList>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/manufacturer/list";

    @Override
    public void executeAsync(WishListsSearchModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<WishListsSearchModel> getDataClass() {
        return WishListsSearchModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected List<WishList> parse(String response) {
        List<WishList> wishLists = new ArrayList<WishList>();

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.WISHLISTS)) {
                JsonElement jsonManufacturers = jsonObject.get(Requester.ParameterKey.WISHLISTS);
                if (jsonManufacturers.isJsonArray()) {
                    JsonArray arrayManufacturers = jsonManufacturers.getAsJsonArray();
                    for (int i = 0; i < arrayManufacturers.size(); i++) {
                        JsonElement jsonArray = arrayManufacturers.get(i);
                        if (jsonArray.isJsonObject()) {
                            JsonObject object = jsonArray.getAsJsonObject();
                            if (object.has(Requester.ParameterKey.ID)
                                    && object.has(Requester.ParameterKey.UUID)
                                    && object.has(Requester.ParameterKey.TITLE)) {
                                long id = object.get(Requester.ParameterKey.ID).getAsLong();
                                String uuid = object.get(Requester.ParameterKey.UUID).getAsString();
                                String title = object.get(Requester.ParameterKey.TITLE).getAsString();

                                WishList w = new WishList(id, title);
                                wishLists.add(w);
                            }
                        }
                    }
                }
            }
        }
        return wishLists;
    }

    @Override
    protected List<WishList> debugParse() {
        List<WishList> wishLists = new ArrayList<WishList>();
        return wishLists;
    }
}
