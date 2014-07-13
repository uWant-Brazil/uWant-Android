package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.RequestModel;
import br.com.uwant.models.cloud.Requester;

public class RegisterModel extends RequestModel {

    private String login;
    private String password;
    private String name;
    private String mail;
    private String birthday;
    private Person.Gender gender;

    private SocialRegisterModel socialModel;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Person.Gender getGender() {
        return gender;
    }

    public void setGender(Person.Gender gender) {
        this.gender = gender;
    }

    public SocialRegisterModel getSocialModel() {
        return socialModel;
    }

    public void setSocialModel(SocialRegisterModel socialModel) {
        this.socialModel = socialModel;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Requester.ParameterKey.LOGIN, this.login);
        jsonObject.addProperty(Requester.ParameterKey.PASSWORD, this.password);
        jsonObject.addProperty(Requester.ParameterKey.FULL_NAME, this.name);
        jsonObject.addProperty(Requester.ParameterKey.MAIL, this.mail);
        jsonObject.addProperty(Requester.ParameterKey.BIRTHDAY, this.birthday);
        jsonObject.addProperty(Requester.ParameterKey.GENDER, this.gender.ordinal());

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
