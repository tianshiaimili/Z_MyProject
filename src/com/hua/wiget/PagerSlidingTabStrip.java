/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hua.wiget;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.utils.LogUtils2;

/**
 * 这是首页顶部的指示器
 * @author zero
 *
 */
public class PagerSlidingTabStrip extends HorizontalScrollView {

	public interface IconTabProvider {
		public int getPageIconResId(int position);
	}

	// @formatter:off
	private static final int[] ATTRS = new int[] {
		android.R.attr.textSize,
		android.R.attr.textColor
    };
	// @formatter:on

	private LinearLayout.LayoutParams defaultTabLayoutParams;
	private LinearLayout.LayoutParams expandedTabLayoutParams;

	private final PageListener pageListener = new PageListener();
	public OnPageChangeListener delegatePageListener;

	private LinearLayout tabsContainer;
	private ViewPager pager;

	private int tabCount;

	/**
	 * 当前的position位置
	 */
	private int currentPosition = 0;
	private int selectedPosition = 0;
	/**
	 * 当前的位置偏移量
	 */
	private float currentPositionOffset = 0f;

	private Paint rectPaint;
	private Paint dividerPaint;

	private int indicatorColor = 0xFF666666;
	/**
	 * tab底部分割线的颜色
	 */
	private int underlineColor = 0x1A000000;
	
	
	private int dividerColor = 0x1A000000;

	private boolean shouldExpand = false;
	private boolean textAllCaps = true;

	private int scrollOffset = 52;
	private int indicatorHeight = 8;
	private int underlineHeight = 2;
	private int dividerPadding = 12;
	private int tabPadding = 14;
	private int dividerWidth = 1;

	private int tabTextSize = 12;
	private int tabTextColor = 0xFF666666;
	private int selectedTabTextColor = 0xFF666666;
	private Typeface tabTypeface = null;
	private int tabTypefaceStyle = Typeface.NORMAL;

	private int lastScrollX = 0;

	private int tabBackgroundResId = R.drawable.background_tab;

	private Locale locale;

	/**
	 * 标题上的图片组 未选中时
	 */
	private int[] mDrawableIds = new int[] {R.drawable.bg_home,
			R.drawable.bg_category, R.drawable.bg_collect,
			R.drawable.bg_setting };
	
	/**
	 * 标题上的图片组 未选中后
	 */
	private int[] mDrawableIdsSleect = new int[] {R.drawable.home_selected,
			R.drawable.category_selected, R.drawable.collect_selected,
			R.drawable.setting_selected };
	
	/**
	 * 标题组
	 */
	private final String[] titles = { "精选", "发现", "榜单", "团购" };
	
	/**
	 * 定义一个全局的上下文
	 */
	private Context context ;
	
	public PagerSlidingTabStrip(Context context) {
		this(context, null);
	}

	public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		setFillViewport(true);
		setWillNotDraw(false);

		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(tabsContainer);

		DisplayMetrics dm = getResources().getDisplayMetrics();
//		DisplayMetrics metrics = getResources().getDisplayMetrics();
	
		/**
		 * 这段似乎没多大用处
		 */
		scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
		indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
		LogUtils2.i("underlineHeight------="+underlineHeight);
		underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
		LogUtils2.i("underlineHeight=="+underlineHeight);
		dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
		tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
		dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
		//tab上字的大小
		tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);
