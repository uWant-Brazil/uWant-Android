package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.RecoveryPasswordModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class RecoveryPasswordRequest extends AbstractRequest<Boolean> implements IRequest<RecoveryPasswordModelAbstract, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/recoveryPassword";

    @Override
    public void executeAsync(RecoveryPasswordModelAbstract data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<RecoveryPasswordModelAbstract> getDataClass() {
        return RecoveryPasswordModelAbstract.class;
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
