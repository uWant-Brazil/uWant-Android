package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.classes.Action;
import br.com.uwant.models.cloud.JSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

/**
 * Model para envio dos parâmetros da requisição de obtenção das listas de desejos do usuário.
 */
public class ShareModelAbstract extends JSONRequestModel {

    private Action action;

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(Requester.ParameterKey.ACTION_ID, this.action.getId());
        return json;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.ACTION_SHARE;
    }

}
