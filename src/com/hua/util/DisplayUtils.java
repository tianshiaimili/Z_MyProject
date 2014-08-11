/*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

package com.hua.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.widget.TextView;

public class DisplayUtils
{
    public static interface OnBarHeightKnown
    {

        public abstract void onBarHeightKnown(int i, int j);
    }


    private DisplayUtils()
    {
    }

    public static final DisplayMetrics getDisplayMetrics(Context context)
    {
        if(metrics == null)
            metrics = context.getResources().getDisplayMetrics();
        return metrics;
    }

    public static final int getScreenWidth(Context context)
    {
        return getDisplayMetrics(context).widthPixels;
    }

    public static final int getScreenHeight(Context context)
    {
        return getDisplayMetrics(context).heightPixels;
    }

    public static final int getDensityDpi(Context context)
    {
        return getDisplayMetrics(context).densityDpi;
    }

    public static final float getDensity(Context context)
    {
        return getDisplayMetrics(context).density;
    }

    public static final int dip2px(Context ctx, float dipValue)
    {
        float density = getDensity(ctx);
        return (int)(dipValue * density + 0.5F);
    }

    public static final int px2dip(Context ctx, float pxValue)
    {
        float density = getDensity(ctx);
        return (int)(pxValue / density + 0.5F);
    }

    public static int getStatusBarHeight(Context context)
    {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0)
            result = context.getResources().getDimensionPixelSize(resourceId);
        return result;
    }

    public static int[] getBarHeight(Activity activity)
    {
        Rect appArea = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(appArea);
        int statusBarHeight = appArea.top;
        Log.v(TAG, (new StringBuilder("Application area(not including status bar) top is ")).append(statusBarHeight).toString());
        int viewTop = window.findViewById(16908290).getTop();
        Log.v(TAG, (new StringBuilder("View area(not including status bar and title bar) top is ")).append(viewTop).toString());
        int titleBarHeight;
        if(viewTop == 0)
            titleBarHeight = 0;
        else
            titleBarHeight = Math.abs(viewTop - appArea.top);
        Log.d(TAG, (new StringBuilder("Status bar height is ")).append(statusBarHeight).append(", Title bar height is ").append(titleBarHeight).toString());
        return (new int[] {
            statusBarHeight, titleBarHeight
        });
    }

    public static void getBarHeight(final Activity activity, final OnBarHeightKnown callback)
    {
        TextView view = new TextView(activity.getApplicationContext());
        view.post(new Runnable() {

            public void run()
            {
                int barsHeight[] = DisplayUtils.getBarHeight(activity);
                if(callback != null)
                    callback.onBarHeightKnown(barsHeight[0], barsHeight[1]);
            }

            private final Activity val$activity=null;;
            private final OnBarHeightKnown val$callback=null;

            
            {
//                activity = val$activity;
//                callback = val$callback;
//                super();
            }
        }
);
    }

    public static float getRawSize(Context context, int unit, float size)
    {
        Resources r;
        if(context == null)
            r = Resources.getSystem();
        else
            r = context.getResources();
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

    public static double getDiagonalPixels(Context context)
    {
        double widthPow = Math.pow(getScreenWidth(context), 2D);
        double heightPow = Math.pow(getScreenHeight(context), 2D);
        return Math.sqrt(widthPow + heightPow);
    }

    public static double getRealDPI(Context context, float screenSize)
    {
        return getDiagonalPixels(context) / (double)screenSize;
    }

    public static double getApproximateScreenSize(Context context)
    {
        return getDiagonalPixels(context) / (double)(160F * getDensity(context));
    }

    public static int getTextWidth(TextView tv)
    {
        Paint paint = tv.getPaint();
        return Math.round(paint.measureText(tv.getText().toString()));
    }

    public static int getTextHeight(TextView tv)
    {
        Paint paint = tv.getPaint();
        android.graphics.Paint.FontMetrics fm = paint.getFontMetrics();
        float textHeight = fm.descent - fm.ascent;
        Log.v(TAG, (new StringBuilder("TextView size is ")).append(tv.getTextSize()).append(" sp, textHeight ").append(textHeight).toString());
        return (int)(textHeight + 0.5F);
    }

    public static float getAttrDimensionValue(Context context, int attrId)
    {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attrId, value, true);
        return (float)TypedValue.complexToDimensionPixelSize(value.data, getDisplayMetrics(context));
    }

    public static int getHorizontalMargin(View view)
    {
        android.view.ViewGroup.MarginLayoutParams vlp = (android.view.ViewGroup.MarginLayoutParams)view.getLayoutParams();
        return vlp.leftMargin + vlp.rightMargin;
    }

    public static int getVerticalMargin(View view)
    {
        android.view.ViewGroup.MarginLayoutParams vlp = (android.view.ViewGroup.MarginLayoutParams)view.getLayoutParams();
        return vlp.topMargin + vlp.bottomMargin;
    }

    public static int getLeftMargin(View view)
    {
        android.view.ViewGroup.MarginLayoutParams vlp = (android.view.ViewGroup.MarginLayoutParams)view.getLayoutParams();
        return vlp.leftMargin;
    }

    public static int getTopMargin(View view)
    {
        android.view.ViewGroup.MarginLayoutParams vlp = (android.view.ViewGroup.MarginLayoutParams)view.getLayoutParams();
        return vlp.topMargin;
    }

    public static int getRightMargin(View view)
    {
        android.view.ViewGroup.MarginLayoutParams vlp = (android.view.ViewGroup.MarginLayoutParams)view.getLayoutParams();
        return vlp.rightMargin;
    }

    public static int getBottomMargin(View view)
    {
        android.view.ViewGroup.MarginLayoutParams vlp = (android.view.ViewGroup.MarginLayoutParams)view.getLayoutParams();
        return vlp.bottomMargin;
    }

    public static int getTextViewLineCount(TextView textView, int textViewWidth)
    {
        float textSize = textView.getTextSize();
        Typeface textTypeface = textView.getTypeface();
        TextPaint paint = new TextPaint(129);
        paint.setTextSize(textSize);
        paint.setTypeface(textTypeface);
        int lineCount = 0;
        int index = 0;
        for(int length = textView.getText().length(); index < length - 1;)
        {
            index += paint.breakText(textView.getText(), index, length, true, textViewWidth, null);
            lineCount++;
        }

        return lineCount;
    }

    public static int getTextViewHeight(TextView textView, int textViewWidth)
    {
        int lineHeight = getTextHeight(textView);
        if(textView.getText() == null || textView.getText().equals(""))
            return lineHeight;
        else
            return lineHeight * getTextViewLineCount(textView, textViewWidth);
    }

    public static String getSpaceText(TextView textView, int spaceDP)
    {
        String text = textView.getText().toString();
        textView.setText(" ");
        int spaceWidth = getTextWidth(textView);
        int spaceCount = dip2px(textView.getContext(), spaceDP) / spaceWidth;
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < spaceCount; i++)
            sb.append(" ");

        textView.setText(text);
        return sb.toString();
    }

    public static void setTextViewEllipsizeEnd(TextView textView, int maxLine, boolean useTextViewAPI)
    {
        if(useTextViewAPI)
        {
            textView.setMaxLines(maxLine);
            textView.setEllipsize(android.text.TextUtils.TruncateAt.END);
            textView.invalidate();
        } else
        {
            Log.v(TAG, (new StringBuilder("setTextViewEllipsizeEnd textView.getLineCount() ")).append(textView.getLineCount()).toString());
            if(textView.getLineCount() <= maxLine)
                return;
            CharSequence text = textView.getText();
            int lineEndIndex = textView.getLayout().getLineEnd(maxLine - 1);
            int skipCharSize = 3;
            String endSymbol = "...";
            textView.setText(endSymbol);
            int endSymbolWidth = getTextWidth(textView);
            CharSequence lastThree = text.subSequence(lineEndIndex - 3, lineEndIndex);
            String lastChar = lastThree.subSequence(2, 3).toString();
            textView.setText(lastChar);
            int lastCharWidth = getTextWidth(textView);
            if(lastCharWidth < endSymbolWidth)
            {
                String lastSecondChar = text.subSequence(1, 2).toString();
                textView.setText(lastSecondChar);
                int lastSecondCharWidth = getTextWidth(textView);
                if(lastSecondCharWidth < endSymbolWidth)
                    skipCharSize = 3;
                else
                    skipCharSize = 2;
            } else
            {
                skipCharSize = 1;
            }
            Log.v(TAG, (new StringBuilder("setTextViewEllipsizeEnd lastThree ")).append(lastThree).append(", skipCharSize ").append(skipCharSize).toString());
            String ellipsizeEndText = (new StringBuilder()).append(text.subSequence(0, lineEndIndex - skipCharSize)).append(endSymbol).toString();
            textView.setText(ellipsizeEndText);
            textView.invalidate();
        }
    }

    protected static final String TAG = "com/pccw/gzmobile/utils/DisplayUtils.getSimpleName()";
    private static DisplayMetrics metrics;

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 41 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/