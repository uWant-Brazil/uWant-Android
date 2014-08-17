package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.ExcludeAccountModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ExcludeAccountRequest extends AbstractRequest<Boolean> implements IRequest<ExcludeAccountModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/exclude";

    @Override
    public void executeAsync(ExcludeAccountModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ExcludeAccountModel> getDataClass() {
        return ExcludeAccountModel.class;
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
