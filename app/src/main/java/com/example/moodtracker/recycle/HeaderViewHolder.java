package com.example.moodtracker.recycle;

import android.graphics.Bitmap;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> mViews;

    private View mConvertView;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        this.mConvertView = itemView;
        this.mViews = new SparseArray<View>();
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public RecyclerView.ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public RecyclerView.ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);

        return this;
    }

    public RecyclerView.ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public RecyclerView.ViewHolder setImageByUrl(int viewId, String url) {
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO).loadImage(url,
                (ImageView) getView(viewId));
        return this;
    }

}
