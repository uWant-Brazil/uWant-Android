package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.JSONRequestModel;
import br.com.uwant.models.cloud.IRequest;

/**
 * Model para envio dos parâmetros da requisição de obtenção das listas de desejos do usuário.
 */
public class ExcludeAccountModelAbstract extends JSONRequestModel {

    @Override
    protected JsonObject toJson() {
        return null;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.EXCLUDE_ACCOUNT;
    }

}
