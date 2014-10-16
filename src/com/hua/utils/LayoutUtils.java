/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.View.MeasureSpec;
import android.widget.*;

import java.io.PrintStream;

public class LayoutUtils {
	private static final String TAG = "com/pccw/gzmobile/app/LayoutUtils.getSimpleName()";

	public static interface OnViewGlobalLayoutListener {

		public abstract void onViewGlobalLayout(View view);
	}

	private LayoutUtils() {
	}

	public static void getViewLayoutInfoBeforeOnResume(final View view,
			final OnViewGlobalLayoutListener listener) {
		if (view.getViewTreeObserver().isAlive())
			view.getViewTreeObserver().addOnGlobalLayoutListener(
					new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

						public void onGlobalLayout() {
							if (android.os.Build.VERSION.SDK_INT < 16)
								view.getViewTreeObserver()
										.removeGlobalOnLayoutListener(this);
							else
								view.getViewTreeObserver()
										.removeOnGlobalLayoutListener(this);
							if (listener != null)
								listener.onViewGlobalLayout(view);
						}

						private final View val$view=null;
						private final OnViewGlobalLayoutListener val$listener=null;

						{
							/*view = view;
							listener = onviewgloballayoutlistener;
							super();*/
						}
					});
		else
			System.err.println("== The ViewTreeObserver is NOT alive. ==");
	}

	public static View getContentView(Activity act) {
		return act.getWindow().getDecorView().findViewById(16908290);
	}

	public static void setLayoutParams(View view, ViewGroup parent, int width,
			int height) {
		if (view == null)
			throw new NullPointerException("View parameter cannot be null");
		if (view.getParent() != null) {
			parent = (ViewGroup) view.getParent();
			Log.d(TAG,
					(new StringBuilder("View is attached to a "))
							.append(parent.getClass().getName())
							.append(" already. Use its own parent LayoutParams.")
							.toString());
		} else {
			if (parent == null) {
				view.setLayoutParams(new android.view.ViewGroup.LayoutParams(
						width, height));
				Log.w(TAG,
						"View is not attached to a parent ViewGroup and Null parent. Try ViewGroup.LayoutParams");
				return;
			}
			Log.d(TAG,
					(new StringBuilder(
							"View is not attached to a parent ViewGroup. Use given parent "))
							.append(parent.getClass().getName()).toString());
		}
		android.view.ViewGroup.LayoutParams target = null;
		if (view.getLayoutParams() != null) {
			view.getLayoutParams().width = width;
			view.getLayoutParams().height = height;
		} else {
			view.setLayoutParams(new android.view.ViewGroup.LayoutParams(width,
					height));
		}
		if (parent instanceof FrameLayout)
			target = new android.widget.FrameLayout.LayoutParams(
					view.getLayoutParams());
		else if (parent instanceof LinearLayout)
			target = new android.widget.LinearLayout.LayoutParams(
					view.getLayoutParams());
		else if (parent instanceof RelativeLayout)
			target = new android.widget.RelativeLayout.LayoutParams(
					view.getLayoutParams());
		else if (parent instanceof AbsListView) {
			target = new android.widget.AbsListView.LayoutParams(
					view.getLayoutParams());
		} else {
			Log.w(TAG,
					(new StringBuilder("Unknown parent "))
							.append(parent.getClass().getName())
							.append(". Try FrameLayout.LayoutParams")
							.toString());
			target = new android.widget.FrameLayout.LayoutParams(width, height);
		}
		view.setLayoutParams(target);
	}

	public static void setViewCenterInParent(View view) {
		android.view.ViewGroup.LayoutParams lp = view.getLayoutParams();
		if (lp instanceof android.widget.FrameLayout.LayoutParams)
			((android.widget.FrameLayout.LayoutParams) lp).gravity = 17;
		else if (lp instanceof android.widget.LinearLayout.LayoutParams)
			((android.widget.LinearLayout.LayoutParams) lp).gravity = 17;
		else if (lp instanceof android.widget.RelativeLayout.LayoutParams)
			((android.widget.RelativeLayout.LayoutParams) lp).addRule(13);
	}

	public static void addListHeaderView(ListView listView, View header) {
		if (listView.getAdapter() != null)
			System.err
					.println("Should addHeaderView before calling setAdapter.");
		listView.addHeaderView(header);
	}

	public static void addListFooterView(ListView listView, View footer) {
		if (listView.getAdapter() != null)
			System.err
					.println("Should addFooterView before calling setAdapter.");
		listView.addFooterView(footer);
	}

	public static void setListViewItemVisibility(boolean visible,
			View convertView, int height) {
		if (convertView instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) convertView;
			int childCount = vg.getChildCount();
			for (int i = 0; i < childCount; i++)
				vg.getChildAt(i).setVisibility(visible ? 0 : 8);

		} else {
			convertView.setVisibility(visible ? 0 : 8);
		}
		convertView.getLayoutParams().height = visible ? height : 0;
	}

	public static void setListViewItemVisibility(boolean visible,
			View convertView, int height, int leftPadding, int topPadding,
			int rightPadding, int bottomPadding) {
		if (visible)
			convertView.setPadding(leftPadding, topPadding, rightPadding,
					bottomPadding);
		else
			convertView.setPadding(leftPadding, -height, rightPadding, 0);
		convertView.getLayoutParams().height = visible ? height : 0;
	}

	/**
	 * 判断是否平板
	 */
	public static boolean isTablet(Context context) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int smallestWidth = (int) ((float) metrics.widthPixels / metrics.density);
		return smallestWidth >= 600;
	}

	public static void setFullScreenOrNot(Activity activity,
			boolean isFullScreen) {
		if (activity == null) {
			Log.e(TAG, "setFullScreenOrNot activity can NOT be null.");
			return;
		}
		android.view.WindowManager.LayoutParams attrs = activity.getWindow()
				.getAttributes();
		if (isFullScreen)
			attrs.flags |= 1024;
		else
			attrs.flags &= -1025;
		activity.getWindow().setAttributes(attrs);
	}

	public static void setActivityNoTitleBar(Activity activity) {
		activity.requestWindowFeature(1);
	}

	public static void setActivityNoTitleNoStatusBar(Activity activity) {
		activity.requestWindowFeature(1);
		activity.getWindow().setFlags(1024, 1024);
	}

	
	/**
	 * MeasureSpec封装从parent传递给child的layout要求。每个MeasureSpec表示对width/height的要求。
	 * MeasureSpec由size和mode组成。可用的mode有3种：
	1. UNSPECIFIED表示parent没有强加给child任何constraint。
	2. EXACTLY表示parent已经确定child的精确size。
	3. AT_MOST表示child可以设定为specified size之内的任何值。
	 * @param child
	 */
	public  void measureView(View child) {
		
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		
		/**
		 * 这里应该是测量获取子View的大小把，然后在父view中给出合适的大小显示
		 */
		LogUtils2.d("p.width=="+p.width+"   p.height=="+p.height);
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		/**
		 * 
		 */
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		
		//
		LogUtils2.i("childWidthSpec=="+childWidthSpec+"   childHeightSpec="+childHeightSpec);
		child.measure(childWidthSpec, childHeightSpec);
	}
	
	
}

