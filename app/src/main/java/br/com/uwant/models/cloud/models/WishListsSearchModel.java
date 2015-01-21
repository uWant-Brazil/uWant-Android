package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

public class WishListsSearchModel extends AbstractJSONRequestModel {

    private String wishListName;

    public void setWishListName(String wishListName) {
        this.wishListName = wishListName;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(Requester.ParameterKey.WISHLIST, this.wishListName);
        return json;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.WISH_LIST_SEARCH;
    }
}
