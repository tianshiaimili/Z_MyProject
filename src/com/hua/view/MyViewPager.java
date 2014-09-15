package com.hua.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.hua.activity.R;

public class MyViewPager extends ViewPager{

	private ViewPager pager;
	
	
	private int [] imageViews = {R.drawable.xianjian1,R.drawable.xianjian2,
			R.drawable.xianjian1,R.drawable.xianjian2};
	
	public MyViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
//		intitView(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
//		intitView(context);
		
	}

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
//		intitView(context);
	}

    //拦截 TouchEvent  
//    @Override  
//    public boolean onInterceptTouchEvent(MotionEvent arg0) {  
//        // TODO Auto-generated method stub  
//        return super.onInterceptTouchEvent(arg0);  
//    }  
      
    //处理 TouchEvent   
//    @Override  
//    public boolean onTouchEvent(MotionEvent arg0) {  
//        // TODO Auto-generated method stub  
//        return super.onTouchEvent(arg0);  
//    }  
      
  
    //因为这个执行的顺序是  父布局先得到 action_down的事件  
      
    /** 
     * onInterceptTouchEvent(MotionEvent ev)方法，这个方法只有ViewGroup类有 
     * 如LinearLayout，RelativeLayout等    可以包含子View的容器的 
     * 
     * 用来分发 TouchEvent 
     * 此方法    返回true    就交给本 View的 onTouchEvent处理 
     * 此方法    返回false   就交给本View的 onInterceptTouchEvent 处理 
     */  
//    @Override  
//    public boolean dispatchTouchEvent(MotionEvent ev) {  
//          
//        //让父类不拦截触摸事件就可以了。  
//        this.getParent().requestDisallowInterceptTouchEvent(false);   
//        return super.dispatchTouchEvent(ev);  
//     
//    } 
	

//	/**
//	 * 初始化
//	 */
//	private void intitView(Context context) {
//		// TODO Auto-generated method stub
//		
//		setOrientation(LinearLayout.VERTICAL);
//	
//		///
//		LinearLayout.LayoutParams params = 
//				new LinearLayout.LayoutParams(
//						LinearLayout.LayoutParams.MATCH_PARENT, 
//						LinearLayout.LayoutParams.WRAP_CONTENT);
//		setLayoutParams(params);
//		setBackgroundColor(Color.parseColor("#CCCCCC"));
//		//
//		pager = new ViewPager(context);
//		pager.setLayoutParams(params);
//		pager.setAdapter(new MyViewPagerAdater());
//		
//		
//	}
//	
//	class MyViewPagerAdater extends PagerAdapter{
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return 4;
//		}
//
//		@Override
//		public boolean isViewFromObject(View view, Object obj) {
//			// TODO Auto-generated method stub
//			return view == (View)obj;
//		}
//
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {
//			// TODO Auto-generated method stub
//			
//			ImageView imageView = new ImageView(getContext());
//			imageView.setImageResource(imageViews[position]);
//			container.addView(imageView);
//			return imageView;
////			return super.instantiateItem(container, position);
//		}
//		
//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			// TODO Auto-generated method stub
////			super.destroyItem(container, position, object);
//			container.removeView((View)object);
//			
//		}
//		
//		
//	}
	
	
}
