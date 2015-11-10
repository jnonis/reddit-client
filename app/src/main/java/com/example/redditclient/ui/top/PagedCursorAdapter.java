package com.example.redditclient.ui.top;

import android.content.Context;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

/**
 * Cursor adapter with pagination support.
 */
public abstract class PagedCursorAdapter extends CursorAdapter {
  /** Loading view type. */
  private static final int VIEW_TYPE_LOADING = 1;
  /** Indicates that adapter is in loading mode. */
  private boolean mIsLoading;
  /** Indicates that there are more pages to fetch. */
  private boolean mHasMorePages = true;
  /** Pagination listener. */
  private OnPageRequestListener mOnPageRequestListener;

  /** Constructor. */
  public PagedCursorAdapter(Context context) {
    super(context, null, 0);
  }

  @Override
  public int getCount() {
    if (mIsLoading) {
      return super.getCount() + 1;
    }
    return super.getCount();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (mIsLoading && position == super.getCount()) {
      if (convertView == null) {
        convertView = new ProgressBar(mContext);
        ((ProgressBar) convertView).setIndeterminate(true);
      }
      return convertView;
    } else if (mHasMorePages && position == super.getCount() - 2) {
      mIsLoading = true;
      if (mOnPageRequestListener != null) {
        mOnPageRequestListener.onPageRequest();
      }
    }
    return super.getView(position, convertView, parent);
  }

  @Override
  public int getItemViewType(int position) {
    if (mIsLoading && position == super.getCount()) {
      return VIEW_TYPE_LOADING;
    }
    return super.getItemViewType(position);
  }

  @Override
  public int getViewTypeCount() {
    return super.getViewTypeCount() + 1;
  }

  @Override
  public Object getItem(int position) {
    return getItemViewType(position) != VIEW_TYPE_LOADING ? getItem(position) :
        null;
  }

  @Override
  public long getItemId(int position) {
    return getItemViewType(position) != VIEW_TYPE_LOADING ? position : -1;
  }

  /**
   * Allows to indicate adapter that there are no more pages.
   */
  public void setHasMorePages(boolean hasMorePages) {
    mHasMorePages = hasMorePages;
  }

  /**
   * Sets the listener to be called to request more pages.
   */
  public void setOnPageRequestListener(OnPageRequestListener listener) {
    mOnPageRequestListener = listener;
  }

  /** Called to request more pages. */
  public interface OnPageRequestListener {
    void onPageRequest();
  }
}
