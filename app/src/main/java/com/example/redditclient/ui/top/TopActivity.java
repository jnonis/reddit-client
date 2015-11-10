package com.example.redditclient.ui.top;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.redditclient.R;

/**
 * This activity shows the top list of Reddit entries.
 */
public class TopActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_container);

    if (savedInstanceState == null) {
      FragmentTransaction transaction = getSupportFragmentManager()
          .beginTransaction();
      transaction.replace(R.id.container, TopFragment.newInstance());
      transaction.commit();
    }
  }
}
