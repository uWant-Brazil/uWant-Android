package br.com.uwant.models.cloud.errors;

/**
 * Classe base para todos os erros de integração com o WS.
 */
public class RequestError {

    /**
     * Código do erro.
     */
    private int code;

    /**
     * Mensagem do erro.
     */
    private String message;

    public RequestError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
