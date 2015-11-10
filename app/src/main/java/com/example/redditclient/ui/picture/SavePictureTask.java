package com.example.redditclient.ui.picture;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.redditclient.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Async task which downloads an image, save it in external storage and add it
 * to galley.
 */
public class SavePictureTask extends AsyncTask<String, Void, Void> {
  /** Log tag. */
  private static final String TAG = SavePictureTask.class.getSimpleName();
  /** Download buffer size. */
  private static final int BUFFER_SIZE = 1024;
  /** Context. */
  private Context mContext;

  /** Constructor. */
  public SavePictureTask(Context context) {
    mContext = context;
  }

  @Override
  protected Void doInBackground(String... params) {
    String urlString = params[0];
    // Get root directory.
    File root = Environment.getExternalStorageDirectory();
    File redditRoot = new File(root, "reddit");
    if (!redditRoot.exists()) {
      redditRoot.mkdirs();
    }
    // Create image file.
    String fileName = urlString.substring(urlString.lastIndexOf("/"),
        urlString.indexOf("?"));
    File imageFile = new File(redditRoot, fileName);

    FileOutputStream out = null;
    InputStream in = null;
    try {
      // Get input stream.
      URL url = new URL(urlString);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      in = connection.getInputStream();

      // Get output stream.
      out = new FileOutputStream(imageFile);

      // Add notification.
      notifyDownload(0);

      // Download.
      int total = connection.getContentLength();
      int current = 0;
      final byte[] bytes = new byte[BUFFER_SIZE];
      int count;
      while ((count = in.read(bytes, 0, BUFFER_SIZE)) != -1) {
        out.write(bytes, 0, count);
        // Update progress.
        current += count;
        notifyDownload(current * 100 / total);
      }
      out.flush();

      // Add image to gallery.
      ContentValues values = new ContentValues();
      values.put(MediaStore.Images.Media.DATE_TAKEN,
          System.currentTimeMillis());
      values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
      values.put(MediaStore.MediaColumns.DATA, imageFile.getAbsolutePath());
      mContext.getContentResolver().insert(
          MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

      // Notify finish.
      notifyDownloadFinished();

    } catch (IOException e) {
      Log.e(TAG, "Error downloading image.", e);
      notifyDownloadFail();
      if (imageFile.exists()) {
        imageFile.delete();
      }
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
      if (out != null) {
        try {
          out.close();
        } catch (IOException e) {
        }
      }
    }
    return null;
  }

  /** Add a notification with current progress. */
  private void notifyDownload(int progress) {
    NotificationManager manager = (NotificationManager) mContext
        .getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(
        mContext);
    builder.setContentTitle(mContext.getString(R.string.notification_download_tile))
        .setContentText(mContext.getString(R.string.notification_download_progress))
        .setSmallIcon(android.R.drawable.ic_menu_gallery)
        .setProgress(100, progress, false);
    manager.notify(1, builder.build());
  }

  /** Add or update a notification with finish status. */
  private void notifyDownloadFinished() {
    NotificationManager manager = (NotificationManager) mContext
        .getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(
        mContext);
    builder.setContentTitle(mContext.getString(R.string.notification_download_tile))
        .setContentText(mContext.getString(R.string.notification_download_finished))
        .setSmallIcon(android.R.drawable.ic_menu_gallery)
        .setProgress(0, 0, false);
    manager.notify(1, builder.build());
  }

  /** Add or update a notification with fail status. */
  private void notifyDownloadFail() {
    NotificationManager manager = (NotificationManager) mContext
        .getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationCompat.Builder builder = new NotificationCompat.Builder(
        mContext);
    builder.setContentTitle(mContext.getString(R.string.notification_download_tile))
        .setContentText(mContext.getString(R.string.notification_download_fail))
        .setSmallIcon(android.R.drawable.ic_menu_gallery)
        .setProgress(0, 0, false);
    manager.notify(1, builder.build());
  }
}
