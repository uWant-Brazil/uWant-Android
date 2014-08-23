package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.ContactsModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ContactsRequest extends AbstractRequest<Boolean> implements IRequest<ContactsModelAbstract, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/contacts";

    @Override
    public void executeAsync(ContactsModelAbstract data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ContactsModelAbstract> getDataClass() {
        return ContactsModelAbstract.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Boolean parse(String response) {
        return true;
    }

    @Override
    protected Boolean debugParse() {
        return parse(null);
    }
}
