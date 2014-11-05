package br.com.uwant.models.cloud;

import android.content.Context;

import br.com.uwant.models.AbstractFactory;

/**
 * Factory responsável pela criação das requisições.
 */
class RequestFactory extends AbstractFactory<IRequest.Type, Context, IRequest> {

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

            case EXCLUDE_ACCOUNT:
                request = new ExcludeAccountRequest();
                break;

            case USER_SEARCH:
                request = new UserSearchRequest();
                break;

            case USER_UPDATE:
                request = new UserUpdateRequest();
                break;

            case ACTIONS:
                request = new ActionsRequest();
                break;

            case FEEDS:
                request = new FeedsRequest();
                break;

            case ACTION_WANT:
                request = new ActionWantRequest();
                break;

            case LOGOFF:
                request = new LogoffRequest();
                break;

            case ACTION_SHARE:
                request = new ActionShareRequest();
                break;

            case ACTION_LIST_COMMENTS:
                request = new ActionCommentsRequest();
                break;

            case ACTION_COMMENT:
                request = new ActionSendCommentRequest();
                break;

            case ACTION_REPORT:
                request = new ActionReportRequest();
                break;

            case BLOCK_FRIEND:
                request = new FriendBlockRequest();
                break;

            case EXCLUDE_FRIEND:
                request = new FriendExcludeRequest();
                break;

            case DELETE_WISH_LIST:
                request = new WishListDeleteRequest();
                break;

            case CREATE_WISH_LIST:
                request = new WishListCreateRequest();
                break;

            case WISH_LIST_PRODUCT_PICTURE:
                request = new WishListProductPictureRequest();
                break;

            case USER_PICTURE:
                request = new RegisterPictureRequest();
                break;

            case WISH_LIST_PRODUCTS:
                request = new WishListProductsRequest();
                break;

            case SOCIAL_LINK:
                request = new SocialLinkRequest();
                break;

            case ADD_FRIEND:
                request = new FriendAddRequest();
                break;

            case UPDATE_WISH_LIST:
                request = new WishListUpdateRequest();
                break;

            default:
                request = null;
                break;
        }
        return request;
    }

    @Override
    public IRequest get(IRequest.Type id, Context parameter) {
        IRequest request;

        switch (id) {
            case WISH_LIST:
                request = new WishListRequest(parameter);
                break;

            case WISH_LIST_PRODUCTS:
                request = new WishListProductsRequest(parameter);
                break;

            case SOCIAL_LINK:
                request = new SocialLinkRequest(parameter);
                break;

            default:
                request = get(id);
        }

        return request;
    }

}
