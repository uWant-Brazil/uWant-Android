package br.com.uwant.models.cloud;

import android.app.Activity;

import br.com.uwant.flow.fragments.WishListFragment;

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
    }

    /**
     * Método responsável pelo processo de integração.
     * Apenas a partir dele é possível realizar a requisição por conta do encapsulamento.
     * @param model - RequestModel com os parâmetros.
     * @param listener - OnRequestListener para os eventos da requisição.
     */
    public static void executeAsync(RequestModel model, IRequest.OnRequestListener listener) {
        RequestFactory factory = RequestFactory.getInstance();
        IRequest request = factory.get(model.getRequestType());

        if (request != null) {
            if (model == null || request.getDataClass() == model.getClass()) {
                request.executeAsync(model, listener);
            } else {
                throw new RuntimeException("A classe enviada como data é diferente da necessária para a requisição.");
            }
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
    public static void executeAsync(RequestModel model) {
        executeAsync(model, null);
    }

    /**
     * Método responsável pelo processo de integração.
     * Apenas a partir dele é possível realizar a requisição por conta do encapsulamento, além disso,
     * nenhum model necessita ser passado para envio como body da requisição.
     * @param listener - OnRequestListener para os eventos da requisição.
     */
    public static void executeAsync(IRequest.OnRequestListener listener) {
        executeAsync(null, listener);
    }

}
