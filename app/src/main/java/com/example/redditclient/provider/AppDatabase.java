package com.example.redditclient.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Database which use SQLite and support the contract defined in {@link
 * com.example.redditclient.provider.AppContract} It support incremental
 * upgrades.
 */
public class AppDatabase extends SQLiteOpenHelper {
  /** Database Name */
  private static final String DATABASE_NAME = "application.db";
  /** Database version */
  public static final int DATABASE_VERSION = 1;

  /** DB table names. */
  interface Tables {
    String ENTRIES = "entries";
    String CACHE_STATE = "cache_state";
  }

  /** Constructor. */
  public AppDatabase(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL("CREATE TABLE " + Tables.ENTRIES + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + AppContract.EntryColumns.ID + " TEXT NOT NULL,"
        + AppContract.EntryColumns.TITLE + " TEXT,"
        + AppContract.EntryColumns.AUTHOR + " TEXT,"
        + AppContract.EntryColumns.ENTRY_DATE + " INTEGER,"
        + AppContract.EntryColumns.THUMBNAIL + " TEXT,"
        + AppContract.EntryColumns.COMMENTS_COUNT + " INTEGER,"
        + AppContract.EntryColumns.PICTURE + " TEXT,"
        + "UNIQUE (" + AppContract.EntryColumns.ID + ") ON CONFLICT " +
        "REPLACE)");

    db.execSQL("CREATE TABLE " + Tables.CACHE_STATE + " ("
        + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
        + AppContract.CacheStates.URL + " TEXT NOT NULL,"
        + AppContract.CacheStates.LAST_UPDATE + " INTEGER NOT NULL,"
        + "UNIQUE (" + AppContract.CacheStates.URL + ") ON CONFLICT " +
        "REPLACE)");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Implement incremental upgrade.
  }

  @Override
  public void onOpen(SQLiteDatabase db) {
    super.onOpen(db);
    if (!db.isReadOnly()) {
      // Enable foreign key constraints
      db.execSQL("PRAGMA foreign_keys=ON;");
    }
  }
}
