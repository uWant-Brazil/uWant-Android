package br.com.uwant.models.cloud;

import java.util.List;

import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.AuthModel;
import br.com.uwant.models.cloud.models.ContactsModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ContactsRequest extends AbstractRequest<Boolean> implements IRequest<ContactsModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/contacts";

    @Override
    public void executeAsync(ContactsModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ContactsModel> getDataClass() {
        return ContactsModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Boolean parse(String response) {
        return true;
    }
}
