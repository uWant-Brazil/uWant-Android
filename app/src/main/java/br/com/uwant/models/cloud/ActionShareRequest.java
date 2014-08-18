package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.ShareModel;
import br.com.uwant.models.cloud.models.WantModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ActionShareRequest extends AbstractRequest<Boolean> implements IRequest<ShareModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/share";

    @Override
    public void executeAsync(ShareModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ShareModel> getDataClass() {
        return ShareModel.class;
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
