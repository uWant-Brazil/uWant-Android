package br.com.uwant.models.cloud;

import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.models.AuthModel;
import br.com.uwant.models.cloud.models.RecoveryPasswordModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class RecoveryPasswordRequest extends AbstractRequest<Boolean> implements IRequest<RecoveryPasswordModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/recoveryPassword";

    @Override
    public void executeAsync(RecoveryPasswordModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<RecoveryPasswordModel> getDataClass() {
        return RecoveryPasswordModel.class;
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
