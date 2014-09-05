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
	    private boolean mIsMenuOpen = false;//�ж��Ƿ�һ��
	    
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
     * �򿪲˵��Ķ���
     * @param view ִ�ж�����view
     * @param index view�ڶ��������е�˳��
     * @param total �������еĸ���
     * @param radius �����뾶
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
        //����ƽ�ơ����ź�͸���ȶ���
        set.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0, translationX),
                ObjectAnimator.ofFloat(view, "translationY", 0, translationY),
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1));
        //��������Ϊ500ms
        try {
			set.setInterpolator(OvershootInterpolator.class.newInstance());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        set.setDuration(DURATION).start();
    }
	
    
    /**
     * �رղ˵��Ķ���
     * @param view ִ�ж�����view
     * @param index view�ڶ��������е�˳��
     * @param total �������еĸ���
     * @param radius �����뾶
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
         *  AccelerateDecelerateInterpolator �ڶ�����ʼ������ĵط����ʸı�Ƚ��������м��ʱ�����

     AccelerateInterpolator  �ڶ�����ʼ�ĵط����ʸı�Ƚ�����Ȼ��ʼ����

   AnticipateInterpolator ��ʼ��ʱ�����Ȼ����ǰ˦

   AnticipateOvershootInterpolator ��ʼ��ʱ�����Ȼ����ǰ˦һ��ֵ�󷵻�����ֵ

   BounceInterpolator   ����������ʱ����

   CycleInterpolator ����ѭ�������ض��Ĵ��������ʸı�������������

   DecelerateInterpolator �ڶ�����ʼ�ĵط���Ȼ����

     LinearInterpolator   �Գ������ʸı�

     OvershootInterpolator    ��ǰ˦һ��ֵ���ٻص�ԭ��λ��
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
//      //����ƽ�ơ����ź�͸���ȶ���
//        set.playTogether(
//                ObjectAnimator.ofFloat(view, "translationX", translationX, 0),
//                ObjectAnimator.ofFloat(view, "translationY", translationY, 0),
//                ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f),
//                ObjectAnimator.ofFloat(view, "scaleY", 1f, 0f),
//                ObjectAnimator.ofFloat(view, "alpha", 1f, 0f));
//        //Ϊ���������¼�������������������ʱ�����ǰѵ�ǰview����
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
