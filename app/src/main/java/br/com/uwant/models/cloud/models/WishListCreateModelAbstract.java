package br.com.uwant.models.cloud.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import br.com.uwant.models.classes.Manufacturer;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.JSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

public class WishListCreateModelAbstract extends JSONRequestModel {

    private WishList wishList;
    private List<Product> products;

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    @Override
    protected JsonObject toJson() {
        JsonArray arrayProducts = new JsonArray();
        for (Product product : this.products) {
            Manufacturer manufacturer = product.getManufacturer();
            JsonObject jsonManufacturer = new JsonObject();
            jsonManufacturer.addProperty(Requester.ParameterKey.NAME, manufacturer.getName());

            JsonObject jsonProduct = new JsonObject();
            jsonProduct.addProperty(Requester.ParameterKey.NAME, product.getName());
            jsonProduct.addProperty(Requester.ParameterKey.NICK_NAME, product.getName());
            jsonProduct.add(Requester.ParameterKey.MANUFACTURER, jsonManufacturer);

            arrayProducts.add(jsonProduct);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Requester.ParameterKey.TITLE, this.wishList.getTitle());
        jsonObject.addProperty(Requester.ParameterKey.DESCRIPTION, this.wishList.getDescription());
        jsonObject.add(Requester.ParameterKey.PRODUCTS, arrayProducts);
        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.CREATE_WISH_LIST;
    }

}
