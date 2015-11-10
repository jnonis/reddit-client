package com.example.redditclient.ui.top;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.redditclient.R;
import com.example.redditclient.provider.AppContract;
import com.example.redditclient.service.ApiIntentService;
import com.example.redditclient.ui.widget.ErrorDialogFragment;
import com.example.redditclient.ui.picture.PictureActivity;


/**
 * This fragment shows the top list of Reddit entries.
 */
public class TopFragment extends Fragment implements
    LoaderManager.LoaderCallbacks<Cursor> {
  /** Error dialog tag. */
  private static final String ERROR_DIALOG_FRAGMENT = "ERROR_DIALOG_FRAGMENT";
  /** Next page id state. */
  private static final String STATE_NEXT_PAGE_ID = "STATE_NEXT_PAGE_ID";
  /** List view of entries. */
  private ListView mEntriesView;
  /** List adapter. */
  private EntriesAdapter mAdapter;
  /** Service result receiver. */
  private BroadcastReceiver mResultReceiver;
  /** Next page of entries id. */
  private String mNextPageId;

  /** Create a instance of the fragment, */
  public static TopFragment newInstance() {
    return new TopFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Keep the fragment alive on configuration changed.
    setRetainInstance(true);

    mResultReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        int result = intent.getExtras().getInt(ApiIntentService.EXTRA_RESULT);
        String nextPageId = intent.getExtras().getString(
            ApiIntentService.EXTRA_NEXT_PAGE_ID);
        handleServiceResult(result, nextPageId);
      }
    };

    if (savedInstanceState != null) {
      mNextPageId = savedInstanceState.getString(STATE_NEXT_PAGE_ID);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_top, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mEntriesView = (ListView) view.findViewById(android.R.id.list);
    View emptyView = view.findViewById(android.R.id.empty);
    mEntriesView.setEmptyView(emptyView);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // Setup adapter.
    if (mAdapter == null) {
      // Use application context to avoid leaks.
      mAdapter = new EntriesAdapter(getActivity().getApplicationContext());
    }
    mAdapter.setOnPageRequestListener(new PagedCursorAdapter.OnPageRequestListener() {
      @Override
      public void onPageRequest() {
        if (mNextPageId == null) {
          mAdapter.setHasMorePages(false);
        }
        startRequest();
      }
    });
    mAdapter.setOnThumbnailClickListener(new EntriesAdapter.OnThumbnailClickListener() {
      @Override
      public void onThumbnailClick(String picture) {
        startPicture(picture);
      }
    });
    mEntriesView.setAdapter(mAdapter);

    // Initialize loader.
    LoaderManager loaderManager = getActivity().getSupportLoaderManager();
    loaderManager.initLoader(0, null, this);

    // Call service.
    if (savedInstanceState == null) {
      startRequest();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    getActivity().registerReceiver(mResultReceiver,
        new IntentFilter(ApiIntentService.ACTION_SERVICE_FINISHED));
  }

  @Override
  public void onPause() {
    super.onPause();
    getActivity().unregisterReceiver(mResultReceiver);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(STATE_NEXT_PAGE_ID, mNextPageId);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mAdapter.setOnThumbnailClickListener(null);
    mEntriesView.setAdapter(null);
  }

  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new CursorLoader(getActivity(), AppContract.Entries.buildUri(),
        AppContract.Entries.DEFAULT_PROJECTION, null, null,
        AppContract.Entries.DEFAULT_SORT);
  }

  @Override
  public void onLoadFinished(Loader loader, Cursor data) {
    mAdapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader loader) {
    mAdapter.swapCursor(null);
  }

  /** Send an intent to request fetch data from service. */
  private void startRequest() {
    Intent intent = new Intent(getActivity(), ApiIntentService.class);
    if (mNextPageId != null) {
      intent.putExtra(ApiIntentService.EXTRA_NEXT_PAGE_ID, mNextPageId);
    }
    getActivity().startService(intent);
  }

  /** Handles service results. */
  private void handleServiceResult(int result, String nextPageId) {
    switch (result) {
      case ApiIntentService.RESULT_OK:
        mNextPageId = nextPageId;
        break;
      case ApiIntentService.RESULT_NETWORK_FAIL:
        showErrorDialog(R.string.error_connection);
        break;
      case ApiIntentService.RESULT_APP_FAIL:
        showErrorDialog(R.string.error_app);
        break;
      case ApiIntentService.RESULT_SERVICE_FAIL:
        showErrorDialog(R.string.error_service);
        break;
    }
  }

  /** Send an intent to start picture activity. */
  private void startPicture(String picture) {
    Intent intent = new Intent(getActivity(), PictureActivity.class);
    intent.putExtra(PictureActivity.EXTRA_URL, picture);
    startActivity(intent);
  }

  /**
   * Shows an error dialog.
   * It will remove any other previous error dialog.
   * @param resErrorMessage the resrouce of error messages.
   */
  private void showErrorDialog(int resErrorMessage) {
    ErrorDialogFragment errorDialog = ErrorDialogFragment.newInstance(
        resErrorMessage, false);
    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
    FragmentTransaction transaction = fragmentManager.beginTransaction();

    // Remove old fragment.
    Fragment dialog = fragmentManager.findFragmentByTag(
        ERROR_DIALOG_FRAGMENT);
    if (dialog != null) {
      transaction.remove(dialog);
    }

    // Add new fragment.
    transaction.add(errorDialog, ERROR_DIALOG_FRAGMENT);
    transaction.commit();
  }
}
