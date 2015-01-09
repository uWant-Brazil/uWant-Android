package br.com.uwant;

import android.app.Application;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

import br.com.uwant.utils.DebugUtil;

/**
 * Classe responsável pelo tratamento do ciclo de vida da aplicação.
 */
public class UWApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        DebugUtil.info("Inicializando o Crashlytics...");
//        Crashlytics.start(this);
//        DebugUtil.info("Crashlytics inicializado com sucesso!");

        String CACHE_DIR = String.format("%s/.uwtemp", Environment.getExternalStorageDirectory().getAbsolutePath());
        File dir = new File(CACHE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            DebugUtil.info("Diretório (" + CACHE_DIR + ") criado.");
        }

        File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(), CACHE_DIR);

        DebugUtil.info("Configurando a biblioteca do ImageLoader...");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .denyCacheImageMultipleSizesInMemory()
                .build();
        ImageLoader.getInstance().init(config);
        DebugUtil.info("Biblioteca configurada com sucesso!");
    }

}
