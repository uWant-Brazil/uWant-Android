package br.com.uwant.models.cloud;

import br.com.uwant.models.classes.Action;
import br.com.uwant.models.cloud.models.WantModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ActionWantRequest extends AbstractRequest<Action> implements IRequest<WantModelAbstract, Action> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/want";

    private Action action;

    @Override
    public void executeAsync(WantModelAbstract data, OnRequestListener listener) {
        this.action = data.getAction();
        execute(data, listener);
    }

    @Override
    public Class<WantModelAbstract> getDataClass() {
        return WantModelAbstract.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Action parse(String response) {
        return this.action;
    }

    @Override
    protected Action debugParse() {
        return parse(null);
    }
}
