package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.RequestModel;
import br.com.uwant.models.cloud.Requester;

/**
 * Model para envio dos parâmetros da requisição de obtenção das listas de desejos do usuário.
 */
public class WantModel extends RequestModel {

    private long actionId;

    public void setActionId(long actionId) {
        this.actionId = actionId;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(Requester.ParameterKey.ACTION_ID, this.actionId);
        return json;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.ACTION_WANT;
    }

}
