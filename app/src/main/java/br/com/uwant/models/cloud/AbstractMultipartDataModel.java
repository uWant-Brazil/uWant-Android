package br.com.uwant.models.cloud;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.Serializable;
import java.util.concurrent.Executor;

public abstract class AbstractMultipartDataModel extends AbstractRequestModel<HttpEntity> implements Serializable {

    protected abstract MultipartEntityBuilder toMultipartData();

    @Override
    protected HttpEntity getRequestBody() {
        MultipartEntityBuilder builder = toMultipartData();
        return builder.build();
    }

    @Override
    protected Executor getExecutor() {
        return AsyncTask.SERIAL_EXECUTOR;
    }
}
