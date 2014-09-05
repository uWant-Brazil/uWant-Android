package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.RegisterModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class RegisterRequest extends AbstractRequest<Boolean> implements IRequest<RegisterModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/register";

    @Override
    public void executeAsync(RegisterModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<RegisterModel> getDataClass() {
        return RegisterModel.class;
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
