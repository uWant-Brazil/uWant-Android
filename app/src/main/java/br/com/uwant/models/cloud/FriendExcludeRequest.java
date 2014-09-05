package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.ExcludeFriendModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class FriendExcludeRequest extends AbstractRequest<Boolean> implements IRequest<ExcludeFriendModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/circle/leave";

    @Override
    public void executeAsync(ExcludeFriendModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ExcludeFriendModel> getDataClass() {
        return ExcludeFriendModel.class;
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
