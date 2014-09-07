package br.com.uwant.models.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.RegisterModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class RegisterRequest extends AbstractRequest<User> implements IRequest<RegisterModel, User> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/register";

    private RegisterModel mModel;

    @Override
    public void executeAsync(RegisterModel data, OnRequestListener listener) {
        this.mModel = data;
        execute(data, listener);
    }

    @Override
    public Class<RegisterModel> getDataClass() {
        return RegisterModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected User parse(String response) {
        User user = this.mModel.getUser();
        
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.USER)) {
                JsonObject jsonUser = jsonObject.getAsJsonObject(Requester.ParameterKey.USER);

                if (jsonUser.has(Requester.ParameterKey.ID)) {
                    long id = jsonUser.get(Requester.ParameterKey.ID).getAsLong();
                    user.setId(id);
                }
            }
        }

        return user;
    }

    @Override
    protected User debugParse() {
        return parse(null);
    }
}
