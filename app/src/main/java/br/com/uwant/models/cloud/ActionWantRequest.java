package br.com.uwant.models.cloud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.WishList;
import br.com.uwant.models.cloud.models.WantModel;
import br.com.uwant.models.cloud.models.WishListModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ActionWantRequest extends AbstractRequest<Boolean> implements IRequest<WantModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/want";

    @Override
    public void executeAsync(WantModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<WantModel> getDataClass() {
        return WantModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Boolean parse(String response) {
        return true;
    }
}
