package br.com.uwant.models.cloud;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;

public abstract class RequestModel implements Serializable {

    private static final Gson GSON = new Gson();

    protected abstract JsonObject toJson();
    protected abstract IRequest.Type getRequestType();

    protected String getRequestBody() {
        return GSON.toJson(toJson());
    }

}
