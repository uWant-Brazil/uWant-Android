package br.com.uwant.models.cloud;

import br.com.uwant.models.cloud.errors.RequestError;

public interface IRequest<K extends RequestModel, T> {

    void executeAsync(K data, OnRequestListener<T> listener);

    Class<K> getDataClass();

    public interface OnRequestListener<T> {

        void onPreExecute();

        void onExecute(T result);

        void onError(RequestError error);
    }

    public enum Type {
        AUTH, REGISTER;
    }

}
