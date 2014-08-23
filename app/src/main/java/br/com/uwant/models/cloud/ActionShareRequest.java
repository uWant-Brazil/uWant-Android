package br.com.uwant.models.cloud;

import br.com.uwant.models.classes.Action;
import br.com.uwant.models.cloud.models.ShareModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ActionShareRequest extends AbstractRequest<Action> implements IRequest<ShareModelAbstract, Action> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/share";

    private Action mAction;

    @Override
    public void executeAsync(ShareModelAbstract data, OnRequestListener listener) {
        this.mAction = data.getAction();
        execute(data, listener);
    }

    @Override
    public Class<ShareModelAbstract> getDataClass() {
        return ShareModelAbstract.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Action parse(String response) {
        return this.mAction;
    }

    @Override
    protected Action debugParse() {
        return parse(null);
    }
}
