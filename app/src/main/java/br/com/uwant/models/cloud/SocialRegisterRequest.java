package br.com.uwant.models.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.SocialRegisterModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class SocialRegisterRequest extends AbstractRequest<Boolean> implements IRequest<SocialRegisterModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/social/signUp";

    @Override
    public void executeAsync(SocialRegisterModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<SocialRegisterModel> getDataClass() {
        return SocialRegisterModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Boolean parse(String response) {
        User user = User.getInstance();

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(response);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            if (jsonObject.has(Requester.ParameterKey.USER)) {
                JsonObject jsonUser = jsonObject.getAsJsonObject(Requester.ParameterKey.USER);
                if (jsonUser.has(Requester.ParameterKey.LOGIN)) {
                    String login = jsonUser.get(Requester.ParameterKey.LOGIN).getAsString();
                    user.setLogin(login);
                }

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

            if (jsonObject.has(Requester.ParameterKey.REGISTERED)) {
                return jsonObject.get(Requester.ParameterKey.REGISTERED).getAsBoolean();
            }
        }

        return null;
    }

    @Override
    protected Boolean debugParse() {
        Multimedia picture = new Multimedia();
        picture.setUrl("http://dailysignal.com/wp-content/uploads/armstrong.jpg");

        User user = User.getInstance();
        user.setLogin("debug_mode");
        user.setName("Debug Mode # ON");
        user.setPicture(picture);

        return true;
    }
}
