package br.com.uwant.models.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.AuthModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class AuthRequest extends AbstractRequest<User> implements IRequest<AuthModel, User> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/authorize";

    /**
     * Model que será enviado.
     */
    private AuthModel mModel;

    @Override
    public void executeAsync(AuthModel data, OnRequestListener listener) {
        this.mModel = data;
        execute(data, listener);
    }

    @Override
    public Class<AuthModel> getDataClass() {
        return AuthModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected User parse(String response) {
        User user = User.getInstance();
        user.setLogin(mModel.getLogin());

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Requester.ParameterKey.USER)) {
                JsonObject jsonUser = jsonObject.getAsJsonObject(Requester.ParameterKey.USER);
                if (jsonUser.has(Requester.ParameterKey.NAME)) {
                    String name = jsonUser.get(Requester.ParameterKey.NAME).getAsString();
                    user.setName(name);
                }

                if (jsonUser.has(Requester.ParameterKey.PICTURE)) {
                    JsonElement jsonElementPicture = jsonUser.get(Requester.ParameterKey.PICTURE);
                    if (!jsonElementPicture.isJsonNull()) {
                        JsonObject jsonPicture = jsonElementPicture.getAsJsonObject();
                        if (jsonPicture.has(Requester.ParameterKey.URL)) {
                            String url = jsonPicture.get(Requester.ParameterKey.URL).getAsString();

                            Multimedia picture = new Multimedia();
                            picture.setUrl(url);

                            user.setPicture(picture);
                        }
                    }
                }
            }
        }

        return user;
    }

    @Override
    protected User debugParse() {
        Multimedia picture = new Multimedia();
        picture.setUrl("http://dailysignal.com/wp-content/uploads/armstrong.jpg");

        User user = User.getInstance();
        user.setLogin(mModel.getLogin());
        user.setName("Debug Mode # ON");
        user.setPicture(picture);

        return user;
    }
}
