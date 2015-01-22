package br.com.uwant.models.cloud.models;

import com.facebook.Request;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

/**
 * Model para envio dos parâmetros da requisição de envio de contatos que serão
 * adicionados na lista de amigos ou convidados a participar do uWant.
 */
public class ContactsModel extends AbstractJSONRequestModel {

    private List<Person> persons;

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }

    @Override
    protected JsonObject toJson() {
        List<JsonObject> jsonContacts = new ArrayList<JsonObject>();
        for (Person p : persons) {
            JsonObject json = new JsonObject();
            json.addProperty(Requester.ParameterKey.LOGIN, p.getLogin());
            json.addProperty(Requester.ParameterKey.MAIL, p.getMail());
            json.addProperty(Requester.ParameterKey.FACEBOOK_ID, p.getFacebookId());
            jsonContacts.add(json);
        }

        TypeToken<List<JsonObject>> token = new TypeToken<List<JsonObject>>() {};
        JsonElement jsonElement = super.GSON.toJsonTree(jsonContacts, token.getType());

        JsonObject jsonObject = new JsonObject();
        jsonObject.add(Requester.ParameterKey.CONTACTS, jsonElement);
        return jsonObject;
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.CONTACTS;
    }

}
