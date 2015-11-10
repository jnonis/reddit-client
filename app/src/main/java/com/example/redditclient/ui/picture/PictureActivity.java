package com.example.redditclient.ui.picture;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.redditclient.R;

/**
 * This activity shows a picture from a url.
 */
public class PictureActivity extends AppCompatActivity {
  /** Picture url extra. */
  public static final String EXTRA_URL = "EXTRA_URL";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_container);

    if (savedInstanceState == null) {
      String url = getIntent().getStringExtra(EXTRA_URL);
      FragmentTransaction transaction = getSupportFragmentManager()
          .beginTransaction();
      transaction.replace(R.id.container, PictureFragment.newInstance(url));
      transaction.commit();
    }
  }
}
