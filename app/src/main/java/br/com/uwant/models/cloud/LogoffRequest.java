package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.LogoffModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class LogoffRequest extends AbstractRequest<Boolean> implements IRequest<LogoffModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/logoff";

    @Override
    public void executeAsync(LogoffModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<LogoffModel> getDataClass() {
        return LogoffModel.class;
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
