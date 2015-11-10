package com.example.redditclient.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract to communicate with {@link com.example.redditclient.provider.AppContentProvider}
 */
public class AppContract {
  /** The authority for app contents. */
  public static final String CONTENT_AUTHORITY = "com.example.redditclient.provider";
  /** Base URI to access provider's content. */
  protected static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
  /** Base content type. */
  protected static final String BASE_CONTENT_TYPE = "vnd.redditclient.app.dir/vnd.redditclient.";
  /** Base item Content type. */
  protected static final String BASE_CONTENT_ITEM_TYPE = "vnd.redditclient.app.item/vnd.redditclient.";


  /** Entry columns. */
  interface EntryColumns {
    /** Entry id. */
    String ID = "id";
    /** Entry title. */
    String TITLE = "title";
    /** Entry author. */
    String AUTHOR = "author";
    /** Entry date. */
    String ENTRY_DATE = "entry_date";
    /** Entry thumbnail. */
    String THUMBNAIL = "thumbnail";
    /** Entry comment count. */
    String COMMENTS_COUNT = "comments_count";
    /** Entry comment count. */
    String PICTURE = "picture";
  }

  /** Entries contract. */
  public static class Entries implements EntryColumns, BaseColumns {

    /** Uri Path. */
    static final String PATH = "entries";

    /** Content Uri. */
    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

    /** Content type. */
    public static final String CONTENT_TYPE = BASE_CONTENT_TYPE + PATH;

    /** Item Content type. */
    public static final String CONTENT_ITEM_TYPE = BASE_CONTENT_ITEM_TYPE + PATH;

    /** Default projection. */
    public static final String[] DEFAULT_PROJECTION = new String[]{
        _ID, ID, TITLE, AUTHOR, ENTRY_DATE, THUMBNAIL, COMMENTS_COUNT, PICTURE};

    /** Default "ORDER BY" clause. */
    public static final String DEFAULT_SORT = _ID + " ASC";

    /** Build {@link android.net.Uri} for request all entities. */
    public static Uri buildUri() {
      return CONTENT_URI.buildUpon().build();
    }

    /** Build {@link android.net.Uri} for requested entity. */
    public static Uri buildUri(String id) {
      return CONTENT_URI.buildUpon().appendPath(id).build();
    }

    /** Extract the id from given {@link android.net.Uri} */
    public static final String getId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }

  /** Cache state columns. */
  interface CacheStateColumns {
    /** Url. */
    String URL = "url";
    /** Last update time. */
    String LAST_UPDATE = "last_update";
  }

  /** Cache state  contract. */
  public static class CacheStates implements CacheStateColumns, BaseColumns {

    /** Uri Path. */
    static final String PATH = "cache_state";

    /** Content Uri. */
    public static final Uri CONTENT_URI =
        BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

    /** Content type. */
    public static final String CONTENT_TYPE = BASE_CONTENT_TYPE + PATH;

    /** Item Content type. */
    public static final String CONTENT_ITEM_TYPE = BASE_CONTENT_ITEM_TYPE + PATH;

    /** Default projection. */
    public static final String[] DEFAULT_PROJECTION = new String[]{
        _ID, URL, LAST_UPDATE};

    /** Default "ORDER BY" clause. */
    public static final String DEFAULT_SORT = _ID + " ASC";

    /** Build {@link android.net.Uri} for request all entities. */
    public static Uri buildUri() {
      return CONTENT_URI.buildUpon().build();
    }

    /** Build {@link android.net.Uri} for requested entity. */
    public static Uri buildUri(String id) {
      return CONTENT_URI.buildUpon().appendPath(id).build();
    }

    /** Extract the id from given {@link android.net.Uri} */
    public static final String getId(Uri uri) {
      return uri.getPathSegments().get(1);
    }
  }
}
