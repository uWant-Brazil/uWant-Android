package br.com.uwant.models.cloud;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;

public abstract class RequestModel implements Serializable {

    /**
     * Gson para transformar o JsonObject em String.
     * Será utilizado para formar o Body da requisição.
     */
    private static final Gson GSON = new Gson();

    /**
     * Método responsável por obter o JsonObject a partir do RequestModel.
     * @return
     */
    protected abstract JsonObject toJson();

    /**
     * Método responsável por retornar o tipo da requisição no qual o RequestModel se encaixa.
     * Ele será utilizado pelo RequestFactory montar a requisição correta.
     * @return type - tipo da requisição
     */
    protected abstract IRequest.Type getRequestType();

    /**
     * Método responsável por realizar o parse do JsonObject para String.
     * Será utilizado para formar o Body da requisição.
     * @return body
     */
    protected String getRequestBody() {
        return GSON.toJson(toJson());
    }

}
