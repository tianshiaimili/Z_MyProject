package com.hua.wiget;

import com.hua.util.LogUtils2;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

public class CoverFlow extends Gallery {

	private Camera mCamera = new Camera();

	// private int mMaxRotationAngle = 30;
	//
	// private int mMaxZoom = -180;

	// private int mMaxRotationAngle = 30;
	//
	// private int mMaxZoom = -100;

	private int mMaxRotationAngle = 60;

	private int mMaxZoom = -150;

	private int mCoveflowCenter;

	private boolean mAlphaMode = true;

	private boolean mCircleMode = false;

	public CoverFlow(Context context) {

		super(context);

		this.setStaticTransformationsEnabled(true);

	}

	public CoverFlow(Context context, AttributeSet attrs) {

		super(context, attrs);

		this.setStaticTransformationsEnabled(true);

	}

	public CoverFlow(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

		this.setStaticTransformationsEnabled(true);
		this.setChildrenDrawingOrderEnabled(true);

	}

	public int getMaxRotationAngle() {

		return mMaxRotationAngle;

	}

	public void setMaxRotationAngle(int maxRotationAngle) {

		mMaxRotationAngle = maxRotationAngle;

	}

	public boolean getCircleMode() {

		return mCircleMode;

	}

	public void setCircleMode(boolean isCircle) {

		mCircleMode = isCircle;

	}

	public boolean getAlphaMode() {

		return mAlphaMode;

	}

	public void setAlphaMode(boolean isAlpha) {

		mAlphaMode = isAlpha;

	}

	public int getMaxZoom() {

		return mMaxZoom;

	}

	public void setMaxZoom(int maxZoom) {

		mMaxZoom = maxZoom;

	}

	private int getCenterOfCoverflow() {

		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2

		+ getPaddingLeft();

	}

	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;

	}

	private int getChildLeft(View child) {
		if (child != null) {
			return child.getLeft();
		} else {
			return 0;
		}
	}

	private int getChildRight(View child) {
		if (child != null) {
			return child.getRight();
		} else {
			return 0;
		}

	}

	protected boolean getChildStaticTransformation(View child, Transformation t) {

		LogUtils2.e("getChildStaticTransformation....");
		
		final int childCenter = getCenterOfView(child);

		final int childWidth = child.getWidth();

		int rotationAngle = 0;

		t.clear();

		t.setTransformationType(Transformation.TYPE_MATRIX);

		if (childCenter == mCoveflowCenter) {

			transformImageBitmap((ImageView) child, t, 0);

		} else {

			// int left = getChildLeft(child);
			// int right = getChildRight(child);
			// if (left < mCoveflowCenter && right > mCoveflowCenter) {
			// transformImageBitmap((ImageView) child, t, 0);
			// } else {
			rotationAngle = (int) (((float) (mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);

			if (Math.abs(rotationAngle) > mMaxRotationAngle) {

				rotationAngle = (rotationAngle < 0) ? -mMaxRotationAngle

				: mMaxRotationAngle;

			}

			transformImageBitmap((ImageView) child, t, rotationAngle);
			// }

		}

		return true;

	}

	/**
	 * 
	 * �������ν���ڴ�С�Ĳ���ʱ,��һ�۵��Ѿ������˸ı䡣��� ��ֻ����ӵ���ͼ���,���˽���ɵĹ��� ��ֵ��Ϊ0��
	 * 
	 * 
	 * 
	 * @param w
	 * 
	 *            Current width of this view.
	 * 
	 * @param h
	 * 
	 *            Current height of this view.
	 * 
	 * @param oldw
	 * 
	 *            Old width of this view.
	 * 
	 * @param oldh
	 * 
	 *            Old height of this view.
	 */

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		mCoveflowCenter = getCenterOfCoverflow();

		super.onSizeChanged(w, h, oldw, oldh);

	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
	
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			LogUtils2.e("*******");
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			LogUtils2.d("++++++++++++++++++");
		default:
			break;
		}
		LogUtils2.e("");
		
		return super.onTouchEvent(event);
	}
	
	
	/**
	 * 
	 * ��ͼ��λͼ�ĽǶ�ͨ��
	 * 
	 * 
	 * 
	 * @param imageView
	 * 
	 *            ImageView the ImageView whose bitmap we want to rotate
	 * 
	 * @param t
	 * 
	 *            transformation
	 * 
	 * @param rotationAngle
	 * 
	 *            the Angle by which to rotate the Bitmap
	 */

	private void transformImageBitmap(ImageView child, Transformation t,

	int rotationAngle) {

		mCamera.save();
		final Matrix imageMatrix = t.getMatrix();
		final int imageHeight = child.getLayoutParams().height;
		final int imageWidth = child.getLayoutParams().width;
		final int rotation = Math.abs(rotationAngle);

		// ��Z���������ƶ�camera���ӽǣ�ʵ��Ч��Ϊ�Ŵ�ͼƬ��
		// �����Y�����ƶ�����ͼƬ�����ƶ���X���϶�ӦͼƬ�����ƶ���
		mCamera.translate(0.0f, 0.0f, 100.0f);

		// As the angle of the view gets less, zoom in
		if (rotation < mMaxRotationAngle) {
			float zoomAmount = (float) (mMaxZoom + (rotation * 1.5));
			mCamera.translate(0.0f, 0.0f, zoomAmount);
		}
		// ��Y������ת����ӦͼƬ�������﷭ת��
		// �����X������ת�����ӦͼƬ�������﷭ת��
		mCamera.rotateY(rotationAngle);
		mCamera.getMatrix(imageMatrix);
		imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
		imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));
		mCamera.restore();

	}
	
	//override the method to control gallery speed not too fast
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if(velocityX > 2500 ){
			velocityX = 2500;
		}else if(velocityX > 0){
			velocityX = 1500;
		}else if(velocityX < -2500){
			velocityX = -2500;
		}else if(velocityX < 0){
			velocityX = -1500;
		}
		return super.onFling(e1, e2, velocityX, velocityY);
		
//		return false;
	}

}