package br.com.uwant.models.cloud;

import java.io.Serializable;

public abstract class MultipartDataModelAbstract extends AbstractRequestModel implements Serializable {

    protected abstract void toMultipartData();

    @Override
    protected String getRequestBody() {
        return null;
    }
}
