package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.ActionReportModelAbstract;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ActionReportRequest extends AbstractRequest<Boolean> implements IRequest<ActionReportModelAbstract, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/report";

    @Override
    public void executeAsync(ActionReportModelAbstract data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ActionReportModelAbstract> getDataClass() {
        return ActionReportModelAbstract.class;
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
