package com.hua.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.ScrollView;

import com.hua.util.LogUtils2;

public class MyGridView extends GridView implements OnScrollListener{


	private boolean isfocuse = true;
	
	 Handler handler;

	    /**
	     */
	    public ScrollView parentScrollView;
	    private int lastScrollDelta = 0;
	    int mTop = 10;
	    /**
	     * ��ʾ��һ�������ĵ� (�յ����ȥʱ�������)������˵��ָ�뿪ʱ�������
	     */
	    int currentY;
	    
	    /**
	     * �Ѿ������˵ľ���
	     */
	    int havaScrollY;
	
	public MyGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		 handler = new Handler();
	}


	   @Override
	    public boolean onInterceptTouchEvent(MotionEvent ev) {
	    	// TODO Auto-generated method stub
	    	
	    	int code = ev.getAction();
	    	switch (code) {
			case MotionEvent.ACTION_DOWN:
				getParent().requestDisallowInterceptTouchEvent(true);
//				isfocuse = true;
				/**
				 * ev.getY() �������Ļ���Ͻǣ�0��0���������
				 */
				currentY = (int) ev.getY();
				LogUtils2.i("onInterceptTouchEvent---currentY=="+currentY);
				break;
				
			case MotionEvent.ACTION_MOVE:
				
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				getParent().requestDisallowInterceptTouchEvent(false);
				break;
			default:
				break;
			}
	    	return super.onInterceptTouchEvent(ev);
	    }
	   
	   /**
	    * 
	    */
