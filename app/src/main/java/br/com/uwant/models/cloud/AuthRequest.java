package br.com.uwant.models.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.AuthModel;

public class AuthRequest extends AbstractRequest<User> implements IRequest<AuthModel, User> {

    private static final String URL = "";

    @Override
    public void executeAsync(AuthModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<AuthModel> getDataClass() {
        return AuthModel.class;
    }

    @Override
    protected String getURL() {
        return URL;
    }

    @Override
    protected User parse(String response) {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);

        return null;
    }
}
