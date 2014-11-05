package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.classes.SocialProvider;
import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

public class SocialLinkModel extends AbstractJSONRequestModel {

    private String facebookId;
    private String login;
    private String token;
    private SocialProvider provider;

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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
        jsonObject.addProperty(Requester.ParameterKey.TOKEN, this.token);
        jsonObject.addProperty(Requester.ParameterKey.LOGIN, this.login);
        jsonObject.addProperty(Requester.ParameterKey.SOCIAL_PROVIDER, this.provider.ordinal());
        jsonObject.addProperty(Requester.ParameterKey.FACEBOOK_ID, this.facebookId);
        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.SOCIAL_LINK;
    }

    public String getToken() {
        return token;
    }
}
