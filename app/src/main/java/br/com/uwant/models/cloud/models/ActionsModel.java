package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.Requester;

/**
 * Model para envio dos parâmetros da requisição de autenticação.
 */
public class ActionsModel extends AbstractJSONRequestModel {

    private int startIndex;
    private int endIndex;

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Requester.ParameterKey.START_INDEX, this.startIndex);
        jsonObject.addProperty(Requester.ParameterKey.END_INDEX, this.endIndex);
        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.ACTIONS;
    }

}
