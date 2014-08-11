package com.hua.wiget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckedTextView;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.hua.activity.R;
import com.hua.util.DensityUtil;
import com.hua.util.LogUtils2;

/**
 * 顶部indicator
 * 
 * @author dewyze
 * 
 */
@SuppressLint("NewApi")
public class TopIndicator2 extends LinearLayout {

	private static final String TAG = "TopIndicator";
	private int[] mDrawableIds = new int[] {R.drawable.bg_home,
			R.drawable.bg_category, R.drawable.bg_collect,
			R.drawable.bg_setting };
	
	private List<CheckedTextView> mCheckedTextViewList = new ArrayList<CheckedTextView>();
	private List<View> mViewList = new ArrayList<View>();
	/**
	 * 标题
	 */
	private CharSequence[] mtitles = new CharSequence[]{"精选", "发现", "榜单",
			"团购"};
	
	//
	private int mScreenWidth;
	private int mUnderLineWidth;//滑动线的宽
	private View mUnderLine;
	// 底部线条移动初始位置
	private int mUnderLineFromX = 0;
	
	/**
	 * 一个回调接口 
	 */
	private OnClickTopIndicatorListener onClickTopIndicatorListener;	
	
	

	public TopIndicator2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TopIndicator2(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TopIndicator2(Context context) {
		super(context);
		init(context);
	}
	
	private void init(Context context) {
		
		LogUtils2.d("********************************************");
		// TODO Auto-generated method stub
		setOrientation(LinearLayout.VERTICAL);
		this.setBackgroundColor(Color.rgb(250, 250, 250));
		/**
		 * 设置移动条
		 */
		mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
		mUnderLineWidth = mScreenWidth / mtitles.length;
		mUnderLine = new View(context);
//		mUnderLine.setBackgroundColor(Color.rgb(247, 88, 123));
		mUnderLine.setBackgroundColor(context.getResources().getColor(R.color.homefragment_top_title_color));
		LinearLayout.LayoutParams underlineParams = 
				new LayoutParams(mUnderLineWidth, DensityUtil.dip2px(context, 4));
//		mUnderLine.setLayoutParams(underlineParams);
		
		
		
		//顶部的layout
		LinearLayout topLayout = new LinearLayout(context);
		
		LinearLayout.LayoutParams topParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		//
		topLayout.setOrientation(LinearLayout.HORIZONTAL);
		///
		LayoutInflater inflater = LayoutInflater.from(context);
		///
		LinearLayout.LayoutParams topsubParams = 
				new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		///
		topsubParams.weight=1.0f;
		topsubParams.gravity =Gravity.CENTER;
		///
		for(int i=0;i<mtitles.length;i++){
			
			final int index = i;
			
			final View view = inflater.inflate(R.layout.top_indicator_item, null);
			LogUtils2.d("这里**********");
			///
			final CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.item_name);
			///
			checkedTextView.setCompoundDrawablesWithIntrinsicBounds(context
					.getResources().getDrawable(mDrawableIds[i]), null, null,
					null);
			checkedTextView.setCompoundDrawablePadding(DensityUtil.dip2px(context, 10));
			checkedTextView.setText(mtitles[i]);
			///
			LogUtils2.d("这里可以不********");
			topLayout.addView(view,topsubParams);
			LogUtils2.d("这里可以不+++++++++++++");
			checkedTextView.setTag(index);
			
			LogUtils2.d("++++++++++++++++++");
			
			mCheckedTextViewList.add(checkedTextView);//
			mViewList.add(view);//添加每一个标题的view
			/**
			 * 设置监听
			 */
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onClickTopIndicatorListener.onClickIndicatorSelected(index);
				}
			});
			
			
			// 初始化 底部菜单选中状态,默认第一个选中
			if(index == 0){
				
				checkedTextView.setChecked(true);
				checkedTextView.setTextColor(context.getResources().getColor(R.color.homefragment_top_title_color));
				
			}else {
				checkedTextView.setChecked(false);
//				checkedTextView.setTextColor(Color.rgb(19, 12, 14));
				checkedTextView.setTextColor(context.getResources().getColor(R.color.homefragment_top_title_select_cancel));
			}
		}//over for
		LogUtils2.d("55555555");
		this.addView(topLayout,topParams);
		this.addView(mUnderLine,underlineParams);
		
	}
	

	/**
	 * 设置底部导航中图片显示状态和字体颜色
	 */
	public void setTabsDisplay(Context context, int index) {
		int size = mCheckedTextViewList.size();
		for (int i = 0; i < size; i++) {
			CheckedTextView checkedTextView = mCheckedTextViewList.get(i);
			if ((Integer) (checkedTextView.getTag()) == index) {
				LogUtils2.i(TAG, mtitles[index] + " is selected...");
				checkedTextView.setChecked(true);
				checkedTextView.setTextColor(context.getResources().getColor(R.color.homefragment_top_title_color));
			} else {
				checkedTextView.setChecked(false);
				checkedTextView.setTextColor(context.getResources().getColor(R.color.homefragment_top_title_select_cancel));
			}
		}
		// 下划线动画
		LogUtils2.d("开始下线动画");
		doUnderLineAnimation(index);
	}
	

	private void doUnderLineAnimation(int index) {
		// TODO Auto-generated method stub
		LogUtils2.d("=================index="+index);
		TranslateAnimation animation = new TranslateAnimation
				(mUnderLineFromX, index * mUnderLineWidth, 0, 0);
		
		LogUtils2.d("mUnderLineFromX=="+mUnderLineFromX+"    ----"+ mUnderLineWidth*index);
		
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setFillAfter(true);
		animation.setDuration(150);
//		mUnderLine.setAnimation(animation);
		mUnderLine.startAnimation(animation);
		mUnderLineFromX = index * mUnderLineWidth;
		LogUtils2.d("77777777777777");
	}

	
	/**
	 *回调借口，作用是当点击投票标题时 做出相应的改变
	 */
	public interface OnClickTopIndicatorListener{
		
		void onClickIndicatorSelected(int index);
		
	}
	
	public OnClickTopIndicatorListener getOnClickTopIndicatorListener() {
		return onClickTopIndicatorListener;
	}

	public void setOnClickTopIndicatorListener(
			OnClickTopIndicatorListener onClickTopIndicatorListener) {
		this.onClickTopIndicatorListener = onClickTopIndicatorListener;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
}
