package br.com.uwant.models.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import br.com.uwant.GCMBroadcastReceiver;
import br.com.uwant.R;
import br.com.uwant.flow.SplashActivity;
import br.com.uwant.models.cloud.Requester;

/**
 * Created by felipebenezi on 03/08/14.
 */
public class GCMMessageHandler extends IntentService {

    private static final int NOTIFICATION_ID = 0x6547;

    public GCMMessageHandler() {
        super("GCMMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        String title = extras.getString(Requester.ParameterKey.TITLE, null);
        String message = extras.getString(Requester.ParameterKey.MESSAGE, null);
        if (title == null) {
            title = getString(R.string.app_name);
        }

        if (message != null) {
            showNotification(title, message);
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void showNotification(String title, String message) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSound(soundUri)
                        .setAutoCancel(true)
                        .setVibrate(new long[] { 200 })
                        .setSmallIcon(R.drawable.ic_action_uwant) // TODO Modificar icon.
                        .setContentTitle(title)
                        .setContentText(message);

        Intent resultIntent = new Intent(this, SplashActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
