package com.example.redditclient.service;

import android.content.ContentProviderOperation;

import com.example.redditclient.provider.AppContract;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Parse the json content from a input stream and create the {@link
 * android.content.ContentProviderOperation} for the results.
 */
public class ApiResponseParser {
  /** Data json attribute. */
  private static final String DATA = "data";
  /** Children json attribute. */
  private static final String CHILDREN = "children";
  /** After json attribute. */
  private static final String AFTER = "after";
  /** Id json attribute. */
  private static final String ID = "id";
  /** Title json attribute. */
  private static final String TITLE = "title";
  /** Author json attribute. */
  private static final String AUTHOR = "author";
  /** Created utc json attribute. */
  private static final String CREATED_UTC = "created_utc";
  /** Thumbnail json attribute. */
  private static final String THUMBNAIL = "thumbnail";
  /** Comments json attribute. */
  private static final String NUM_COMMENTS = "num_comments";
  /** Preview json attribute. */
  private static final String PREVIEW = "preview";
  /** Images json attribute. */
  private static final String IMAGES = "images";
  /** Source json attribute. */
  private static final String SOURCE = "source";
  /** Url json attribute. */
  private static final String URL = "url";

  /**
   * Parse the json content from input stream and returns the content provider
   * operations for parsed entries.
   * @param in the input stream.
   * @param operations operation list.
   * @return the id of next page.
   * @throws IOException in case of connection error.
   */
  public String parse(InputStream in,
      ArrayList<ContentProviderOperation> operations)
      throws IOException {
    String after = null;
    JsonReader reader = new JsonReader(new InputStreamReader(in));
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      switch (name) {
        case DATA:
          after = parseData(reader, operations);
          break;
        default:
          reader.skipValue();
          break;
      }
    }
    reader.endObject();
    return after;
  }

  /** Parse data object. */
  private String parseData(JsonReader reader,
      ArrayList<ContentProviderOperation> operations) throws IOException {
    String after = null;
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      switch (name) {
        case CHILDREN:
          reader.beginArray();
          while (reader.hasNext()) {
            parseChild(reader, operations);
          }
          reader.endArray();
          break;
        case AFTER:
          after = nextStringSafe(reader);
          break;
        default:
          reader.skipValue();
          break;
      }
    }
    reader.endObject();
    return after;
  }

  /** Parse a child. */
  private void parseChild(JsonReader reader,
      ArrayList<ContentProviderOperation> operations) throws IOException {
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      switch (name) {
        case DATA:
          parseChildData(reader, operations);
          break;
        default:
          reader.skipValue();
          break;
      }
    }
    reader.endObject();
  }

  /** Parse child data object. */
  private void parseChildData(JsonReader reader,
      ArrayList<ContentProviderOperation> operations) throws IOException {
    // Create a insert.
    ContentProviderOperation.Builder builder = ContentProviderOperation
        .newInsert(AppContract.Entries.buildUri());

    // Parse entry.
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      switch (name) {
        case ID:
          builder.withValue(AppContract.Entries.ID, nextStringSafe(reader));
          break;
        case TITLE:
          builder.withValue(AppContract.Entries.TITLE, nextStringSafe(reader));
          break;
        case AUTHOR:
          builder.withValue(AppContract.Entries.AUTHOR, nextStringSafe(reader));
          break;
        case CREATED_UTC:
          builder.withValue(AppContract.Entries.ENTRY_DATE, nextIntegerSafe(reader));
          break;
        case THUMBNAIL:
          builder.withValue(AppContract.Entries.THUMBNAIL, nextStringSafe(reader));
          break;
        case NUM_COMMENTS:
          builder.withValue(AppContract.Entries.COMMENTS_COUNT, nextIntegerSafe(reader));
          break;
        case PREVIEW:
          builder.withValue(AppContract.Entries.PICTURE, parsePreview(reader));
          break;
        default:
          reader.skipValue();
          break;
      }
    }
    reader.endObject();

    // Add the insert.
    operations.add(builder.build());
  }

  /** Parse preview object. */
  private String parsePreview(JsonReader reader) throws IOException {
    String picture = null;
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      switch (name) {
        case IMAGES:
          reader.beginArray();
          while (reader.hasNext()) {
            picture = parseImage(reader);
          }
          reader.endArray();
          break;
        default:
          reader.skipValue();
          break;
      }
    }
    reader.endObject();
    return picture;
  }

  /** Parse image object. */
  private String parseImage(JsonReader reader) throws IOException {
    String picture = null;
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      switch (name) {
        case SOURCE:
          picture = parseSource(reader);
          break;
        default:
          reader.skipValue();
          break;
      }
    }
    reader.endObject();
    return picture;
  }

  /** Parse source object. */
  private String parseSource(JsonReader reader) throws IOException {
    String picture = null;
    reader.beginObject();
    while (reader.hasNext()) {
      String name = reader.nextName();
      switch (name) {
        case URL:
          picture = nextStringSafe(reader);
          break;
        default:
          reader.skipValue();
          break;
      }
    }
    reader.endObject();
    return picture;
  }

  /**
   * Utility to read an string safely in case of null content.
   * @param reader reader with the content to onParseResponse.
   * @return a string or null case of null content.
   * @throws java.io.IOException in case of error reading from stream.
   */
  private String nextStringSafe(final JsonReader reader) throws
      IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }
    return reader.nextString();
  }

  /**
   * Utility to read an integer safely in case of null content.
   * @param reader reader with the content to onParseResponse.
   * @return a boolean or null case of null content.
   * @throws java.io.IOException in case of error reading from stream.
   */
  private Integer nextIntegerSafe(final JsonReader reader) throws
      IOException {
    if (reader.peek() == JsonToken.NULL) {
      reader.nextNull();
      return null;
    }
    return reader.nextInt();
  }
}
