package br.com.uwant.models.cloud;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.uwant.models.cloud.errors.DefaultRequestError;
import br.com.uwant.models.cloud.errors.RequestError;

abstract class AbstractRequest<K> {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; encoding=utf-8;");
    private static final String URL_COMMON = "http://192.168.1.4:9000/v1";

    protected void execute(RequestModel model, IRequest.OnRequestListener listener) {
        final AsyncRequest asyncRequest = new AsyncRequest(listener);
        asyncRequest.execute(model.getRequestBody());
    }

    protected abstract String getRoute();
    protected abstract K parse(String response);

    private class AsyncRequest extends AsyncTask<String, Void, K> {

        private static final long DEFAULT_TIMEOUT = 2000;

        private OkHttpClient mClient;
        private IRequest.OnRequestListener<K> mListener;
        private String mResponse;

        public AsyncRequest(IRequest.OnRequestListener<K> listener) {
            this.mListener = listener;
            mClient = new OkHttpClient();
            mClient.setConnectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.mListener.onPreExecute();
        }

        @Override
        protected K doInBackground(String... strings) {
            String body = strings[0];
            String url = URL_COMMON + getRoute();

            RequestBody requestBody = RequestBody.create(MEDIA_TYPE, body);
            final Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            try {
                Response response = mClient.newCall(request).execute();
                mResponse = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                mResponse = null;
            }

            return (mResponse != null && isStatus() ? parse(mResponse) : null);
        }

        private boolean isStatus() {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(mResponse);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return jsonObject.has(Requester.ParameterKey.STATUS) && jsonObject.get(Requester.ParameterKey.STATUS).getAsBoolean();
            }
            return false;
        }

        @Override
        protected void onPostExecute(K result) {
            super.onPostExecute(result);
            if (result == null) {
                this.mListener.onError(getError());
            } else {
                this.mListener.onExecute(result);
            }
        }

        private RequestError getError() {
            if (mResponse != null) {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(mResponse);
                if (jsonElement.isJsonObject()) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    if (jsonObject.has(Requester.ParameterKey.STATUS)
                            && !jsonObject.get(Requester.ParameterKey.STATUS).getAsBoolean()
                            && jsonObject.has(Requester.ParameterKey.ERROR)
                            && jsonObject.has(Requester.ParameterKey.MESSAGE)) {
                        int code = jsonObject.get(Requester.ParameterKey.ERROR).getAsInt();
                        String message = jsonObject.get(Requester.ParameterKey.MESSAGE).getAsString();
                        return new RequestError(code, message);
                    }
                }
            }
            return new DefaultRequestError();
        }

    }

}
