package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.RequestModel;
import br.com.uwant.models.cloud.Requester;

public class FeedsModel extends RequestModel {

    private int startIndex;
    private int endIndex;

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(Requester.ParameterKey.START_INDEX, this.startIndex);
        json.addProperty(Requester.ParameterKey.END_INDEX, this.endIndex);
        return json;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.FEEDS;
    }

}