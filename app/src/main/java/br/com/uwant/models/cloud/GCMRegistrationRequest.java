package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.GCMRegistrationModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class GCMRegistrationRequest extends AbstractRequest<Boolean> implements IRequest<GCMRegistrationModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/notification/register";

    @Override
    public void executeAsync(GCMRegistrationModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<GCMRegistrationModel> getDataClass() {
        return GCMRegistrationModel.class;
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
