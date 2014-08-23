package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.RegisterModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class RegisterRequest extends AbstractRequest<Boolean> implements IRequest<RegisterModelAbstract, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/register";

    @Override
    public void executeAsync(RegisterModelAbstract data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<RegisterModelAbstract> getDataClass() {
        return RegisterModelAbstract.class;
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
