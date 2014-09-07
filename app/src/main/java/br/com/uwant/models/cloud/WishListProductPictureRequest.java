package br.com.uwant.models.cloud;

import br.com.uwant.models.classes.Multimedia;
import br.com.uwant.models.cloud.models.WishListProductPictureModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class WishListProductPictureRequest extends AbstractRequest<Multimedia> implements IRequest<WishListProductPictureModel, Multimedia> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/cdn/retrieve";

    @Override
    public void executeAsync(WishListProductPictureModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<WishListProductPictureModel> getDataClass() {
        return WishListProductPictureModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Multimedia parse(String response) {
        return null;
    }

    @Override
    protected Multimedia debugParse() {
        return null;
    }
}
