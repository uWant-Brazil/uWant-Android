package br.com.uwant.models.cloud;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.uwant.models.classes.User;
import br.com.uwant.models.cloud.errors.DefaultRequestError;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.utils.DebugUtil;

/**
 * Classe responsável por realizar a comunicação com o WS, enviando os parâmetros e realizando
 * as chamadas dos métodos responsáveis por tratar a resposta.
 * @param <K> - Classe que deverá ser montada no retorno da requisição.
 */
public abstract class AbstractRequest<K> {

    /**
     * Content-Type.
     */
    public static final String JSON_CONTENT_TYPE = "application/json; encoding=utf-8;";

    /**
     * Content-Type encapsulado para a requisição.
     */
    private static final MediaType MEDIA_TYPE = MediaType.parse(JSON_CONTENT_TYPE);

    /**
     * Padrão das URLs de requisição.
     */
    public static final String URL_COMMON = "http://192.168.0.100:9000/v1";

    /**
     * Header responsável por conter o token de autenticação para requisições.
     */
    private static final String HEADER_AUTHENTICATION_TOKEN = "Authentication";

    /**
     * Contexto da activity em execução durante a requisição.
     */
    private Context mContext;

    protected AbstractRequest() {
    }

    protected AbstractRequest(Context context) {
        this.mContext = context;
    }

    /**
     * Método responsável por iniciar a requisição (Ainda não está assíncrono).
     * @param model - RequestModel com os parâmetros de envio.
     * @param listener - Classe que irá ser avisada ao finalizar a requisição.
     */
    protected void execute(AbstractRequestModel model, IRequest.OnRequestListener listener) {
        if (DebugUtil.DEBUG_WITHOUT_REQUEST) {
            listener.onPreExecute();
            listener.onExecute(debugParse());
        } else if (model instanceof AbstractMultipartDataModel) {
            AbstractMultipartDataModel mdma = (AbstractMultipartDataModel) model;

            final AsyncMultipartDataRequest asyncRequest = new AsyncMultipartDataRequest(listener);
            asyncRequest.executeOnExecutor(mdma.getExecutor(), mdma.getRequestBody());
        } else {
            AbstractJSONRequestModel jrm = (AbstractJSONRequestModel) model;

            final AsyncRequest asyncRequest = new AsyncRequest(listener);
            asyncRequest.executeOnExecutor(jrm.getExecutor(), jrm.getRequestBody());
        }
    }

    /**
     * Método responsável por retornar o route da requisição.
     * Não deve ser colocado o caminho completo até o método, apenas o route.
     * EX: /mobile/login
     * @return route
     */
    protected abstract String getRoute();

    /**
     * Método responsável por retornar a classe de resposta da requisição.
     * Dentro desse método, você deve realizar o parse da string gerada pelo WS.
     * @param response
     * @return K - Classe de resposta
     */
    protected abstract K parse(String response);

    /**
     * Método responsável por retornar a classe de resposta da requisição.
     * Dentro desse método você deve realizar o pase localmente para debug.
     * @return K - Classe de resposta
     */
    protected abstract K debugParse();

    /**
     * Setter -> Contexto inicializante da requisição.
     * @return context
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Classe que realizará as chamadas ao WS de forma assíncrona.
     * Além disso, ela também irá avisar ao OnRequestListener dos eventos que estão acontecendo.
     */
    private class AsyncRequest extends AsyncTask<String, Void, K> {

        /**
         * Timeout padrão para a requisição em minutos.
         */
        private static final long DEFAULT_TIMEOUT = 2;

        /**
         * Classe responsável por montar a classe de requisição.
         */
        private OkHttpClient mClient;

        /**
         * Listener para os eventos da requisição.
         */
        private IRequest.OnRequestListener<K> mListener;

        /**
         * Resposta gerada pelo WS após a requisição.
         */
        private String mResponse;

        public AsyncRequest(IRequest.OnRequestListener<K> listener) {
            this.mListener = listener;
            mClient = new OkHttpClient();
            mClient.setConnectTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (this.mListener != null) {
                this.mListener.onPreExecute();
            }
        }

