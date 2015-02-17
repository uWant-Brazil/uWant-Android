package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

public class ManufacturersModel extends AbstractJSONRequestModel {

    private String manufacturerName;

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty(Requester.ParameterKey.MANUFACTURER, this.manufacturerName);
        return json;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.MANUFACTURER_LIST;
    }
}
