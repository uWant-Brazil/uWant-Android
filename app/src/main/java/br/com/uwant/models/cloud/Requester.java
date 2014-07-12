package br.com.uwant.models.cloud;

import java.io.InvalidClassException;

public abstract class Requester {

    public static class ParameterKey {
        public static final String STATUS = "status";
        public static final String LOGIN = "login";
        public static final String PASSWORD = "password";
    }

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
