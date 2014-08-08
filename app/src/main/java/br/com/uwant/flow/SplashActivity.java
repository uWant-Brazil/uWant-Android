package br.com.uwant.flow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import br.com.uwant.R;


public class SplashActivity extends Activity implements Runnable {

    private static final long SLEEP_TIME = 300;

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
                    Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                    startActivity(intent);
                    finish();
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
                    resource = R.drawable.uwant_splash_1;
                    break;

                case 1:
                    resource = R.drawable.uwant_splash_2;
                    break;

                case 2:
                    resource = R.drawable.uwant_splash_3;
                    break;

                case 3:
                    resource = R.drawable.uwant_splash_4;
                    break;

                case 4:
                    resource = R.drawable.uwant_splash_5;
                    break;

                case 5:
                    resource = R.drawable.uwant_splash_6;
                    break;

                case 6:
                    resource = R.drawable.uwant_splash_7;
                    break;

                default:
                    finished = true;
                    break;
            }

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mHandler.sendEmptyMessage(resource);
        }
    }
}
