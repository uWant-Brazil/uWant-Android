package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.errors.RequestError;

/**
 * Interface responsável por identificar as requisições do sistema.
 * @param <K> - RequestModel para envio
 * @param <T> - Classe para resposta
 */
public interface IRequest<K extends RequestModel, T> {

    /**
     * Método responsável por iniciar o processo de integração.
     * @param data - RequestModel para envio
     * @param listener - Listener da requisição
     */
    void executeAsync(K data, OnRequestListener<T> listener);

    /**
     * Retorno do .class do RequestModel.
     * @return
     */
    Class<K> getDataClass();

    /**
     * Interface responsável por observar os eventos da requisição.
     * @param <T> - Classe para resposta
     */
    public interface OnRequestListener<T> {

        /**
         * Este método é acionado antes de realizar a requisição.
         * Último passo antes de realizar a chamada assíncrona.
         */
        void onPreExecute();

        /**
         * Este método é acionado após toda a requisição ser executada, já com sua resposta.
         * @param result - Resultado
         */
        void onExecute(T result);

        /**
         * Este método é acionado após toda a requisição ser executada, mas ocorreu um erro.
         * @param error
         */
        void onError(RequestError error);
    }

    /**
     * Tipo da requisição para identificação pelo RequestFactory.
     */
    public enum Type {
        AUTH, REGISTER;
    }

}
