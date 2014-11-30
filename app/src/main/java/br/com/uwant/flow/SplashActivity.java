package br.com.uwant.flow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import br.com.uwant.R;
import br.com.uwant.models.classes.User;
import br.com.uwant.models.databases.UserDatabase;


public class SplashActivity extends Activity implements Runnable {

    private static final long SLEEP_TIME = 300;

    private int counter = 0;
    private boolean isLogged;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Crashlytics.start(this);
        super.onCreate(savedInstanceState);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCacheExtraOptions(480, 800, null)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(4 * 1024 * 1024))
                .memoryCacheSize(4 * 1024 * 1024)
                .diskCacheSize(100 * 1024 * 1024)
                .diskCacheFileCount(200)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);

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
                    Intent intent = new Intent(SplashActivity.this, isLogged ? MainActivity.class : AuthenticationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        };

        final Thread thread = new Thread(this);
        thread.start();

        UserDatabase db = new UserDatabase(this);
        isLogged = db.existAnything();
        if (isLogged) {
            User.newInstance(db.selectAll().get(0));
        }
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
