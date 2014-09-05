package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.WishListDeleteModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class WishListDeleteRequest extends AbstractRequest<Boolean> implements IRequest<WishListDeleteModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/wishlist/delete";

    @Override
    public void executeAsync(WishListDeleteModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<WishListDeleteModel> getDataClass() {
        return WishListDeleteModel.class;
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
