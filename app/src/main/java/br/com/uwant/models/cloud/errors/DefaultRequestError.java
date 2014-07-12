package br.com.uwant.models.cloud.errors;

/**
 * Classe default para retorno de erros na integração com o WS.
 * Este erro só deverá ser usado quando o retorno da requisição não devolver
 * nenhum code (int) ou message (String) no response body da requisição.
 */
public class DefaultRequestError extends RequestError {

    /**
     * Código padrão do erro.
     */
    private static final int CODE = -1;

    /**
     * Mensagem padrão do erro.
     */
    private static final String MESSAGE = "Ocorreu um erro durante o processo." +
            "\nPor favor, verifique sua conexão ou entre em contato com o suporte.";

    public DefaultRequestError() {
        super(CODE, MESSAGE);
    }

}
