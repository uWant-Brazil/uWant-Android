package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.GCMRegistrationModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class GCMRegistrationRequest extends AbstractRequest<Boolean> implements IRequest<GCMRegistrationModelAbstract, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/notification/register";

    @Override
    public void executeAsync(GCMRegistrationModelAbstract data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<GCMRegistrationModelAbstract> getDataClass() {
        return GCMRegistrationModelAbstract.class;
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
