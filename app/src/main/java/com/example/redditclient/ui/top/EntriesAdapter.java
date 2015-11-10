package com.example.redditclient.ui.top;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.redditclient.R;
import com.example.redditclient.provider.AppContract;
import com.example.redditclient.ui.widget.LoadingImageView;
import com.example.redditclient.utils.ImageLoaderUtils;

/**
 * Adapter for reddit entries.
 */
public class EntriesAdapter extends PagedCursorAdapter {
  /** Thumbnail click listener. */
  private OnThumbnailClickListener mListener;

  /** Constructor. */
  public EntriesAdapter(Context context) {
    super(context);
  }

  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(
        Context.LAYOUT_INFLATER_SERVICE);
    View view = inflater.inflate(R.layout.entry_item, parent, false);
    ViewHolder holder = new ViewHolder();
    holder.image = (LoadingImageView) view.findViewById(R.id.entry_item_image);
    holder.title = (TextView) view.findViewById(R.id.entry_item_title);
    holder.details = (TextView) view.findViewById(R.id.entry_item_details);
    holder.comments = (TextView) view.findViewById(R.id.entry_item_comments);
    view.setTag(holder);
    return view;
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    String title = cursor.getString(cursor.getColumnIndex(
        AppContract.Entries.TITLE));
    String author = cursor.getString(cursor.getColumnIndex(
        AppContract.Entries.AUTHOR));
    long date = cursor.getLong(cursor.getColumnIndex(
        AppContract.Entries.ENTRY_DATE));
    String thumbnail = cursor.getString(cursor.getColumnIndex(
        AppContract.Entries.THUMBNAIL));
    int comments = cursor.getInt(cursor.getColumnIndex(
        AppContract.Entries.COMMENTS_COUNT));
    final String picture = cursor.getString(cursor.getColumnIndex(
        AppContract.Entries.PICTURE));

    // Get holder.
    ViewHolder holder = (ViewHolder) view.getTag();

    // Set title.
    holder.title.setText(title);

    // Format date.
    CharSequence formattedDate = DateUtils.getRelativeDateTimeString(context,
        date * 1000, DateUtils.MINUTE_IN_MILLIS,
        DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
    // Format details.
    String details = context.getString(R.string.entry_details, formattedDate,
        author);
    // Set details.
    holder.details.setText(details);

    // Format comments.
    String formattedComments = context.getString(R.string.entry_comments,
        comments);
    // Set comments.
    holder.comments.setText(formattedComments);

    // Set thumbnail.
    if (TextUtils.isEmpty(thumbnail)) {
      holder.image.getImageView().setImageBitmap(null);
      holder.image.setOnClickListener(null);
    } else {
      ImageLoaderUtils.displayImage(thumbnail, holder.image);
      if (TextUtils.isEmpty(picture)) {
        holder.image.setOnClickListener(null);
      } else {
        holder.image.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (mListener != null) {
              mListener.onThumbnailClick(picture);
            }
          }
        });
      }
    }
  }

  /**
   * Sets the OnThumbnailClickListener.
   * @param listener the listener.
   */
  public void setOnThumbnailClickListener(OnThumbnailClickListener listener) {
    mListener = listener;
  }

  /** View holder. */
  private static class ViewHolder {
    LoadingImageView image;
    TextView title;
    TextView details;
    TextView comments;
  }

  public interface OnThumbnailClickListener {
    void onThumbnailClick(String picture);
  }
}
