package br.com.uwant.models.cloud.models;

import com.google.gson.JsonObject;

import br.com.uwant.models.classes.Person;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.AbstractJSONRequestModel;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;

/**
 * Model para envio dos parâmetros da requisição de obtenção das listas de desejos do usuário.
 */
public class WishListModel extends AbstractJSONRequestModel {

    private Person person;

    @Override
    protected JsonObject toJson() {
        JsonObject json = null;
        if (!isMyself()) {
            json = new JsonObject();
            json.addProperty(Requester.ParameterKey.ID, person.getId());
        }
        return json;
    }

    private boolean isMyself() {
        return (person instanceof User);
    }

    @Override
    protected IRequest.Type getRequestType() {
        return IRequest.Type.WISH_LIST;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }
}
