package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.RequestModel;

/**
 * Model para envio dos parâmetros da requisição de obtenção dos usuários.
 */
public class UserSearchModel extends RequestModel {

    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    protected JsonObject toJson() {
        return null;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.USER_SEARCH;
    }

}
