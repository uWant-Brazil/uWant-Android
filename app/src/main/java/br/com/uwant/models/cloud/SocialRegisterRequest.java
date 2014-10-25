package br.com.uwant.models.cloud;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.ParseException;
import java.util.Date;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.SocialRegisterModel;
import br.com.uwant.utils.DateUtil;

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

                if (jsonUser.has(Requester.ParameterKey.ID)) {
                    long id = jsonUser.get(Requester.ParameterKey.ID).getAsLong();
                    user.setId(id);
                }

                if (jsonUser.has(Requester.ParameterKey.LOGIN)) {
                    String login = jsonUser.get(Requester.ParameterKey.LOGIN).getAsString();
                    user.setLogin(login);
                }

                if (jsonUser.has(Requester.ParameterKey.NAME)) {
                    String name = jsonUser.get(Requester.ParameterKey.NAME).getAsString();
                    user.setName(name);
                }

                if (jsonUser.has(Requester.ParameterKey.BIRTHDAY)) {
                    String birthdayStr = jsonUser.get(Requester.ParameterKey.BIRTHDAY).getAsString();
                    Date birthday = null;
                    try {
                        birthday = DateUtil.parse(birthdayStr, DateUtil.DATE_PATTERN);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        birthday = new Date();
                    }
                    user.setBirthday(birthday);
                }

                if (jsonUser.has(Requester.ParameterKey.GENDER)) {
                    String genderStr = jsonUser.get(Requester.ParameterKey.GENDER).getAsString();
                    Person.Gender gender = Person.Gender.valueOf(genderStr);
                    user.setGender(gender);
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
