package br.com.uwant.models.cloud.models;

import android.net.Uri;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.util.Calendar;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.AbstractMultipartDataModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.helpers.UWFileBody;
import br.com.uwant.models.cloud.helpers.UWFileBodyListener;

public class WishListProductPictureModel extends AbstractMultipartDataModel {

    private UWFileBodyListener listener;
    private WishList wishList;
    private Product product;

    public void setListener(UWFileBodyListener listener) {
        this.listener = listener;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.WISH_LIST_PRODUCT_PICTURE;
    }

    @Override
    protected MultipartEntityBuilder toMultipartData() {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        long productId = product.getId();
        long wishListId = wishList.getId();
        Multimedia picture = product.getPicture();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart(Requester.ParameterKey.MULTIMEDIA_PRODUCT,
                new StringBody(String.valueOf(productId), ContentType.APPLICATION_FORM_URLENCODED));
        builder.addPart(Requester.ParameterKey.WISHLIST_ID,
                new StringBody(String.valueOf(wishListId), ContentType.APPLICATION_FORM_URLENCODED));
        builder.addPart(Requester.ParameterKey.DESCRIPTION,
                new StringBody(String.valueOf("DEFAULT#DESCRIPTION"), ContentType.APPLICATION_FORM_URLENCODED));
        builder.addPart(Requester.ParameterKey.MULTIMEDIA, new UWFileBody(new File(picture.getUri().getPath()), ContentType.MULTIPART_FORM_DATA, now + "-P-" + productId + ".jpg", listener));

        return builder;
    }

}
