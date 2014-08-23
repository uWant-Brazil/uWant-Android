package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.ExcludeFriendModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class FriendExcludeRequest extends AbstractRequest<Boolean> implements IRequest<ExcludeFriendModelAbstract, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/circle/leave";

    @Override
    public void executeAsync(ExcludeFriendModelAbstract data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ExcludeFriendModelAbstract> getDataClass() {
        return ExcludeFriendModelAbstract.class;
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
