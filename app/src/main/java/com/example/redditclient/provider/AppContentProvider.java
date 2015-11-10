package com.example.redditclient.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.redditclient.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Application {@link android.content.ContentProvider}.
 */
public class AppContentProvider extends ContentProvider {
  /** Log TAG. */
  private static final String TAG = AppContentProvider.class.getSimpleName();
  /**
   * {@link android.content.UriMatcher} to determine what is requested to this
   * {@link android.content.ContentProvider}.
   */
  private static final UriMatcher sUriMatcher = buildUriMatcher();
  /** URI ID to get all entries. */
  private static final int ENTRIES = 100;
  /** URI ID to get an entry. */
  private static final int ENTRIES_ID = 101;
  /** URI ID to get all cache states. */
  private static final int CACHE_STATE = 200;
  /** URI ID to get a cache state. */
  private static final int CACHE_STATE_ID = 201;

  /** Local DB Helper */
  private AppDatabase mOpenHelper;

  /**
   * Build and return a {@link android.content.UriMatcher} that catches all
   * {@link android.net.Uri} variations supported by this {@link
   * android.content.ContentProvider}.
   */
  private static UriMatcher buildUriMatcher() {
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = AppContract.CONTENT_AUTHORITY;

    matcher.addURI(authority, AppContract.Entries.PATH, ENTRIES);
    matcher.addURI(authority, AppContract.Entries.PATH + "/*", ENTRIES_ID);
    matcher.addURI(authority, AppContract.CacheStates.PATH, CACHE_STATE);
    matcher.addURI(authority, AppContract.CacheStates.PATH + "/*", CACHE_STATE_ID);

    return matcher;
  }

  @Override
  public boolean onCreate() {
    mOpenHelper = new AppDatabase(getContext());
    return true;
  }

  @Override
  public String getType(Uri uri) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case ENTRIES:
        return AppContract.Entries.CONTENT_TYPE;
      case ENTRIES_ID:
        return AppContract.Entries.CONTENT_ITEM_TYPE;
      case CACHE_STATE:
        return AppContract.CacheStates.CONTENT_TYPE;
      case CACHE_STATE_ID:
        return AppContract.CacheStates.CONTENT_ITEM_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown uri: " + uri);
    }
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder) {
    // Find matching path.
    final int match = sUriMatcher.match(uri);

    // Avoid the expensive string concatenation below if not loggable
    if (BuildConfig.DEBUG) {
      Log.v(TAG, "uri=" + uri + " match=" + match + " proj="
          + Arrays.toString(projection) + " selection=" + selection
          + " args=" + Arrays.toString(selectionArgs) + ")");
    }

    // Create a selection builder from Uri.
    final SelectionBuilder builder = buildExpandedSelection(uri, match);

    // Get the database and run the query
    SQLiteDatabase db = mOpenHelper.getReadableDatabase();
    Cursor cursor = builder
        .where(selection, selectionArgs)
        .query(db, projection, sortOrder);
    // Tell the cursor what uri to watch, so it knows when its source
    // data changes
    Context context = getContext();
    if (null != context) {
      cursor.setNotificationUri(context.getContentResolver(), uri);
    }
    return cursor;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    // Find matching path.
    final int match = sUriMatcher.match(uri);

    // Avoid the expensive string concatenation below if not loggable
    if (BuildConfig.DEBUG) {
      Log.v(TAG, "insert(uri=" + uri + ", values="
          + values.toString() + ")");
    }

    // Get the database and run the insert
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    switch (match) {
      case ENTRIES: {
        db.insertOrThrow(AppDatabase.Tables.ENTRIES, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        // Generate Uri with remote id.
        return AppContract.Entries.buildUri(values.getAsString(AppContract.Entries.ID));
      }
      case CACHE_STATE: {
        long id = db.insertOrThrow(AppDatabase.Tables.CACHE_STATE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return AppContract.CacheStates.buildUri(Long.toString(id));
      }
      default: {
        throw new UnsupportedOperationException("Unknown insert uri: " + uri);
      }
    }
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    // Find matching path.
    final int match = sUriMatcher.match(uri);

    // Avoid the expensive string concatenation below if not loggable
    if (BuildConfig.DEBUG) {
      Log.v(TAG, "delete(uri=" + uri + ")");
    }

    // Create a selection builder from Uri.
    final SelectionBuilder builder = buildSimpleSelection(uri, match);

    // Get the database and run the delete
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    int count = builder.where(selection, selectionArgs).delete(db);
    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
                    String[] selectionArgs) {
    // Find matching path.
    final int match = sUriMatcher.match(uri);

    // Avoid the expensive string concatenation below if not loggable
    if (BuildConfig.DEBUG) {
      Log.v(TAG, "update(uri=" + uri + ", values="
          + values.toString() + ")");
    }

    // Create a selection builder from Uri.
    final SelectionBuilder builder = buildSimpleSelection(uri, match);

    // Get the database and run the update
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    int count = builder.where(selection, selectionArgs).update(db, values);
    getContext().getContentResolver().notifyChange(uri, null);
    return count;
  }

  /** Transactional implementation of applyBatch. */
  @Override
  public ContentProviderResult[] applyBatch(
      ArrayList<ContentProviderOperation> operations)
      throws OperationApplicationException {
    ContentProviderResult[] result = new ContentProviderResult[operations
        .size()];
    // Opens the database object in "write" mode.
    SQLiteDatabase db = mOpenHelper.getWritableDatabase();
    // Begin a transaction
    db.beginTransaction();
    try {
      int i = 0;
      for (ContentProviderOperation operation : operations) {
        // Chain the result for back references
        result[i++] = operation.apply(this, result, i);
      }
      db.setTransactionSuccessful();
    } finally {
      db.endTransaction();
    }
    return result;
  }

  /**
   * Build a simple {@link SelectionBuilder} to match the requested {@link
   * android.net.Uri}. This is usually enough to support {@link #insert}, {@link
   * #update}, and {@link #delete} operations.
   */
  private SelectionBuilder buildSimpleSelection(Uri uri, int match) {
    final SelectionBuilder builder = new SelectionBuilder();
    switch (match) {
      case ENTRIES: {
        return builder.table(AppDatabase.Tables.ENTRIES);
      }
      case ENTRIES_ID: {
        final String id = AppContract.Entries.getId(uri);
        return builder.table(AppDatabase.Tables.ENTRIES)
            .where(AppContract.Entries.ID + "=?", id);
      }
      case CACHE_STATE: {
        return builder.table(AppDatabase.Tables.CACHE_STATE);
      }
      case CACHE_STATE_ID: {
        final String id = AppContract.CacheStates.getId(uri);
        return builder.table(AppDatabase.Tables.CACHE_STATE)
            .where(AppContract.CacheStates._ID + "=?", id);
      }
      default: {
        throw new UnsupportedOperationException("Unknown uri for "
            + match + ": " + uri);
      }
    }
  }

  /**
   * Build an advanced {@link SelectionBuilder} to match the requested {@link
   * android.net.Uri}. This is usually only used by {@link #query}, since it
   * performs table joins useful for {@link android.database.Cursor} data.
   */
  private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
    switch (match) {
      case ENTRIES:
      case ENTRIES_ID:
      case CACHE_STATE:
      case CACHE_STATE_ID: {
        return buildSimpleSelection(uri, match);
      }
      default: {
        throw new UnsupportedOperationException("Unknown uri: " + uri);
      }
    }
  }
}
