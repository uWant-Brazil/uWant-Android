package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.ExcludeAccountModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ExcludeAccountRequest extends AbstractRequest<Boolean> implements IRequest<ExcludeAccountModelAbstract, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/exclude";

    @Override
    public void executeAsync(ExcludeAccountModelAbstract data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ExcludeAccountModelAbstract> getDataClass() {
        return ExcludeAccountModelAbstract.class;
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
