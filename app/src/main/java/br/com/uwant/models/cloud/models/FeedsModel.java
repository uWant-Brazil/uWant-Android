package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

public class FeedsModel extends AbstractJSONRequestModel {

    private int startIndex;
    private int endIndex;
    private WishList wishList;
    private Person person;

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(Requester.ParameterKey.START_INDEX, this.startIndex);
        json.addProperty(Requester.ParameterKey.END_INDEX, this.endIndex);

        if (wishList != null) {
            json.addProperty(Requester.ParameterKey.WISHLIST_ID, wishList.getId());
        } else if (person != null) {
            json.addProperty(Requester.ParameterKey.USER_ID, person.getId());
        }

        return json;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.FEEDS;
    }

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
    }

    public WishList getWishList() {
        return wishList;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}