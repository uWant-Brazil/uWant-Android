package br.com.uwant.models.cloud;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by felipebenezi on 23/08/14.
 */
public abstract class AbstractJSONRequestModel extends AbstractRequestModel<String> {

    /**
     * Gson para transformar o JsonObject em String.
     * Será utilizado para formar o Body da requisição.
     */
    protected static final Gson GSON = new Gson();

    /**
     * Método responsável por obter o JsonObject a partir do RequestModel.
     * @return
     */
    protected abstract JsonObject toJson();

    @Override
    protected IRequest.Type getRequestType() {
        return null;
    }

    @Override
    protected String getRequestBody() {
        JsonObject json = toJson();
        return json != null ? GSON.toJson(json) : null;
    }

}
