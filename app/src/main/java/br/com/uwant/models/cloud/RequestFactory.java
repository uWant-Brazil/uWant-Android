package br.com.uwant.models.cloud;

import br.com.uwant.models.AbstractFactory;

/**
 * Factory responsável pela criação das requisições.
 */
class RequestFactory extends AbstractFactory<IRequest.Type, IRequest> {

    private static final RequestFactory INSTANCE = new RequestFactory();

    public static RequestFactory getInstance() {
        return INSTANCE;
    }

    private RequestFactory() {
    }

    @Override
    public IRequest get(IRequest.Type id) {
        IRequest request;
        switch (id) {
            case AUTH:
                request = new AuthRequest();
                break;
            case REGISTER:
            default:
                request = null;
                break;
        }
        return request;
    }

}
