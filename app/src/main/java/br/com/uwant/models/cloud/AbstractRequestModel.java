package br.com.uwant.models.cloud;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;

public abstract class AbstractRequestModel<R> implements Serializable {

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
    protected abstract R getRequestBody();

}
