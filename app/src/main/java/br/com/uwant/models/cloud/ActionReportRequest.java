package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.models.ActionReportModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class ActionReportRequest extends AbstractRequest<Boolean> implements IRequest<ActionReportModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/action/report";

    @Override
    public void executeAsync(ActionReportModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<ActionReportModel> getDataClass() {
        return ActionReportModel.class;
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
