package br.com.uwant.models.cloud.models;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.util.Calendar;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Product;
import br.com.uwant.models.cloud.AbstractMultipartDataModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.helpers.UWFileBody;
import br.com.uwant.models.cloud.helpers.UWFileBodyListener;

public class WishListProductPictureModel extends AbstractMultipartDataModel {

    private UWFileBodyListener listener;
    private Product product;

    public void setListener(UWFileBodyListener listener) {
        this.listener = listener;
    }

    public void setProduct(Product product) {
        this.product = product;
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
        Multimedia picture = product.getPicture();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart(Requester.ParameterKey.MULTIMEDIA_PRODUCT,
                new StringBody(String.valueOf(productId), ContentType.APPLICATION_FORM_URLENCODED));
        builder.addPart(Requester.ParameterKey.MULTIMEDIA, new UWFileBody(new File(picture.getUri().getPath()), ContentType.MULTIPART_FORM_DATA, now + "-P-" + productId + ".jpg", listener));

        return builder;
    }

}
