package br.com.uwant.models.cloud.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

public class WishListUpdateModel extends AbstractJSONRequestModel {

    public static enum Type {INSERT, UPDATE, DELETE};
    private WishList wishList;
    private HashMap<Type,List<Product>> mUpdateProducts;

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
    }

    public Map<Type, List<Product>> getmUpdateProducts() {
        return mUpdateProducts;
    }

    public void setmUpdateProducts(HashMap<Type, List<Product>> mUpdateProducts) {
        this.mUpdateProducts = mUpdateProducts;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Requester.ParameterKey.ID, this.wishList.getId());
        jsonObject.addProperty(Requester.ParameterKey.TITLE, this.wishList.getTitle());
        jsonObject.addProperty(Requester.ParameterKey.DESCRIPTION, this.wishList.getDescription());
        jsonObject.add(Requester.ParameterKey.PRODUCTS, Util.listProductsToJson(Type.INSERT, mUpdateProducts.get(Type.INSERT)));
        jsonObject.add(Requester.ParameterKey.PRODUCTS_REMOVED, Util.listProductsToJson(Type.DELETE, mUpdateProducts.get(Type.DELETE)));
        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.UPDATE_WISH_LIST;
    }

}
