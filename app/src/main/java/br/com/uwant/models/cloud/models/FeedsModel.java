package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.RequestModel;

public class FeedsModel extends RequestModel {

    @Override
    protected JsonObject toJson() {
        return null;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.ACTIONS;
    }

}