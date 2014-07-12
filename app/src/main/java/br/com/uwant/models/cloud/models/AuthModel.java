package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.RequestModel;

public class AuthModel extends RequestModel {

    private String login;
    private String password;

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

    @Override
    protected JsonObject toJson() {
        return null;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.AUTH;
    }

}
