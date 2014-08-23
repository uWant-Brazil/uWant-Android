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
import br.com.uwant.models.cloud.models.GCMRegistrationModelAbstract;

/**
 * Created by felipebenezi on 01/07/14.
 */
public abstract class GoogleCloudMessageUtil {

    private static final String GCM_PREFERENCES = "gcm_preferences";
    private static final String GCM_KEY = "gcm_reg_id";
    private static final String PROJECT_NUMBER = "222456173803";

    public static void registerAsync(final Context context) {
        final AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
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
                if (regId != null) {
                    final SharedPreferences sharedPreferences = context.
                            getSharedPreferences(GCM_PREFERENCES, Activity.MODE_PRIVATE);
                    if (!regId.equals(sharedPreferences.getString(GCM_KEY, null))) {
                        GCMRegistrationModelAbstract model = new GCMRegistrationModelAbstract();
                        model.setRegId(regId);

                        Requester.executeAsync(model, new IRequest.OnRequestListener() {

                            @Override
                            public void onPreExecute() {
                            }

                            @Override
                            public void onExecute(Object result) {
                                sharedPreferences.edit().putString(GCM_KEY, regId).commit();
                            }

                            @Override
                            public void onError(RequestError error) {
                            }

                        });
                    }
                }
            }

        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}
