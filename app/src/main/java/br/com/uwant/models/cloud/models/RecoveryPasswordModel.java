package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

public class RecoveryPasswordModel extends AbstractJSONRequestModel {

    private String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Requester.ParameterKey.MAIL, this.mail);
        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.RECOVERY_PASSWORD;
    }

}
