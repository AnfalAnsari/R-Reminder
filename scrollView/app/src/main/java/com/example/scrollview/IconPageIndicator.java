package com.example.scrollview;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.scrollview.model.events;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Preconditions;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.PageIndicator;

import java.io.InputStream;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import static com.example.scrollview.model.events.getCategories;

//modified version of https://github.com/JakeWharton/ViewPagerIndicator/blob/master/library/src/com/viewpagerindicator/IconPageIndicator.java
public class IconPageIndicator extends HorizontalScrollView implements PageIndicator {
    private static final String TAG = "IconPageIndicator";
    private LinearLayout mIconsLayout;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mListener;
    private Runnable mIconSelector;
    private int mSelectedIndex;
    private float percentExpanded;

    public IconPageIndicator(Context context) {
        super(context, null);
        init();
    }

    public IconPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconPageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public IconPageIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setHorizontalScrollBarEnabled(false);
        mIconsLayout = new LinearLayout(getContext());
        mIconsLayout.setClipChildren(false);
        addView(mIconsLayout, new LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
    }

    private void animateToIcon(final int position) {
        if (mIconSelector != null) {
            removeCallbacks(mIconSelector);
        }
        mIconSelector = new Runnable() {
            public void run() {
                updateScroll();
            }
        };
        post(mIconSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mIconSelector != null) {
            // Re-post the selector we saved
            post(mIconSelector);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mIconSelector != null) {
            removeCallbacks(mIconSelector);
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
        }
        PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        view.addOnPageChangeListener(this);
        notifyDataSetChanged();
    }


    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        initialPosition+=1;
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    public void notifyDataSetChanged() {
        mIconsLayout.removeAllViews();
        boolean changePadding = true;
        IconPagerAdapter iconAdapter = (IconPagerAdapter) mViewPager.getAdapter();
        int count = iconAdapter.getCount();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < count; i++) {
            final View parent = inflater.inflate(R.layout.indicator, mIconsLayout, false);
            final View parent_bottom = inflater.inflate(R.layout.detail_page,mIconsLayout,false);
            final ImageView logoview = (ImageView) parent.findViewById(R.id.icon);
            final ImageView posterView = (ImageView) parent_bottom.findViewById(R.id.event_poster);
            //// TODO: 25/04/2017 Use ViewCompat to support pre-lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                logoview.setTransitionName("tab_" + i);
            }
            Log.i(TAG, "notifyDataSetChanged: " + new Gson().toJson(getCategories().get(i).imageUrl));

            Glide.with(getContext())
                    .asBitmap()
                    .load(getCategories().get(i).imageUrl)
                    .into(logoview);
            Glide.with(getContext())
                    .asBitmap()
                    .load(getCategories().get(i).posterUrl)
                    .into(posterView);







            if (changePadding) {
                //assuming that every indicator will have same size
                changePadding = false;
                parent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        parent.getViewTreeObserver().removeOnPreDrawListener(this);
                        int parentWidth = getWidth();
                        int width = parent.getWidth();
                        int leftRightPadding = (parentWidth - width) / 2;
                        setPadding(leftRightPadding, getPaddingTop(), leftRightPadding, getPaddingBottom());
                        return true;
                    }
                });
            }
            mIconsLayout.addView(parent);

            parent.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(mIconsLayout.indexOfChild(parent));
                }
            });
        }
        if (mSelectedIndex > count) {
            mSelectedIndex = count - 1;
        }
        setCurrentItem(mSelectedIndex);
        requestLayout();
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mSelectedIndex = item;
        mViewPager.setCurrentItem(item);

        int tabCount = mIconsLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            View child = mIconsLayout.getChildAt(i);
            boolean isSelected = (i == item);
            child.setSelected(isSelected);
            View foreground = child.findViewById(R.id.foreground);
            if (isSelected) {
                animateToIcon(item);
                foreground.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fg_white));
            } else {
                foreground.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fg_gray));
            }
        }
    }

    public void collapse(float top, float total) {

        //do not scale to 0
        float newTop = top / 1.2F;
        float scale = (total - newTop) / (float) total;
        ViewCompat.setScaleX(this, scale);
        ViewCompat.setScaleY(this, scale);
        int tabCount = mIconsLayout.getChildCount();

        //alpha can be zero
        percentExpanded = (total - top) / (float) total;
        float alpha = 1 - percentExpanded;
        for (int i = 0; i < tabCount; i++) {
            View parent = mIconsLayout.getChildAt(i);
            View child = parent.findViewById(R.id.foreground);
            ViewCompat.setAlpha(child, alpha);
        }
        updateScroll();
    }

    private void updateScroll() {
        int x = mIconsLayout.getWidth() / 2;
        int scrollTo = (int) ((x * (1 - percentExpanded)) + (percentExpanded * mIconsLayout.getChildAt(mSelectedIndex).getLeft()));
        smoothScrollTo(scrollTo, 0);
    }


    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener = listener;
    }


}