        @Override
        protected K doInBackground(String... strings) {
            String body = strings[0];
            String url = URL_COMMON + getRoute();

            RequestBody requestBody = null;
            if (body != null) {
                requestBody = RequestBody.create(MEDIA_TYPE, body);
            }

            Request.Builder builder = new Request.Builder().url(url).post(requestBody);

            User user = User.getInstance();
            String token = user.getToken();
            if (token != null && !token.isEmpty()) {
                builder.addHeader(HEADER_AUTHENTICATION_TOKEN, token);
            }

            final Request request = builder.build();
            try {
                Response response = mClient.newCall(request).execute();
                mResponse = response.body().string();

                String tokenTmp = response.header(HEADER_AUTHENTICATION_TOKEN);
                if (tokenTmp != null) {
                    token = response.header(HEADER_AUTHENTICATION_TOKEN);
                    user.setToken(token);
                }
            } catch (IOException e) {
                e.printStackTrace();
                mResponse = null;
            }

            return (mResponse != null && isStatus() ? parse(mResponse) : null);
        }

        @Override
        protected void onPostExecute(K result) {
            super.onPostExecute(result);
            if (mListener != null) {
                if (result == null) {
                    this.mListener.onError(getError());
                } else {
                    this.mListener.onExecute(result);
                }
            }
        }

        /**
         * Método responsável por verificar se a requisição foi efetuada com sucesso.
         * O sucesso é em relação ao campo 'status' retornado no JSON.
         * @return true ou false
         */
        private boolean isStatus() {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(mResponse);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return jsonObject.has(Requester.ParameterKey.STATUS) && jsonObject.get(Requester.ParameterKey.STATUS).getAsBoolean();
            }
            return false;
        }

        /**
         * Método responsável por gerar a classe de erro da requisição.
         * Este método só é acionado quando não temos resposta do servidor ou então
         * quando o servidor retorna um JSON com o campo 'status' como false.
         * @return error - Erro do WS tratado.
         */
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

    private class AsyncMultipartDataRequest extends AsyncTask<HttpEntity, Void, K> {

        /**
         * Timeout padrão para a requisição em minutos.
         */
        private static final long DEFAULT_TIMEOUT = 2;

        /**
         * Classe responsável por montar a classe de requisição.
         */
        private OkHttpClient mClient;

        /**
         * Listener para os eventos da requisição.
         */
        private IRequest.OnRequestListener<K> mListener;

        /**
         * Resposta gerada pelo WS após a requisição.
         */
        private String mResponse;

        public AsyncMultipartDataRequest(IRequest.OnRequestListener<K> listener) {
            this.mListener = listener;
            mClient = new OkHttpClient();
            mClient.setConnectTimeout(DEFAULT_TIMEOUT, TimeUnit.MINUTES);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected K doInBackground(HttpEntity... params) {
            HttpEntity entity = params[0];
            String url = URL_COMMON + getRoute();

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(entity);

            User user = User.getInstance();
            String token = user.getToken();
            if (token != null && !token.isEmpty()) {
                httppost.addHeader(HEADER_AUTHENTICATION_TOKEN, token);
            }

            try {
                HttpResponse httpResponse = httpclient.execute(httppost);
                HttpEntity responseEntity = httpResponse.getEntity();

                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    mResponse = EntityUtils.toString(responseEntity, HTTP.UTF_8);
                } else {
                    mResponse = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                mResponse = null;
            }

            return (mResponse != null && isStatus() ? parse(mResponse) : null);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(K result) {
            super.onPostExecute(result);
            if (mListener != null) {
                if (result == null) {
                    this.mListener.onError(getError());
                } else {
                    this.mListener.onExecute(result);
                }
            }
        }

        /**
         * Método responsável por verificar se a requisição foi efetuada com sucesso.
         * O sucesso é em relação ao campo 'status' retornado no JSON.
         * @return true ou false
         */
        private boolean isStatus() {
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(mResponse);
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                return jsonObject.has(Requester.ParameterKey.STATUS) && jsonObject.get(Requester.ParameterKey.STATUS).getAsBoolean();
            }
            return false;
        }

        /**
         * Método responsável por gerar a classe de erro da requisição.
         * Este método só é acionado quando não temos resposta do servidor ou então
         * quando o servidor retorna um JSON com o campo 'status' como false.
         * @return error - Erro do WS tratado.
         */
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
