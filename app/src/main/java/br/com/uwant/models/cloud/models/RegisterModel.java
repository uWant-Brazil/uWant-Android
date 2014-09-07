package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import java.util.Date;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.Requester;

public class RegisterModel extends AbstractJSONRequestModel {

    private User user;
    private String password;
    private String birthday;

    private SocialRegisterModel socialModel;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public SocialRegisterModel getSocialModel() {
        return socialModel;
    }

    public void setSocialModel(SocialRegisterModel socialModel) {
        this.socialModel = socialModel;
    }

    @Override
    protected JsonObject toJson() {
        String login = user.getLogin();
        String name = user.getName();
        String mail = user.getMail();
        Person.Gender gender = user.getGender();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Requester.ParameterKey.LOGIN, login);
        jsonObject.addProperty(Requester.ParameterKey.PASSWORD, this.password);
        jsonObject.addProperty(Requester.ParameterKey.FULL_NAME, name);
        jsonObject.addProperty(Requester.ParameterKey.MAIL, mail);
        jsonObject.addProperty(Requester.ParameterKey.BIRTHDAY, this.birthday);
        jsonObject.addProperty(Requester.ParameterKey.GENDER, gender.ordinal());

        if (this.socialModel != null) {
            JsonObject jsonSocial = new JsonObject();
            jsonSocial.addProperty(Requester.ParameterKey.TOKEN, socialModel.getToken());
            jsonSocial.addProperty(Requester.ParameterKey.LOGIN, socialModel.getLogin());
            jsonSocial.addProperty(Requester.ParameterKey.SOCIAL_PROVIDER, socialModel.getProvider().ordinal());

            jsonObject.add(Requester.ParameterKey.SOCIAL_PROFILE, jsonSocial);
        }

        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.REGISTER;
    }

}
