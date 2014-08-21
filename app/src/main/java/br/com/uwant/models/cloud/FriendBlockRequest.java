package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.BlockFriendModel;
import br.com.uwant.models.cloud.models.ExcludeFriendModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class FriendBlockRequest extends AbstractRequest<Boolean> implements IRequest<BlockFriendModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/toggleBlock";

    @Override
    public void executeAsync(BlockFriendModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<BlockFriendModel> getDataClass() {
        return BlockFriendModel.class;
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
