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
                request = new RegisterRequest();
                break;

            case RECOVERY_PASSWORD:
                request = new RecoveryPasswordRequest();
                break;

            case SOCIAL_REGISTER:
                request = new SocialRegisterRequest();
                break;

            case CONTACTS:
                request = new ContactsRequest();
                break;

            case WISH_LIST:
                request = new WishListRequest();
                break;

            case FRIENDS_CIRCLE:
                request = new FriendsCircleRequest();
                break;

            case GCM_REGISTRATION:
                request = new GCMRegistrationRequest();
                break;

            default:
                request = null;
                break;
        }
        return request;
    }

}
