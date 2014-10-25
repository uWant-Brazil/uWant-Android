package br.com.uwant.models.cloud;

import android.content.Context;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.models.WishListModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class WishListRequest extends AbstractRequest<List<WishList>> implements IRequest<WishListModel, List<WishList>> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/wishlist/list";

    public WishListRequest() {
        super();
    }
    
    public WishListRequest(Context context) {
        super(context);
    }

    @Override
    public void executeAsync(WishListModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<WishListModel> getDataClass() {
        return WishListModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected List<WishList> parse(String response) {
        List<WishList> wishLists = new ArrayList<WishList>(5);

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.WISHLIST)) {
                JsonElement jsonWishLists = jsonObject.get(Requester.ParameterKey.WISHLIST);
                if (jsonWishLists.isJsonArray()) {
                    JsonArray arrayWishLists = jsonWishLists.getAsJsonArray();
                    for (int i = 0;i < arrayWishLists.size();i++) {
                        JsonElement jsonWishList = arrayWishLists.get(i);
                        if (jsonWishList.isJsonObject()) {
                            JsonObject jsonWishListObj = jsonWishList.getAsJsonObject();

                            long id = jsonWishListObj.get(Requester.ParameterKey.ID).getAsLong();
                            String title = jsonWishListObj.get(Requester.ParameterKey.TITLE).getAsString();
                            String description = jsonWishListObj.get(Requester.ParameterKey.DESCRIPTION).getAsString();
//                            long modifiedAtMilli = jsonWishListObj.get(Requester.ParameterKey.MODIFIED_AT).getAsLong();

                            WishList wishList = new WishList(id, title, description);
                            wishLists.add(wishList);

                            //wldb.createOrUpdate(wishList);
                        }
                    }
                }
            }
        }

        return wishLists;
    }

    @Override
    protected List<WishList> debugParse() {
        int wishListSize = (int)(Math.random() * 15);
        List<WishList> wishLists = new ArrayList<WishList>(wishListSize + 5);
        for (int i = 0;i < wishListSize;i++) {
            WishList wishList = new WishList(i, "WishList#" + i, (i % 3 == 0 ? "WishList#" + i + " Description" : null));
            wishLists.add(wishList);
        }

        return wishLists;
    }
}
