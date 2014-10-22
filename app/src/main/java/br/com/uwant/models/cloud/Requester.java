package br.com.uwant.models.cloud;

import android.content.Context;

/**
 * Classe utilitária responsável por realizar e configurar toda a chamada ao WS.
 * Esta classe deve ser executada a partir de classes que irão realizar as chamadas da requisição,
 * geralmente sendo as Activities.
 */
public abstract class Requester {

    /**
     * Chaves para parametrização dos campos durante as requisições.
     */
    public static class ParameterKey {
        public static final String STATUS = "status";
        public static final String MESSAGE = "message";
        public static final String ERROR = "error";
        public static final String LOGIN = "login";
        public static final String PASSWORD = "password";
        public static final String FULL_NAME = "fullName";
        public static final String GENDER = "gender";
        public static final String MAIL = "mail";
        public static final String BIRTHDAY = "birthday";
        public static final String SOCIAL_PROFILE = "socialProfile";
        public static final String SOCIAL_PROVIDER = "socialProvider";
        public static final String TOKEN = "access_token";
        public static final String REGISTERED = "registered";
        public static final String CONTACTS = "contacts";
        public static final String WISHLIST = "wishlist";
        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String USER = "user";
        public static final String NAME = "name";
        public static final String PICTURE = "picture";
        public static final String URL = "url";
        public static final String FRIENDS = "friends";
        public static final String MOBILE_IDENTIFIER = "mobileIdentifier";
        public static final String OS = "os";
        public static final String START_INDEX = "startIndex";
        public static final String END_INDEX = "endIndex";
        public static final String ACTIONS = "actions";
        public static final String TYPE = "type";
        public static final String USER_FROM = "userFrom";
        public static final String EXTRA = "extra";
        public static final String WHEN = "when";
        public static final String USERS = "users";
        public static final String QUERY = "query";
        public static final String ACTION_ID = "actionId";
        public static final String COMMENTS_COUNT = "commentsCount";
        public static final String COUNT = "count";
        public static final String WANT = "want";
        public static final String SHARE = "share";
        public static final String UWANT = "uWant";
        public static final String USHARE = "uShare";
        public static final String COMMENTS = "comments";
        public static final String TEXT = "text";
        public static final String SINCE = "since";
        public static final String PRODUCTS = "products";
        public static final String NICK_NAME = "nickName";
        public static final String MANUFACTURER = "manufacturer";
        public static final String MULTIMEDIA_PRODUCT = "multimediaProduct";
        public static final String MULTIMEDIA_USER_PICTURE = "multimediaUserPicture";
        public static final String MULTIMEDIA = "multimedia";
        public static final String USER_ID = "userId";
        public static final String WISHLIST_ID = "wishlistId";
        public static final String LINKED = "linked";
        public static final String FRIENDSHIP_LEVEL = "friendshipLevel";
        public static final String PRODUCTS_REMOVED = "productsRemoved";
    }

    /**
     * Método responsável pelo processo de integração.
     * Apenas a partir dele é possível realizar a requisição por conta do encapsulamento.
     * @param model - RequestModel com os parâmetros.
     * @param listener - OnRequestListener para os eventos da requisição.
     */
    public static void executeAsync(AbstractRequestModel model, IRequest.OnRequestListener listener) {
        RequestFactory factory = RequestFactory.getInstance();
        IRequest request = factory.get(model.getRequestType());

        if (request != null) {
                request.executeAsync(model, listener);
        } else {
            throw new RuntimeException("A requisição está nula. Verifique se você mapeou corretamente em RequestFactory.class!");
        }
    }

    /**
     * Método responsável pelo processo de integração.
     * Apenas a partir dele é possível realizar a requisição por conta do encapsulamento, além disso,
     * nenhum lister necessita ser passado para saber os passos da requisição.
     * @param model - RequestModel com os parâmetros.
     */
    public static void executeAsync(AbstractRequestModel model) {
        executeAsync(model, null);
    }

    /**
     * Método responsável pelo processo de integração.
     * Apenas a partir dele é possível realizar a requisição por conta do encapsulamento.
     * @param context - Contexto da Activity
     * @param model - RequestModel com os parâmetros.
     * @param listener - OnRequestListener para os eventos da requisição.
     */
    public static void executeAsync(Context context, AbstractRequestModel model, IRequest.OnRequestListener listener) {
        RequestFactory factory = RequestFactory.getInstance();
        IRequest request = factory.get(model.getRequestType(), context);

        if (request != null) {
            request.executeAsync(model, listener);
        } else {
            throw new RuntimeException("A requisição está nula. Verifique se você mapeou corretamente em RequestFactory.class!");
        }
    }

    /**
     * Método responsável pelo processo de integração.
     * Apenas a partir dele é possível realizar a requisição por conta do encapsulamento, além disso,
     * nenhum lister necessita ser passado para saber os passos da requisição.
     * @param context - Contexto da Activity
     * @param model - RequestModel com os parâmetros.
     */
    public static void executeAsync(Context context, AbstractRequestModel model) {
        executeAsync(context, model, null);
    }

}
