package com.example.redditclient.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.redditclient.provider.AppContract;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * This service fetch data from reddit top service.
 */
public class ApiIntentService extends IntentService {
  /** Log tag. */
  private static final String TAG = ApiIntentService.class.getSimpleName();

  /** Result extra. */
  public static final String EXTRA_NEXT_PAGE_ID = "EXTRA_NEXT_PAGE_ID";

  /** Action for result intent. */
  public static final String ACTION_SERVICE_FINISHED = "ACTION_SERVICE_FINISHED";
  /** Result extra. */
  public static final String EXTRA_RESULT = "EXTRA_RESULT";
  /** Indicates that service finished successfully. */
  public static final int RESULT_OK = 1;
  /** Indicates that a connection error has happened. */
  public static final int RESULT_NETWORK_FAIL = 2;
  /** Indicates that a application error has happened. */
  public static final int RESULT_APP_FAIL = 3;
  /** Indicates that a server error has happened. */
  public static final int RESULT_SERVICE_FAIL = 4;

  /** Reddit top service url. */
  private static final String REDDIT_TOP_URL = "https://www.reddit.com/top.json";
  /** Next page parameter. */
  private static final String PARAM_AFTER = "after";

  /** Response parser. */
  private ApiResponseParser mParser;

  /** Constructor. */
  public ApiIntentService() {
    super(ApiIntentService.class.getSimpleName());
    mParser = new ApiResponseParser();
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    String nextPageId = intent.getStringExtra(EXTRA_NEXT_PAGE_ID);
    StringBuilder urlString = new StringBuilder(REDDIT_TOP_URL);
    if (nextPageId != null) {
      urlString.append("?").append(PARAM_AFTER).append("=").append(nextPageId);
    }

    Bundle result = new Bundle();
    InputStream in = null;
    try {
      // Setup connection.
      URL url = new URL(urlString.toString());
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setReadTimeout(10000);
      connection.setConnectTimeout(15000);
      connection.setRequestMethod("GET");
      connection.setDoInput(true);

      // Do request.
      connection.connect();
      int response = connection.getResponseCode();

      // Check response.
      Log.d(TAG, "The response in: " + response);
      if (response == HttpsURLConnection.HTTP_OK) {
        in = connection.getInputStream();
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        // If it in the first page remove previous data.
        if (nextPageId == null) {
          operations.add(ContentProviderOperation.newDelete(
              AppContract.Entries.buildUri()).build());
        }
        // Parse response.
        String newNextPageId = mParser.parse(in, operations);
        // Execute operations.
        getContentResolver().applyBatch(AppContract.CONTENT_AUTHORITY,
            operations);
        result.putString(EXTRA_NEXT_PAGE_ID, newNextPageId);
        result.putInt(EXTRA_RESULT, RESULT_OK);
      } else {
        result.putInt(EXTRA_RESULT, RESULT_SERVICE_FAIL);
      }
    } catch (IOException e) {
      Log.e(TAG, "Connection error", e);
      result.putInt(EXTRA_RESULT, RESULT_NETWORK_FAIL);
    } catch (Exception e) {
      Log.e(TAG, "App error", e);
      result.putInt(EXTRA_RESULT, RESULT_APP_FAIL);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }

    // Notify results.
    Intent resultIntent = new Intent(ACTION_SERVICE_FINISHED);
    resultIntent.putExtras(result);
    sendBroadcast(resultIntent);
  }
}
