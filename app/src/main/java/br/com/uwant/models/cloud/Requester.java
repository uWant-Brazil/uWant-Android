package br.com.uwant.models.cloud;

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
            if (request.getDataClass() == model.getClass()) {
                request.executeAsync(model, listener);
            } else {
                throw new RuntimeException("A classe enviada como data é diferente da necessária para a requisição.");
            }
        } else {
            throw new RuntimeException("A requsição está nula. Verifique se você mapeou corretamente em RequestFactory.class!");
        }
    }

}
