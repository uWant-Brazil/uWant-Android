package br.com.uwant.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import br.com.uwant.models.cloud.IRequest;
import br.com.uwant.models.cloud.Requester;
import br.com.uwant.models.cloud.errors.RequestError;
import br.com.uwant.models.cloud.models.GCMRegistrationModel;

/**
 * Classe utilitária responsável por métodos relacionados ao Google Cloud Message (Notifications).
 */
public abstract class GoogleCloudMessageUtil {

    private static final String TAG = "GoogleCloudMessageUtil";
    private static final String GCM_PREFERENCES = "gcm_preferences";
    private static final String GCM_KEY = "gcm_reg_id";
    private static final String PROJECT_NUMBER = "222456173803";

    /**
     * Método assíncrono responsável por realizar requisição no Google para obter o RegID.
     * Caso seja realizada com sucesso, esse RegID será encaminhado para o servidor do uWant!
     * @param context
     */
    public static void registerAsync(final Context context) {
        DebugUtil.info(TAG, "Configurando Task para registro no GCM...");
        final AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                DebugUtil.info(TAG, "Realizando registro...");
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                String regId = null;
                try {
                    regId = gcm.register(PROJECT_NUMBER);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return regId;
            }

            @Override
            protected void onPostExecute(final String regId) {
                super.onPostExecute(regId);
                DebugUtil.info(TAG, "Registro finalizado!");
                if (regId != null) {
                    DebugUtil.info(TAG, String.format("RegID: %s", regId));
                    final SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_PREFERENCES, Activity.MODE_PRIVATE);
                    if (!regId.equals(sharedPreferences.getString(GCM_KEY, null))) {
                        DebugUtil.info(TAG, "Encaminhando para o WebService do uWant...");

                        GCMRegistrationModel model = new GCMRegistrationModel();
                        model.setRegId(regId);

                        Requester.executeAsync(model, new IRequest.OnRequestListener() {

                            @Override
                            public void onPreExecute() {
                            }

                            @Override
                            public void onExecute(Object result) {
                                DebugUtil.info(TAG, "RegID salvo com sucesso!");
                                sharedPreferences.edit().putString(GCM_KEY, regId).commit();
                            }

                            @Override
                            public void onError(RequestError error) {
                                DebugUtil.error(TAG, "Ocorreu um problema para salvar o RegID!");
                            }

                        });
                    }
                }
            }

        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Método responsável por remover o RegID pré-salvo.
     * @param context
     */
    public static void clear(Context context) {
        final SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_PREFERENCES, Activity.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
    }

}
