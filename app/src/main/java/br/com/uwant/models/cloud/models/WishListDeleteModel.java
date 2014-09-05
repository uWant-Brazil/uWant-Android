package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

public class WishListDeleteModel extends AbstractJSONRequestModel {

    private WishList wishList;

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
    }

    public WishList getWishList() {
        return wishList;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Requester.ParameterKey.ID, this.wishList.getId());
        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.DELETE_WISH_LIST;
    }

}
