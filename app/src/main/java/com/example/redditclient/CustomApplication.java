package com.example.redditclient;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.L;

/**
 * Custom application.
 */
public class CustomApplication extends Application {
  /** Size of the image disk cache: 5 Mb. */
  private static final int IMAGE_DISK_CACHE_SIZE = 5 * 1024 * 1024;

  @Override
  public void onCreate() {
    super.onCreate();

    // Create ImageLoader configuration.
    ImageLoaderConfiguration config =
        new ImageLoaderConfiguration.Builder(getApplicationContext())
            .diskCacheSize(IMAGE_DISK_CACHE_SIZE)
            .tasksProcessingOrder(QueueProcessingType.LIFO)
            .build();
    // Initialize ImageLoader with configuration.
    ImageLoader.getInstance().init(config);
    L.writeLogs(false);
    L.writeDebugLogs(false);
  }
}
