package com.example.redditclient.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.redditclient.R;

/**
 * This view provides an image view and a loading view.
 */
public class LoadingImageView extends FrameLayout {
    /** Array to map scale type and index. */
    private static final ImageView.ScaleType[] sScaleTypeArray = {
            ImageView.ScaleType.MATRIX,
            ImageView.ScaleType.FIT_XY,
            ImageView.ScaleType.FIT_START,
            ImageView.ScaleType.FIT_CENTER,
            ImageView.ScaleType.FIT_END,
            ImageView.ScaleType.CENTER,
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE
    };
    /** Image view. */
    private ImageView mImageView;
    /** Loading view. */
    private ProgressBar mLoadingView;

    /** Constructor. */
    public LoadingImageView(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    /** Constructor. */
    public LoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    /** Constructor. */
    public LoadingImageView(Context context, AttributeSet attrs,
        int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    /** Constructor. */
    @SuppressWarnings("unused")
    @TargetApi(21)
    public LoadingImageView(Context context, AttributeSet attrs,
        int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /** Initialize the view. */
    private void init(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        // Get attributes.
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.LoadingImageView, defStyleAttr, defStyleRes);
        final int scaleTypeIndex = a.getInt(
                R.styleable.LoadingImageView_scaleType, -1);
        final Drawable srcDrawable = a.getDrawable(
                R.styleable.LoadingImageView_src);
        a.recycle();

        // Inflate view.
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_loading_image_view, this);
        mImageView = (ImageView) findViewById(R.id.loading_image_image);
        mLoadingView = (ProgressBar) findViewById(R.id.loading_image_loading);

        // Set attributes.
        if (scaleTypeIndex >= 0) {
            mImageView.setScaleType(sScaleTypeArray[scaleTypeIndex]);
        }
        if (srcDrawable != null) {
            mImageView.setImageDrawable(srcDrawable);
        }
    }

    /** Returns the image view. */
    public ImageView getImageView() {
        return mImageView;
    }

    /** Returns the loading view. */
    public ProgressBar getLoadingView() {
        return mLoadingView;
    }
}
