package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.AddFriendModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class FriendAddRequest extends AbstractRequest<Boolean> implements IRequest<AddFriendModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/user/circle/join";

    @Override
    public void executeAsync(AddFriendModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<AddFriendModel> getDataClass() {
        return AddFriendModel.class;
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
