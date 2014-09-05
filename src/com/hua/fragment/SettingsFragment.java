package com.hua.fragment;

import android.R.integer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.util.LogUtils2;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class SettingsFragment extends Fragment implements OnClickListener{
	
	  private final int DURATION = 350;
	    private Button mMenuButton;
	    private Button mItemButton1;
	    private Button mItemButton2;
	    private Button mItemButton3;
	    private Button mItemButton4;
	    private Button mItemButton5;
	    private boolean mIsMenuOpen = false;//判断是否一打开
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	// TODO Auto-generated method stub
	    	super.onCreate(savedInstanceState);
//	    	initView(getActivity());
	    	
	    }
	
	private void initButton(View view) {
			// TODO Auto-generated method stub
		mMenuButton = (Button) view.findViewById(R.id.menu);
        mMenuButton.setOnClickListener(this);

        mItemButton1 = (Button) view.findViewById(R.id.item1);
        mItemButton1.setOnClickListener(this);

        mItemButton2 = (Button) view.findViewById(R.id.item2);
        mItemButton2.setOnClickListener(this);

        mItemButton3 = (Button) view.findViewById(R.id.item3);
        mItemButton3.setOnClickListener(this);

        mItemButton4 = (Button) view.findViewById(R.id.item4);
        mItemButton4.setOnClickListener(this);

        mItemButton5 = (Button) view.findViewById(R.id.item5);
        mItemButton5.setOnClickListener(this);
        
		}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_setting, container,
				false);
		
		initButton(view);
		
		return view;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		doAnimationCloseBySubButton();
	}
	
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		 int buttonID = view.getId();
		 
		 switch (buttonID) {
		case R.id.menu:
//			Toast.makeText(getActivity(), "lalalla", 300).show();
			if (!mIsMenuOpen) {
                mIsMenuOpen = true;
                doAnimateOpen(mItemButton1, 0, 5, 300);
                doAnimateOpen(mItemButton2, 1, 5, 300);
                doAnimateOpen(mItemButton3, 2, 5, 300);
                doAnimateOpen(mItemButton4, 3, 5, 300);
                doAnimateOpen(mItemButton5, 4, 5, 300);
            } else {
                doAnimationCloseBySubButton();
            }
			
			break;

		case R.id.item1:
			Toast.makeText(getActivity(), "lalalla", 300).show();
			doAnimationCloseBySubButton();
			
			break;
			
		case R.id.item2:
			doAnimationCloseBySubButton();
			break;
			
		case R.id.item3:
			doAnimationCloseBySubButton();
			break;
			
		case R.id.item4:
			doAnimationCloseBySubButton();
			break;
			
		case R.id.item5:
			doAnimationCloseBySubButton();
			
			
			
			break;
			
		default:
			break;
		}
	}

	private void doAnimationCloseBySubButton() {
		if(mIsMenuOpen){
			mIsMenuOpen = false;
			doAnimateClose(mItemButton1, 0, 5, 300);
			doAnimateClose(mItemButton2, 1, 5, 300);
			doAnimateClose(mItemButton3, 2, 5, 300);
			doAnimateClose(mItemButton4, 3, 5, 300);
			doAnimateClose(mItemButton5, 4, 5, 300);
		}
	}
	
	 /**
     * 打开菜单的动画
     * @param view 执行动画的view
     * @param index view在动画序列中的顺序
     * @param total 动画序列的个数
     * @param radius 动画半径
     */
    private void doAnimateOpen(View view, int index, int total, int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.PI * index / ((total - 1) * 2);
        int translationX = -(int) (radius * Math.cos(degree));
        int translationY = -(int) (radius * Math.sin(degree));
        LogUtils2.d("SettingFragmnet", String.format("degree=%f, translationX=%d, translationY=%d",
                degree, translationX, translationY));
        AnimatorSet set = new AnimatorSet();
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0, translationX),
                ObjectAnimator.ofFloat(view, "translationY", 0, translationY),
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1));
        //动画周期为500ms
        try {
			set.setInterpolator(OvershootInterpolator.class.newInstance());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        set.setDuration(DURATION).start();
    }
	
    
    /**
     * 关闭菜单的动画
     * @param view 执行动画的view
     * @param index view在动画序列中的顺序
     * @param total 动画序列的个数
     * @param radius 动画半径
     */
    private void doAnimateClose(final View view, int index, int total,
            int radius) {
        if (view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        double degree = Math.PI * index / ((total - 1) * 2);
        int translationX = -(int) (radius * Math.cos(degree));
        int translationY = -(int) (radius * Math.sin(degree));
        LogUtils2.d("", String.format("degree=%f, translationX=%d, translationY=%d",
                degree, translationX, translationY));
        
        ViewPropertyAnimator viewPropertyAnimator = ViewPropertyAnimator.animate(view);
        viewPropertyAnimator.alpha(0f).translationX(0).translationY(0).scaleX(0).scaleY(0);
        viewPropertyAnimator.setListener(new AnimatorListener(){

			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				   view.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}

		
        	
        });
        /**
         *  AccelerateDecelerateInterpolator 在动画开始与结束的地方速率改变比较慢，在中间的时候加速

     AccelerateInterpolator  在动画开始的地方速率改变比较慢，然后开始加速

   AnticipateInterpolator 开始的时候向后然后向前甩

   AnticipateOvershootInterpolator 开始的时候向后然后向前甩一定值后返回最后的值

   BounceInterpolator   动画结束的时候弹起

   CycleInterpolator 动画循环播放特定的次数，速率改变沿着正弦曲线

   DecelerateInterpolator 在动画开始的地方快然后慢

     LinearInterpolator   以常量速率改变

     OvershootInterpolator    向前甩一定值后再回到原来位置
         */
        try {
			viewPropertyAnimator.setInterpolator(AnticipateInterpolator.class.newInstance());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        viewPropertyAnimator.setDuration(DURATION).start();
        
//        
//        AnimatorSet set = new AnimatorSet();
//      //包含平移、缩放和透明度动画
//        set.playTogether(
//                ObjectAnimator.ofFloat(view, "translationX", translationX, 0),
//                ObjectAnimator.ofFloat(view, "translationY", translationY, 0),
//                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f),
//                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f),
//                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f));
//        //为动画加上事件监听，当动画结束的时候，我们把当前view隐藏
//        set.addListener(new AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                view.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//            }
//        });
//
//        set.setDuration(DURATION).start();
    }
    
}
