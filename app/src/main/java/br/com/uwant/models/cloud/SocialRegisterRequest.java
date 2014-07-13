package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.AbstractRequest;
import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.models.RegisterModel;
import br.com.uwant.models.cloud.models.SocialRegisterModel;

/**
 * Classe de requisição responsável por configurar as informações da chamada ao WS.
 */
public class SocialRegisterRequest extends AbstractRequest<Boolean> implements IRequest<SocialRegisterModel, Boolean> {

    /**
     * Route da requisição.
     */
    private static final String ROUTE = "/mobile/social/signUp";

    @Override
    public void executeAsync(SocialRegisterModel data, OnRequestListener listener) {
        execute(data, listener);
    }

    @Override
    public Class<SocialRegisterModel> getDataClass() {
        return SocialRegisterModel.class;
    }

    @Override
    protected String getRoute() {
        return ROUTE;
    }

    @Override
    protected Boolean parse(String response) {
        return true;
    }
}
