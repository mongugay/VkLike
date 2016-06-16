package ru.mongugay.vklike;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.VKSdk;

/**
 * Created by user on 24.09.2015.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();
        VKSdk.initialize(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }
}
