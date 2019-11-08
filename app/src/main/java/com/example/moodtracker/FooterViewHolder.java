package com.example.moodtracker;

import android.graphics.Bitmap;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This returns the image view from the footer
 * @author xuhf0429
 */

public class FooterViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;

    private View mConvertView;

    /**
     * return the footer view
     * @param itemView item in the footer view
     */
    public FooterViewHolder(@NonNull View itemView) {
        super(itemView);

        this.mConvertView = itemView;
        this.mViews = new SparseArray<View>();
    }

    /**
     * get the data of the index
     * @param viewId
     * @param <T> the data set storing data in memory
     * @return the data of the indexed object
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
     * Get the data of the view
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
     * Returns the data of the view
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
     *
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
     *
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
