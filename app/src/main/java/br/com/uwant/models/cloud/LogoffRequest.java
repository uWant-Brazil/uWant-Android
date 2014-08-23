package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.LogoffModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class LogoffRequest extends AbstractRequest<Boolean> implements IRequest<LogoffModelAbstract, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/logoff";

    @Override
    public void executeAsync(LogoffModelAbstract data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<LogoffModelAbstract> getDataClass() {
        return LogoffModelAbstract.class;
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