//	   @Override
//	    public boolean onTouchEvent(MotionEvent ev) {
//	    	// TODO Auto-generated method stub
//	    	int code = ev.getAction();
//	    	View itemView = getChildAt(0);
//			 int itemHeight = itemView.getMeasuredHeight();
//			 int currentGridViewHeight = getMeasuredHeight();
//			 int itemCount = getCount();
//			 int totalRowCount = ((itemCount%2) == 0 ? (itemCount/2) : ((itemCount /2)+1));
//			 int verticalSpacing = getVerticalSpacing();
//			 int totalVSpacing = (totalRowCount -1 ) * verticalSpacing;
//			 int paddingTop = getPaddingTop();
//			 int totalHeight = (totalRowCount * itemHeight) + (totalVSpacing) + paddingTop; 
//			 /**
//			  * �ܹ������ľ���
//			  */
//			 int canScrollY = totalHeight - currentGridViewHeight;
//			 
//			 LogUtils2.e("itemView=="+itemView);
//				LogUtils2.e("itemHeight=="+itemHeight);
//				LogUtils2.e("currentGridViewHeight=="+currentGridViewHeight);
//				LogUtils2.e("itemCount=="+itemCount);
//				LogUtils2.e("totalRowCount=="+totalRowCount);
//				LogUtils2.e("verticalSpacing=="+verticalSpacing);
//				LogUtils2.e("totalVSpacing=="+totalVSpacing);
//				LogUtils2.e("totalHeight=="+totalHeight);
//				LogUtils2.e("canScrollY=="+canScrollY);
//				LogUtils2.e("paddingTop=="+paddingTop);
//				
//	    	switch (code) {
//			case MotionEvent.ACTION_DOWN:
//				currentY = (int) ev.getY();
//				LogUtils2.i("onTouchEvent����ACTION_DOWN---currentY=="+currentY);
//				break;
//				
//			case MotionEvent.ACTION_MOVE:
//				LogUtils2.e("************************************");
//				View child = getChildAt(0);
//				
//				/**
//				 * �õ�neibuScrollView�ĵ�һ����View LinerLayout�ĸ߶�
//				 * �������� �����˶��پ��룬Ȼ���ж��Ƿ���ScrollView�������ײ�
//				 */
////				int childHeight = child.getMeasuredHeight();
//				int childHeight = totalHeight;//child.getMeasuredHeight();
//				 /**
//                * getMeasuredHeight() �õ���Ӧ���ǵ�ǰInnerScrollView�ĸ߶� 
//                * ���Ի�ȡ����View LinerLayout�ĸ߶ȼ�ȥ��ǰscrollView�ĸ߶� �ʹ������ײ���
//                */
//				int currentScrollViewHeiht = currentGridViewHeight;//getMeasuredHeight();
//				int currentShowHeight = childHeight - currentScrollViewHeiht;
//				
//				LogUtils2.i("childHeight����="+childHeight +"   currentScrollViewHeiht="+currentScrollViewHeiht);
//				LogUtils2.i("currentShowHeight����="+currentShowHeight);
//				
//				 int y = (int) ev.getY();
//				 int scrollY = itemView.getScrollY();
//				 
//				 LogUtils2.i("y����="+y +"    scrollY=="+scrollY+"   currentY=="+currentY);
//				 
//				 if(currentY > y){
//					 LogUtils2.i("����........");
//					 havaScrollY += (currentY - y );
//					 LogUtils2.i("currentY > y===="+(havaScrollY));
//					 LogUtils2.i("havaScrollY >= currentShowHeight----=="+havaScrollY +" |  "+currentShowHeight);
//					 LogUtils2.d("getTranslationY();==="+getTranslationY());
//					 if(havaScrollY >= currentShowHeight){
//						 
//						 getParent().requestDisallowInterceptTouchEvent(false);
//						 return false;
//						 
//					 }else {
//						
//						 getParent().requestDisallowInterceptTouchEvent(true); 
//						 
//					}
//					 
//				 }else  if(currentY < y){
//					 LogUtils2.i("����*****************");
//					 
//					 havaScrollY += (currentY - y );
//					 LogUtils2.i("havaScrollY >= 0----=="+havaScrollY +"  currentShowHeight=="+currentShowHeight);
//					 LogUtils2.d("getTranslationY();==="+getTranslationY());
//					 if(havaScrollY <=0){
//						 
//						 getParent().requestDisallowInterceptTouchEvent(false);
//						 return false;
//					 }else {
//						
//						 getParent().requestDisallowInterceptTouchEvent(true);
//					}
//					 
//				}
//				 
//				 LogUtils2.i("y����="+y+"   currentY=="+currentY +"    scrollY=="+scrollY);
//				 currentY = y;
//				 
//				break;
//
//			default:
//				break;
//			}
//	    	
//	    	return super.onTouchEvent(ev);
//	    }
	   
	   @Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		   
		   int actionCode = ev.getAction();
		   switch (actionCode) {
		case MotionEvent.ACTION_MOVE:
			LogUtils2.i("XXXXXXXXXXXXXXXXX");
			if(!isfocuse){
				LogUtils2.d("FFFFFFFFPPPPPPPPPPPF");
				getParent().requestDisallowInterceptTouchEvent(false);
				isfocuse = true;
				return false;
			}
			break;

		default:
			break;
		}
		   
		return super.onTouchEvent(ev);
	}
	   
	   /**
	    * 
	    * @return
	    */
	    private int getScrollRange() {
	        int scrollRange = 0;
	        if (getChildCount() > 0) {
	        	
	            View child = getChildAt(0);
	            LogUtils2.d("child.getHeight() - (getHeight())==="+child.getHeight() +" __"+ (getHeight()));
	            scrollRange = Math.max(0, child.getHeight() - (getHeight()));
	            LogUtils2.i("scrollRange==="+scrollRange +"    child.getHeight()=="+child.getHeight());
	        }
	        return scrollRange;
	    }


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		LogUtils2.e("7777777777777777777777777777");
		 switch (scrollState) {  
		    // ��������ʱ  
		 
		    case OnScrollListener.SCROLL_STATE_IDLE:  
		    // �жϹ������ײ�  
		    if (this.getLastVisiblePosition() == (this.getCount() - 1)) {  
		    	
		    	getParent().requestDisallowInterceptTouchEvent(true);
		    	
		                 }else {
		                	 getParent().requestDisallowInterceptTouchEvent(false);
						}  
		    // �жϹ���������  
		  
		    if(this.getFirstVisiblePosition() == 0){  
		    	getParent().requestDisallowInterceptTouchEvent(true);
		    } else {
				
		    	 getParent().requestDisallowInterceptTouchEvent(false);
			} 
		  
		     break;  
		        }  
	}


	/**
	 *  ΨһҪ���ľ����� OnScroll��firstVisibleItem + visibleItemCount == totalItemCount
 	firstVisibleItem :��ʾ����Ļ�е�һ����ʾ��������adapter�е�λ��,visibleItemCount 
 	������Ļ�����һ��������adapter�е�����,totalItemCount����adapter�е�������!
	 */
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		  if(firstVisibleItem==0){  
			  //����
			  getParent().requestDisallowInterceptTouchEvent(false);
          }       
          if(visibleItemCount+firstVisibleItem==totalItemCount){  
            //�ײ�
        	  getParent().requestDisallowInterceptTouchEvent(false);
          }
		
	}
	
	public void setfouce(){
		LogUtils2.i("��������������");
//		getParent().requestDisallowInterceptTouchEvent(false);
		isfocuse = false;
	}
	

}
