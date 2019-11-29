package me.nereo.multi_image_selector.view;

import android.content.Context;
import android.util.AttributeSet;

/** An image view which always remains square with respect to its width. */
class SquaredImageView extends android.support.v7.widget.AppCompatImageView {

  /**
   * returns the context in image
   * @param context
   */
  public SquaredImageView(Context context) {
    super(context);
  }

  /**
   * set for image context and attributes
   * @param context
   * @param attrs
   */
  public SquaredImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
  }
}
