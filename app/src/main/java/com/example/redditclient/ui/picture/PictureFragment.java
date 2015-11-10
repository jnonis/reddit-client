package com.example.redditclient.ui.picture;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.redditclient.R;
import com.example.redditclient.ui.widget.LoadingImageView;
import com.example.redditclient.utils.ImageLoaderUtils;

/**
 * This fragment shows a picture from a url.
 */
public class PictureFragment extends Fragment {
  /** Picture url argument. */
  private static final String ARG_URL = "ARG_URL";
  /** Picture url. */
  private String mUrl;

  /**
   * Create a instance of the fragment.
   * @param url the picture url.
   */
  public static PictureFragment newInstance(String url) {
    Bundle bundle = new Bundle();
    bundle.putString(ARG_URL, url);
    PictureFragment fragment = new PictureFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Keep the fragment alive on configuration changed.
    setRetainInstance(true);
    // Get the arguments.
    mUrl = getArguments().getString(ARG_URL);
    // Enable menu.
    setHasOptionsMenu(true);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    // Inflate the menu.
    inflater.inflate(R.menu.menu_picture, menu);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_picture, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    LoadingImageView pictureView = (LoadingImageView) view.findViewById(
        R.id.picture);
    // Load image.
    ImageLoaderUtils.displayImage(mUrl, pictureView);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Get action id.
    int id = item.getItemId();

    if (id == R.id.action_save) {
      // Save image to gallery.
      savePicture();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /** Execute a task to save the picture. */
  private void savePicture() {
    SavePictureTask task = new SavePictureTask(getActivity()
        .getApplicationContext());
    task.execute(mUrl);
  }
}
