package br.com.uwant;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import br.com.uwant.models.services.GCMMessageHandler;
import br.com.uwant.utils.DebugUtil;

/**
 * Receiver responsável pelo tratamento das mensagens enviadas pelo Google (GCM).
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {

    public static final String TAG = "GCMBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        DebugUtil.info(TAG, "Uma nova mensagem do Google (GCM) foi recebida.");
        String packageName = context.getPackageName();
        String cls = GCMMessageHandler.class.getName();
        ComponentName comp = new ComponentName(packageName, cls);

        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

}
