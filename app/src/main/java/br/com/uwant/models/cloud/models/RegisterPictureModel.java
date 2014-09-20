package br.com.uwant.models.cloud.models;

import android.net.Uri;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.util.Calendar;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.AbstractMultipartDataModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

public class RegisterPictureModel extends AbstractMultipartDataModel {

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.USER_PICTURE;
    }

    @Override
    protected MultipartEntityBuilder toMultipartData() {
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();
        Multimedia picture = user.getPicture();

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart(Requester.ParameterKey.MULTIMEDIA_USER_PICTURE,
                new StringBody(String.valueOf(user.getId()), ContentType.APPLICATION_FORM_URLENCODED));
        builder.addPart(Requester.ParameterKey.MULTIMEDIA, new FileBody(new File(((Uri)picture.getUri()).getPath()), ContentType.MULTIPART_FORM_DATA, now + "-U-" + user.getId() + ".jpg"));

        return builder;
    }

}
