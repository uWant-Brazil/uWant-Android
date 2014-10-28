package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

/**
 * Model para envio dos parâmetros da requisição de obtenção dos usuários.
 */
public class UserUpdateModel extends AbstractJSONRequestModel {

    private String mail;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(Requester.ParameterKey.MAIL, this.mail);
        return json;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.USER_UPDATE;
    }

}
