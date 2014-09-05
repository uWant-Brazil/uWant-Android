package br.com.uwant.models.cloud.models;

import java.util.List;

import br.com.uwant.models.classes.Product;
import br.com.uwant.models.cloud.AbstractMultipartDataModel;
import br.com.uwant.models.cloud.IRequest;

public class WishListProductPictureModel extends AbstractMultipartDataModel {

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
