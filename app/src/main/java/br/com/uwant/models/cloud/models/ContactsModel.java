package br.com.uwant.models.cloud.models;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.RequestModel;
import br.com.uwant.models.cloud.Requester;

/**
 * Model para envio dos parâmetros da requisição de envio de contatos que serão
 * adicionados na lista de amigos ou convidados a participar do uWant.
 */
public class ContactsModel extends RequestModel {

    private List<String> emails;

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    @Override
    protected JsonObject toJson() {
        TypeToken<List<String>> token = new TypeToken<List<String>>() {};
        JsonElement jsonElement = super.GSON.toJsonTree(this.emails, token.getType());

        JsonObject jsonObject = new JsonObject();
        jsonObject.add(Requester.ParameterKey.CONTACTS, jsonElement);
        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.CONTACTS;
    }

}
