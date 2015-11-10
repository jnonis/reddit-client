package com.example.redditclient.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.example.redditclient.ui.widget.LoadingImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Utilities for Image Loader.
 */
public class ImageLoaderUtils {

  /** Default display options. */
  private static final DisplayImageOptions.Builder getDefaultDisplayOptionsBuilder() {
    return new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .resetViewBeforeLoading(true)
        .displayer(new FadeInBitmapDisplayer(200, true, true, false));
  }

  /**
   * Fetch an image from given url and display it in given view.
   * This method is asynchronous, and use cache.
   * @param url image url.
   * @param view image view.
   */
  public static void displayImage(String url, LoadingImageView view) {
    ImageLoader.getInstance().displayImage(url, view.getImageView(),
        getDefaultDisplayOptionsBuilder().build(),
        new LoadingViewLoadingListener(view.getLoadingView()));
  }

  /**
   * Implementation of {@link
   * com.nostra13.universalimageloader.core.listener.ImageLoadingListener}
   * which handles the visibility of a loading view.
   */
  private static class LoadingViewLoadingListener implements
      ImageLoadingListener {
    private View mLoadingView;

    public LoadingViewLoadingListener(View view) {
      mLoadingView = view;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
      mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason
        failReason) {
      mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingComplete(String imageUri, View view,
        Bitmap loadedImage) {
      mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
      mLoadingView.setVisibility(View.GONE);
    }
  }
}