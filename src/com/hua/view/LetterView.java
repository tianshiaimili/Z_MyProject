package com.hua.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
/**
 * �����ұߵ���ĸview
 * @author Hua
 *
 */
public class LetterView extends View{
	/**������view��ʱ��ı�����ɫ**/
	public static final String COLOR_BG = "#9999CC66";
	/**û����view��ʱ��ı�����ɫ**/
	public static final int COLOR_NO_BG = 0x00000000;
	/**ѡ�е���ĸ��������ɫ**/
	public static final int COLOR_TEXT_SELECTED = 0xff386AB7;
	/**ûѡ�е���ĸ��������ɫ**/
	public static final int COLOR_TEXT_NORMAL = 0xff000000;
	/**��ĸ�������С**/
	public static final int SIZE_TEXT = 22;
	/**��ĸ��**/
	private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private Paint paint;
	/**��View�Ŀ�**/
	private int width;
	/**��View�ĸ�**/
	private int height;
	/**������ĸ�ĸ�**/
	private int singleHight;
	public LetterView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		paint = new Paint();
		paint.setAntiAlias(true); //�����
		paint.setTextSize(SIZE_TEXT); //���������С
		paint.setFakeBoldText(true); //��������Ϊ����
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(width == 0 || height == 0){
			width = getWidth();
			height = getHeight();
			singleHight = height/letters.length();
		}
		
		for (int i = 0; i < letters.length(); i++) {
			if(currentSelectedIndex == i){
				paint.setColor(COLOR_TEXT_SELECTED);
			}else{
				paint.setColor(COLOR_TEXT_NORMAL);
			}
			//paint.measureText(str)  ����str�Ŀ�
			float xPos = (width-paint.measureText(letters.charAt(i)+""))/2;
			float yPos = singleHight*i+singleHight;
			canvas.drawText(letters.charAt(i)+"", xPos, yPos, paint);
		}
	}
	private int currentSelectedIndex = 0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		currentSelectedIndex = (int) (event.getY()/singleHight);
		if(currentSelectedIndex < 0 ){
			currentSelectedIndex = 0;
		}
		if(currentSelectedIndex > letters.length() - 1){
			currentSelectedIndex = letters.length() - 1;
		}
		if(letterChangeListener != null){
			//�ص�
			letterChangeListener.onLetterChange(currentSelectedIndex);
		}
		invalidate();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setBackgroundColor(Color.parseColor(COLOR_BG));
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			setBackgroundColor(COLOR_NO_BG);
			break;
		}
		return true;
	}
	
	
	
	private OnLetterChangeListener letterChangeListener;
	public void setOnLetterChangeListener(OnLetterChangeListener letterChangeListener){
		this.letterChangeListener = letterChangeListener;
	}
	public interface OnLetterChangeListener{
		void onLetterChange(int selectedIndex);
	}
	public void setSelectedIndex(int selectedIndex) {
		currentSelectedIndex = selectedIndex;
		invalidate();
	}
	
}
