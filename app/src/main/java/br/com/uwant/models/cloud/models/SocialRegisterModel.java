package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.classes.SocialProvider;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.RequestModel;
import br.com.uwant.models.cloud.Requester;

public class SocialRegisterModel extends RequestModel {

    public static final String EXTRA = "extra_social_model";

    private String login;
    private String token;
    private SocialProvider provider;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SocialProvider getProvider() {
        return provider;
    }

    public void setProvider(SocialProvider provider) {
        this.provider = provider;
    }

    @Override
    protected JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Requester.ParameterKey.LOGIN, this.login);
        jsonObject.addProperty(Requester.ParameterKey.TOKEN, this.token);
        jsonObject.addProperty(Requester.ParameterKey.SOCIAL_PROVIDER, this.provider.ordinal());
        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.SOCIAL_REGISTER;
    }

}