////////////////////
		// get system attrs (android:textSize and android:textColor)

		TypedArray typedArray = context.obtainStyledAttributes(attrs, ATTRS);
		LogUtils2.i("tabTextSize=="+tabTextSize);
		tabTextSize = typedArray.getDimensionPixelSize(0, tabTextSize);
		LogUtils2.i("tabTextSize--------=="+tabTextSize);
		tabTextColor = typedArray.getColor(1, tabTextColor);
		
		typedArray.recycle();

		// get custom attrs

		typedArray = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

		indicatorColor = typedArray.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
		underlineColor = typedArray.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
		dividerColor = typedArray.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
		indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
		underlineHeight = typedArray.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
		dividerPadding = typedArray.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
		tabPadding = typedArray.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPadding);
		tabBackgroundResId = typedArray.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackgroundResId);
		shouldExpand = typedArray.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
		scrollOffset = typedArray.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
		textAllCaps = typedArray.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

		typedArray.recycle();

		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Style.FILL);

		dividerPaint = new Paint();
		dividerPaint.setAntiAlias(true);
		dividerPaint.setStrokeWidth(dividerWidth);

		defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

		if (locale == null) {
			locale = getResources().getConfiguration().locale;
		}
	}

	public void setViewPager(ViewPager pager) {
		this.pager = pager;

		if (pager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}

		pager.setOnPageChangeListener(pageListener);

		notifyDataSetChanged();
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}

	/**
	 * 数据改变 通知刷新 
	 */
	public void notifyDataSetChanged() {

		tabsContainer.removeAllViews();

		tabCount = pager.getAdapter().getCount();

		for (int i = 0; i < tabCount; i++) {

			if (pager.getAdapter() instanceof IconTabProvider) {
				addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
			} else {
				addTextTab(i, titles[i]);
			}

		}

		updateTabStyles();

		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				
				LogUtils2.i("getViewTreeObserver........");
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
				currentPosition = pager.getCurrentItem();
				scrollToChild(currentPosition, 0);
			}
		});

	}

	private void addTextTab(final int position, String title) {

		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setGravity(Gravity.CENTER);
		tab.setSingleLine();
		tab.setPadding(0, 0, 0, 0);
		addTab(position, tab);
	}

	private void addIconTab(final int position, int resId) {

		ImageButton tab = new ImageButton(getContext());
		tab.setImageResource(resId);

		addTab(position, tab);

	}

	@SuppressLint("NewApi")
	private void addTab(final int position, View tab) {
		tab.setFocusable(true);
//		((TextView)(tab)).setCompoundDrawablesRelativeWithIntrinsicBounds(
//				
//				context.getResources().getDrawable(mDrawableIds[position]), null, null, null);
		
		((TextView)(tab)).setCompoundDrawablesWithIntrinsicBounds(
				context.getResources().getDrawable(mDrawableIds[position]), null, null, null);
		
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(position);
			}
		});

		tab.setPadding(tabPadding, 0, tabPadding, 0);
		tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
	}

	private void updateTabStyles() {

		for (int i = 0; i < tabCount; i++) {

			View v = tabsContainer.getChildAt(i);

			v.setBackgroundResource(tabBackgroundResId);
			selectedTabTextColor = context.getResources().getColor(R.color.homefragment_top_title_color);
			if (v instanceof TextView) {

				TextView tab = (TextView) v;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
				tab.setTypeface(tabTypeface, tabTypefaceStyle);
				tab.setTextColor(tabTextColor);
				tab.setCompoundDrawablesWithIntrinsicBounds(
						context.getResources().getDrawable(mDrawableIds[i]),
						null, null, null);

				// setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
				// pre-ICS-build
				if (textAllCaps) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
						tab.setAllCaps(true);
					} else {
						tab.setText(tab.getText().toString().toUpperCase(locale));
					}
				}
				if (i == selectedPosition) {
					tab.setTextColor(selectedTabTextColor);
					tab.setCompoundDrawablesWithIntrinsicBounds(
							context.getResources().getDrawable(mDrawableIdsSleect[i]),
							null, null, null);
				}
			}
		}

	}

	private void scrollToChild(int position, int offset) {

		LogUtils2.i("position=="+position+"   offset=="+offset);
		
		if (tabCount == 0) {
			return;
		}

		int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;
		LogUtils2.i("newScrollX==="+newScrollX);
		if (position > 0 || offset > 0) {
			newScrollX -= scrollOffset;
			LogUtils2.i("newScrollX -= scrollOffset==="+newScrollX);
		}

		if (newScrollX != lastScrollX) {
			LogUtils2.i("newScrollX=="+newScrollX+"  lastScrollX=="+lastScrollX);
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		LogUtils2.i("onDraw------------------------");
		
		if (isInEditMode() || tabCount == 0) {
			return;
		}

		final int height = getHeight();
		LogUtils2.w("getHeight();=="+getHeight());
		
		// draw underline Tab 和viewpager间的分割线
		rectPaint.setColor(underlineColor);
		canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

		// draw indicator line
		rectPaint.setColor(indicatorColor);

		// default: line below current tab
		View currentTab = tabsContainer.getChildAt(currentPosition);
		float lineLeft = currentTab.getLeft();
		float lineRight = currentTab.getRight();

		// if there is an offset, start interpolating left and right coordinates between current and next tab
		if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

			View nextTab = tabsContainer.getChildAt(currentPosition + 1);
			final float nextTabLeft = nextTab.getLeft();
			final float nextTabRight = nextTab.getRight();
			LogUtils2.d("currentPositionOffset=="+currentPositionOffset);
			LogUtils2.d("nextTabLeft=="+nextTabLeft);
			LogUtils2.d("lineLeft=="+lineLeft);
			
			lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
			lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
	
//			lineLeft = (currentPositionOffset * nextTabLeft) + (1f - currentPositionOffset) * lineLeft;
		
		}

		canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

		// draw divider

		dividerPaint.setColor(dividerColor);
		for (int i = 0; i < tabCount - 1; i++) {
			View tab = tabsContainer.getChildAt(i);
			canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
		}
	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			
			LogUtils2.i("**********onPageScrolled*****");
			
			currentPosition = position;
			currentPositionOffset = positionOffset;

			scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

			invalidate();

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				scrollToChild(pager.getCurrentItem(), 0);
			}

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			selectedPosition = position;
			updateTabStyles();
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}
			
		}

	}

	public void setIndicatorColor(int indicatorColor) {
		this.indicatorColor = indicatorColor;
		invalidate();
	}

	public void setIndicatorColorResource(int resId) {
		this.indicatorColor = getResources().getColor(resId);
		invalidate();
	}

	public int getIndicatorColor() {
		return this.indicatorColor;
	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.indicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public int getIndicatorHeight() {
		return indicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.underlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.underlineColor = getResources().getColor(resId);
		invalidate();
	}

	public int getUnderlineColor() {
		return underlineColor;
	}

	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
		invalidate();
	}

	public void setDividerColorResource(int resId) {
		this.dividerColor = getResources().getColor(resId);
		invalidate();
	}

	public int getDividerColor() {
		return dividerColor;
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.underlineHeight = underlineHeightPx;
		invalidate();
	}

	public int getUnderlineHeight() {
		return underlineHeight;
	}

	public void setDividerPadding(int dividerPaddingPx) {
		this.dividerPadding = dividerPaddingPx;
		invalidate();
	}

	public int getDividerPadding() {
		return dividerPadding;
	}

	public void setScrollOffset(int scrollOffsetPx) {
		this.scrollOffset = scrollOffsetPx;
		invalidate();
	}

	public int getScrollOffset() {
		return scrollOffset;
	}

	public void setShouldExpand(boolean shouldExpand) {
		this.shouldExpand = shouldExpand;
		notifyDataSetChanged();
	}

	public boolean getShouldExpand() {
		return shouldExpand;
	}

	public boolean isTextAllCaps() {
		return textAllCaps;
	}

	public void setAllCaps(boolean textAllCaps) {
		this.textAllCaps = textAllCaps;
	}

	public void setTextSize(int textSizePx) {
		this.tabTextSize = textSizePx;
		updateTabStyles();
	}
	
	public int getTextSize() {
		return tabTextSize;
	}

	public void setTextColor(int textColor) {
		this.tabTextColor = textColor;
		updateTabStyles();
	}
	
	public void setTextColorResource(int resId) {
		this.tabTextColor = getResources().getColor(resId);
		updateTabStyles();
	}

	public int getTextColor() {
		return tabTextColor;
	}
	
	public void setSelectedTextColor(int textColor) {
		this.selectedTabTextColor = textColor;
		updateTabStyles();
	}
	
	public void setSelectedTextColorResource(int resId) {
		this.selectedTabTextColor = getResources().getColor(resId);
		updateTabStyles();
	}

	public int getSelectedTextColor() {
		return selectedTabTextColor;
	}

	public void setTypeface(Typeface typeface, int style) {
		this.tabTypeface = typeface;
		this.tabTypefaceStyle = style;
		updateTabStyles();
	}

	public void setTabBackground(int resId) {
		this.tabBackgroundResId = resId;
		updateTabStyles();
	}

	public int getTabBackground() {
		return tabBackgroundResId;
	}

	public void setTabPaddingLeftRight(int paddingPx) {
		this.tabPadding = paddingPx;
		updateTabStyles();
	}

	public int getTabPaddingLeftRight() {
		return tabPadding;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		currentPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = currentPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

}
