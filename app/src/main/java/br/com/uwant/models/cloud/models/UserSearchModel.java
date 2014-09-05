package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

/**
 * Model para envio dos parâmetros da requisição de obtenção dos usuários.
 */
public class UserSearchModel extends AbstractJSONRequestModel {

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(Requester.ParameterKey.QUERY, this.query);
        return json;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.USER_SEARCH;
    }

}
