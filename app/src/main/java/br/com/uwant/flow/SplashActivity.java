package br.com.uwant.flow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import br.com.uwant.R;


public class SplashActivity extends Activity implements Runnable {

    private static final long SLEEP_TIME = 2000;

    private int counter = 0;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final ImageView imageView = (ImageView) findViewById(R.id.splash_imageView);

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int resource = msg.what;
                if (resource != 0) {
                    imageView.setImageResource(resource);
                } else {
                    Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
                    startActivity(intent);
                }
            }

        };

        final Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void onBackPressed() {
        // Não deve responder ao clique no voltar nativo.
        return;
    }

    @Override
    public void run() {
        boolean finished = false;
        while (!finished) {
            int resource = 0;
            switch (counter++) {
                case 0:
                    // TODO Imagem 1
                    resource = 1;
                    break;

                case 1:
                    // TODO Imagem 2
                    resource = 1;
                    break;

                case 2:
                    // TODO Imagem 3
                    resource = 1;
                    break;

                case 3:
                    // TODO Imagem 4
                    resource = 1;
                    break;

                default:
                    finished = true;
                    break;
            }

            mHandler.sendEmptyMessage(resource);

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
