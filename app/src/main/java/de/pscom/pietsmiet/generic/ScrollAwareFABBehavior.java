package de.pscom.pietsmiet.generic;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tobias on 09.04.2017.
 * <p>
 * Source:
 * https://guides.codepath.com/android/floating-action-buttons#design-support-library
 * <p>
 * fix
 * http://stackoverflow.com/questions/41807601/onnestedscroll-called-only-once
 */

public class ScrollAwareFABBehavior extends FloatingActionButton.Behavior {
    final int NEEDED_SCROLL_LENGTH_DOWN = 20;
    final int NEEDED_SCROLL_LENGTH_UP = 20;

    public ScrollAwareFABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                                       View directTargetChild, View target, int nestedScrollAxes) {

        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
                        nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);

        if (dyConsumed < -NEEDED_SCROLL_LENGTH_UP && child.getVisibility() == View.VISIBLE) {
            child.hide(new FloatingActionButton.OnVisibilityChangedListener() {
                @Override
                public void onHidden(FloatingActionButton fab) {
                    super.onHidden(fab);
                    child.setVisibility(View.INVISIBLE);
                }
            });
        } else if (dyConsumed > NEEDED_SCROLL_LENGTH_DOWN && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }
}
