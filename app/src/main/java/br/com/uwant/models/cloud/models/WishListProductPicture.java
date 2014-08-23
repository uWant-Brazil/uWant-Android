package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import java.util.List;

import br.com.uwant.models.classes.Product;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.MultipartDataModelAbstract;

public class WishListProductPicture extends MultipartDataModelAbstract {

    private List<Product> products;

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.WISH_LIST_PRODUCT_PICTURE;
    }

    @Override
    protected void toMultipartData() {
    }

}
