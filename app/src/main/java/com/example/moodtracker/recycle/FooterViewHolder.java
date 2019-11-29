package com.example.moodtracker.recycle;

import android.graphics.Bitmap;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This handles with the footer of
 * the view of activities
 */
public class FooterViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;

    private View mConvertView;

    /**
     * Set footer view
     * @param itemView
     */
    public FooterViewHolder(@NonNull View itemView) {
        super(itemView);

        this.mConvertView = itemView;
        this.mViews = new SparseArray<View>();
    }

    /**
     * Set view
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * set textview
     * @param viewId
     * @param text
     * @return
     */
    public RecyclerView.ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * set image view
     * @param viewId
     * @param drawableId
     * @return
     */
    public RecyclerView.ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);

        return this;
    }

    /**
     * set general view of images
     * @param viewId
     * @param bm
     * @return
     */
    public RecyclerView.ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * Set image view from url
     * @param viewId
     * @param url
     * @return
     */
    public RecyclerView.ViewHolder setImageByUrl(int viewId, String url) {
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(url,
                (ImageView) getView(viewId));
        return this;
    }

}
