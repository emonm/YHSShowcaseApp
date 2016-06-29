package com.yahoo.search.yhssearch;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by surendar on 6/3/16.
 */
public class Content extends ScrollView
        implements View.OnSystemUiVisibilityChangeListener, View.OnClickListener {
    TextView mText;
    TextView mTitleView;
    SeekBar mSeekView;
    boolean mNavVisible;
    int mBaseSystemUiVisibility = SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | SYSTEM_UI_FLAG_LAYOUT_STABLE;
    int mLastSystemUiVis;

    Runnable mNavHider = new Runnable() {
        @Override public void run() {
            setNavVisibility(false);
        }
    };

    public Content(Context context, AttributeSet attrs) {
        super(context, attrs);

        mText = new TextView(context);
        mText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mText.setText("simple content");
        mText.setClickable(false);
        mText.setOnClickListener(this);
        mText.setTextIsSelectable(true);
        addView(mText, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setOnSystemUiVisibilityChangeListener(this);
    }

    public void init(TextView title, SeekBar seek) {
        // This called by the containing activity to supply the surrounding
        // state of the content browser that it will interact with.
        mTitleView = title;
        mSeekView = seek;
        setNavVisibility(true);
    }

    @Override public void onSystemUiVisibilityChange(int visibility) {
        // Detect when we go out of low-profile mode, to also go out
        // of full screen.  We only do this when the low profile mode
        // is changing from its last state, and turning off.
        int diff = mLastSystemUiVis ^ visibility;
        mLastSystemUiVis = visibility;
        if ((diff&SYSTEM_UI_FLAG_LOW_PROFILE) != 0
                && (visibility&SYSTEM_UI_FLAG_LOW_PROFILE) == 0) {
            setNavVisibility(true);
        }

    }

    @Override protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        // When we become visible, we show our navigation elements briefly
        // before hiding them.
        setNavVisibility(true);
        getHandler().postDelayed(mNavHider, 2000);
    }

    @Override protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        // When the user scrolls, we hide navigation elements.
        setNavVisibility(false);
    }

    @Override public void onClick(View v) {
        // When the user clicks, we toggle the visibility of navigation elements.
        int curVis = getSystemUiVisibility();
        setNavVisibility((curVis&SYSTEM_UI_FLAG_LOW_PROFILE) != 0);
    }

    void setBaseSystemUiVisibility(int visibility) {
        mBaseSystemUiVisibility = visibility;
    }

    void setNavVisibility(boolean visible) {
        int newVis = mBaseSystemUiVisibility;
        if (!visible) {
            newVis |= SYSTEM_UI_FLAG_LOW_PROFILE | SYSTEM_UI_FLAG_FULLSCREEN;
        }
        final boolean changed = newVis == getSystemUiVisibility();

        // Unschedule any pending event to hide navigation if we are
        // changing the visibility, or making the UI visible.
        if (changed || visible) {
            Handler h = getHandler();
            if (h != null) {
                h.removeCallbacks(mNavHider);
            }
        }

        // Set the new desired visibility.
        setSystemUiVisibility(newVis);
//        mTitleView.setVisibility(visible ? VISIBLE : INVISIBLE);
//        mSeekView.setVisibility(visible ? VISIBLE : INVISIBLE);
    }
}
