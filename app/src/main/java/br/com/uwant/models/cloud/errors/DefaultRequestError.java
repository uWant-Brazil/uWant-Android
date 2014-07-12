package br.com.uwant.models.cloud.errors;

public class DefaultRequestError extends RequestError {

    private static final int CODE = -1;
    private static final String MESSAGE = "Ocorreu um erro durante o processo." +
            "\nPor favor, verifique sua conexão ou entre em contato com o suporte.";

    public DefaultRequestError() {
        super(CODE, MESSAGE);
    }

}
