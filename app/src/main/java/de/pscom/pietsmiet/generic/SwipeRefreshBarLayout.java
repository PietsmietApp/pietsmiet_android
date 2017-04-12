package de.pscom.pietsmiet.generic;

import android.content.Context;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingChild;

/**
 * Created by saibotk on 10.04.2017.
 */

public class SwipeRefreshBarLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    private View mTarget; // the target of the gesture
    SwipeRefreshLayout.OnRefreshListener mListener;
    boolean mRefreshing = false;
    private int mTouchSlop;
    private float mTotalDragDistance = -1;

    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private float mTotalUnconsumed;
    //private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    //private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mNestedScrollInProgress;

    public SwipeRefreshBarLayout(Context context) {
        this(context, null);
    }

    public SwipeRefreshBarLayout(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

       // mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        if (getChildCount() == 0) {
            return;
        }
    }

    @Override
    protected void onMeasure(int wdthMesSpec, int hgtMesSpec) {
        super.onMeasure(wdthMesSpec, hgtMesSpec);

    }
}
