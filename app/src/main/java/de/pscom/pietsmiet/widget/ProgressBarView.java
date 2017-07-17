package de.pscom.pietsmiet.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.animation.Animation;

/**
 * Created by saibotk on 14.07.2017.
 */

public class ProgressBarView extends View {
    @NonNull
    private Paint mPaint;
    private Animation.AnimationListener mListener;

    private int mHeight;

    public ProgressBarView(Context context) {
        super(context);
        initView();
    }

    public ProgressBarView(Context context, @Nullable int color) {
        super(context);
        initView();
        mPaint.setColor(color);
    }

    private void initView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // todo read doc about flag
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    public void setAnimationListener(Animation.AnimationListener listener) {
        mListener = listener;
    }

    @Override
    public void onAnimationStart() {
        super.onAnimationStart();
        if (mListener != null) {
            mListener.onAnimationStart(getAnimation());
        }
    }

    /**
     * Update the background color of the circle image view.
     *
     * @param colorRes Id of a color resource.
     */
    public void setBackgroundColorRes(int colorRes) {
        setBackgroundColor(ContextCompat.getColor(getContext(), colorRes));
    }

    @Override
    public void setBackgroundColor(int color) {
        if (getBackground() instanceof ShapeDrawable) {
            ((ShapeDrawable) getBackground()).getPaint().setColor(color);
        }
    }

    @Override
    public void onAnimationEnd() {
        super.onAnimationEnd();
        if (mListener != null) {
            mListener.onAnimationEnd(getAnimation());
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw progressbars progress
        //todo create some kind of Listener -> manage state / refresh perc etc
        canvas.drawRect(0, 0, 10, 0, mPaint);
    }

    public void setMaxHeight(int mHeight) {
        this.mHeight = mHeight;
    }
}
