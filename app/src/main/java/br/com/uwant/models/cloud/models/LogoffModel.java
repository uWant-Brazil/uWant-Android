package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.RequestModel;

/**
 * Model para envio dos parâmetros da requisição de obtenção das listas de desejos do usuário.
 */
public class LogoffModel extends RequestModel {

    @Override
    protected JsonObject toJson() {
        return null;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.LOGOFF;
    }

}
