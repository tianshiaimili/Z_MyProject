package com.hua.view;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.hua.activity.ImageDetailsActivity;
import com.hua.activity.R;
import com.hua.model.Images;
import com.hua.util.LogUtils2;
import com.hua.util.MyImageLoader;
import com.hua.view.MyScrollView.LoadImageTask;


/**
 * ʵ�ֻص���scrollview
 * @author Hua
 *
 */
public class ElasticScrollViewTuanGou extends ScrollView  implements OnTouchListener{
	private static final String TAG = "ElasticScrollView";
	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;
	// ʵ�ʵ�padding�ľ����������ƫ�ƾ���ı���
	private final static int RATIO = 3;

	private int headContentWidth;
	private int headContentHeight;

	private LinearLayout innerLayout;
	private LinearLayout headView;
	private ImageView arrowImageView;//��ͷͼƬ
	private ProgressBar progressBar;
	private TextView tipsTextview;// ��ʾ���� �ɿ� ˢ�µ�����
	private TextView lastUpdatedTextView;
	private OnRefreshListener refreshListener;
	private boolean isRefreshable;
	private int state;
	/**
	 * �������������������������ʱ ��arrowImageViewͼƬ�����ж����ı䶯��
	 */
	private boolean isBack;

	private RotateAnimation animation; //��ͷ�Ķ���
	private RotateAnimation reverseAnimation;

	private boolean canReturn; //������ʾ�ɿ��ֺ�Ϳ��Իص���
	private boolean isRecored;
	private int startY;

	//////////////////////////////////////
	/**
	 * ������������
	 */

	/**
	 * ÿҳҪ���ص�ͼƬ����
	 */
	public static final int PAGE_SIZE = 15;

	/**
	 * ��¼��ǰ�Ѽ��ص��ڼ�ҳ
	 */
	private int page;

	/**
	 * ÿһ�еĿ��
	 */
	private int columnWidth;

	/**
	 * ��ǰ��һ�еĸ߶�
	 */
	private int firstColumnHeight;

	/**
	 * ��ǰ�ڶ��еĸ߶�
	 */
	private int secondColumnHeight;

	/**
	 * ��ǰ�����еĸ߶�
	 */
	private int thirdColumnHeight;

	/**
	 * �Ƿ��Ѽ��ع�һ��layout������onLayout�еĳ�ʼ��ֻ�����һ��
	 */
	private boolean loadOnce;

	/**
	 * ��ͼƬ���й���Ĺ�����
	 */
	private MyImageLoader imageLoader;

	/**
	 * ��һ�еĲ���
	 */
	private LinearLayout firstColumn;

	/**
	 * �ڶ��еĲ���
	 */
	private LinearLayout secondColumn;

	/**
	 * �����еĲ���
	 */
	private LinearLayout thirdColumn;

	/**
	 * ��¼�����������ػ�ȴ����ص�����
	 */
	private static Set<LoadImageTask> taskCollection;

	/**
	 * MyScrollView�µ�ֱ���Ӳ��֡�
	 */
	private static View scrollLayout;

	/**
	 * MyScrollView���ֵĸ߶ȡ�
	 */
	private static int scrollViewHeight;

	/**
	 * ��¼�ϴ�ֱ����Ĺ������롣
	 */
	private static int lastScrollY = -1;

	/**
	 * ��¼���н����ϵ�ͼƬ�����Կ�����ʱ���ƶ�ͼƬ���ͷš�
	 */
	private List<ImageView> imageViewList = new ArrayList<ImageView>();
	/**
	 * ��Handler�н���ͼƬ�ɼ��Լ����жϣ��Լ����ظ���ͼƬ�Ĳ�����
	 */
	private static Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			MyScrollView myScrollView = (MyScrollView) msg.obj;
			int scrollY = myScrollView.getScrollY();
			// �����ǰ�Ĺ���λ�ú��ϴ���ͬ����ʾ��ֹͣ����
			if (scrollY == lastScrollY) {
				// ����������ײ������ҵ�ǰû���������ص�����ʱ����ʼ������һҳ��ͼƬ
				if (scrollViewHeight + scrollY >= scrollLayout.getHeight()
						&& taskCollection.isEmpty()) {
					myScrollView.loadMoreImages();
				}
				myScrollView.checkVisibility();
			} else {
				lastScrollY = scrollY;
				Message message = new Message();
				message.obj = myScrollView;
				// 5������ٴζԹ���λ�ý����ж�
				handler.sendMessageDelayed(message, 5);
			}
		};

	};
	
	
	public ElasticScrollViewTuanGou(Context context) {
		super(context);
//		imageLoader = MyImageLoader.getInstance();
//		taskCollection = new HashSet<LoadImageTask>();
//		setOnTouchListener(this);
		init(context);
		
		
	}

	public ElasticScrollViewTuanGou(Context context, AttributeSet attrs) {
		super(context, attrs);
		imageLoader = MyImageLoader.getInstance();
		taskCollection = new HashSet<LoadImageTask>();
		setOnTouchListener(this);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		innerLayout = new LinearLayout(context);
		innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		innerLayout.setOrientation(LinearLayout.VERTICAL);
		
		/**
		 * ����ʱ ��ʾ�Ĳ���
		 */
		headView = (LinearLayout) inflater.inflate(R.layout.myscrollview_head,
				null);
		
		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		
		/**
		 * ��ʾ���� �ɿ� ˢ�µ�����
		 */
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);
		/**
		 * 
		 */
		measureView(headView);

		/**
		 * ��ȡ����headerview�Ĵ�С�Ϳ�Ȼ������paddingֵ��Ȼ����һ��ʼ
		 * ����ʾ��screen
		 */
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.i("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);
		//��headerView��ӵ��ڲ���linearlayout��
		innerLayout.addView(headView);
		/**
		 * ��Linearlayout��ӵ���ǰ��ScrollView��
		 */
		addView(innerLayout);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
		canReturn = false;
	}

	
	/**
	 * ����һЩ�ؼ��Եĳ�ʼ����������ȡMyScrollView�ĸ߶ȣ��Լ��õ���һ�еĿ��ֵ���������￪ʼ���ص�һҳ��ͼƬ��
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed && !loadOnce) {
			scrollViewHeight = getHeight();
			scrollLayout = getChildAt(1);
			firstColumn = (LinearLayout) findViewById(R.id.first_column);
			secondColumn = (LinearLayout) findViewById(R.id.second_column);
			thirdColumn = (LinearLayout) findViewById(R.id.third_column);
			columnWidth = firstColumn.getWidth();
			loadOnce = true;
			loadMoreImages();
		}
	}
	

	/**
	 * �����û��Ĵ����¼�������û���ָ�뿪��Ļ��ʼ���й�����⡣
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			LogUtils2.i("onTouch...............");
			Message message = new Message();
			message.obj = this;
			handler.sendMessageDelayed(message, 5);
		}
		return false;
	}

	
	/**
	 * ��ʼ������һҳ��ͼƬ��ÿ��ͼƬ���Ὺ��һ���첽�߳�ȥ���ء�
	 */
	public void loadMoreImages() {
		if (hasSDCard()) {
			int startIndex = page * PAGE_SIZE;
			int endIndex = page * PAGE_SIZE + PAGE_SIZE;
			if (startIndex < Images.imageUrls.length) {
				Toast.makeText(getContext(), "���ڼ���...", Toast.LENGTH_SHORT).show();
				if (endIndex > Images.imageUrls.length) {
					endIndex = Images.imageUrls.length;
				}
				for (int i = startIndex; i < endIndex; i++) {
					LoadImageTask task = new LoadImageTask();
					taskCollection.add(task);
					task.execute(i);
				}
				page++;
			} else {
				Toast.makeText(getContext(), "��û�и���ͼƬ", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getContext(), "δ����SD��", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ����imageViewList�е�ÿ��ͼƬ����ͼƬ�Ŀɼ��Խ��м�飬���ͼƬ�Ѿ��뿪��Ļ�ɼ���Χ����ͼƬ�滻��һ�ſ�ͼ��
	 */
	public void checkVisibility() {
		for (int i = 0; i < imageViewList.size(); i++) {
			ImageView imageView = imageViewList.get(i);
			int borderTop = (Integer) imageView.getTag(R.string.border_top);
			int borderBottom = (Integer) imageView.getTag(R.string.border_bottom);
			if (borderBottom > getScrollY() && borderTop < getScrollY() + scrollViewHeight) {
				String imageUrl = (String) imageView.getTag(R.string.image_url);
				Bitmap bitmap = imageLoader.getBitmapFromMemoryCache(imageUrl);
				if (bitmap != null) {
					imageView.setImageBitmap(bitmap);
				} else {
					LoadImageTask task = new LoadImageTask(imageView);
					task.execute(i);
				}
			} else {
				imageView.setImageResource(R.drawable.empty_photo);
			}
		}
	}

	/**
	 * �ж��ֻ��Ƿ���SD����
	 * 
	 * @return ��SD������true��û�з���false��
	 */
	private boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	
	/**
	 * �첽����ͼƬ������
	 * 
	 * @author guolin
	 */
	class LoadImageTask extends AsyncTask<Integer, Void, Bitmap> {

		/**
		 * ��¼ÿ��ͼƬ��Ӧ��λ��
		 */
		private int mItemPosition;

		/**
		 * ͼƬ��URL��ַ
		 */
		private String mImageUrl;

		/**
		 * ���ظ�ʹ�õ�ImageView
		 */
		private ImageView mImageView;

		public LoadImageTask() {
		}

		/**
		 * �����ظ�ʹ�õ�ImageView����
		 * 
		 * @param imageView
		 */
		public LoadImageTask(ImageView imageView) {
			mImageView = imageView;
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {
			mItemPosition = params[0];
			mImageUrl = Images.imageUrls[mItemPosition];
			Bitmap imageBitmap = imageLoader.getBitmapFromMemoryCache(mImageUrl);
			if (imageBitmap == null) {
				imageBitmap = loadImage(mImageUrl);
			}
			return imageBitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (bitmap != null) {
				double ratio = bitmap.getWidth() / (columnWidth * 1.0);
				int scaledHeight = (int) (bitmap.getHeight() / ratio);
				addImage(bitmap, columnWidth, scaledHeight);
			}
			taskCollection.remove(this);
		}

		/**
		 * ���ݴ����URL����ͼƬ���м��ء��������ͼƬ�Ѿ�������SD���У���ֱ�Ӵ�SD�����ȡ������ʹ����������ء�
		 * 
		 * @param imageUrl
		 *            ͼƬ��URL��ַ
		 * @return ���ص��ڴ��ͼƬ��
		 */
		private Bitmap loadImage(String imageUrl) {
			File imageFile = new File(getImagePath(imageUrl));
			if (!imageFile.exists()) {
				downloadImage(imageUrl);
			}
			if (imageUrl != null) {
				Bitmap bitmap = MyImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(),
						columnWidth);
				if (bitmap != null) {
					imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
					return bitmap;
				}
			}
			return null;
		}

		/**
		 * ��ImageView�����һ��ͼƬ
		 * 
		 * @param bitmap
		 *            ����ӵ�ͼƬ
		 * @param imageWidth
		 *            ͼƬ�Ŀ��
		 * @param imageHeight
		 *            ͼƬ�ĸ߶�
		 */
		private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth,
					imageHeight);
			if (mImageView != null) {
				mImageView.setImageBitmap(bitmap);
			} else {
				ImageView imageView = new ImageView(getContext());
				imageView.setLayoutParams(params);
				imageView.setImageBitmap(bitmap);
				imageView.setScaleType(ScaleType.FIT_XY);
				imageView.setPadding(5, 5, 5, 5);
				imageView.setTag(R.string.image_url, mImageUrl);
				imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getContext(), ImageDetailsActivity.class);
						intent.putExtra("image_position", mItemPosition);
						getContext().startActivity(intent);
					}
				});
				findColumnToAdd(imageView, imageHeight).addView(imageView);
				imageViewList.add(imageView);
			}
		}

		/**
		 * �ҵ���ʱӦ�����ͼƬ��һ�С�ԭ����Ƕ����еĸ߶Ƚ����жϣ���ǰ�߶���С��һ�о���Ӧ����ӵ�һ�С�
		 * 
		 * @param imageView
		 * @param imageHeight
		 * @return Ӧ�����ͼƬ��һ��
		 */
		private LinearLayout findColumnToAdd(ImageView imageView, int imageHeight) {
			if (firstColumnHeight <= secondColumnHeight) {
				if (firstColumnHeight <= thirdColumnHeight) {
					imageView.setTag(R.string.border_top, firstColumnHeight);
					firstColumnHeight += imageHeight;
					imageView.setTag(R.string.border_bottom, firstColumnHeight);
					return firstColumn;
				}
				imageView.setTag(R.string.border_top, thirdColumnHeight);
				thirdColumnHeight += imageHeight;
				imageView.setTag(R.string.border_bottom, thirdColumnHeight);
				return thirdColumn;
			} else {
				if (secondColumnHeight <= thirdColumnHeight) {
					imageView.setTag(R.string.border_top, secondColumnHeight);
					secondColumnHeight += imageHeight;
					imageView.setTag(R.string.border_bottom, secondColumnHeight);
					return secondColumn;
				}
				imageView.setTag(R.string.border_top, thirdColumnHeight);
				thirdColumnHeight += imageHeight;
				imageView.setTag(R.string.border_bottom, thirdColumnHeight);
				return thirdColumn;
			}
			
		}

		/**
		 * ��ͼƬ���ص�SD������������
		 * 
		 * @param imageUrl
		 *            ͼƬ��URL��ַ��
		 */
		private void downloadImage(String imageUrl) {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				Log.d("TAG", "monted sdcard");
			} else {
				Log.d("TAG", "has no sdcard");
			}
			HttpURLConnection con = null;
			FileOutputStream fos = null;
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
			File imageFile = null;
			try {
				URL url = new URL(imageUrl);
				con = (HttpURLConnection) url.openConnection();
				con.setConnectTimeout(5 * 1000);
				con.setReadTimeout(15 * 1000);
				con.setDoInput(true);
				con.setDoOutput(true);
				bis = new BufferedInputStream(con.getInputStream());
				imageFile = new File(getImagePath(imageUrl));
				fos = new FileOutputStream(imageFile);
				bos = new BufferedOutputStream(fos);
				byte[] b = new byte[1024];
				int length;
				while ((length = bis.read(b)) != -1) {
					bos.write(b, 0, length);
					bos.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bis != null) {
						bis.close();
					}
					if (bos != null) {
						bos.close();
					}
					if (con != null) {
						con.disconnect();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (imageFile != null) {
				//������������ͼƬ �ʵ��ı��С��Ȼ����뵽����
				Bitmap bitmap = MyImageLoader.decodeSampledBitmapFromResource(imageFile.getPath(),
						columnWidth);
				if (bitmap != null) {
					imageLoader.addBitmapToMemoryCache(imageUrl, bitmap);
				}
			}
		}

		/**
		 * ��ȡͼƬ�ı��ش洢·����
		 * 
		 * @param imageUrl
		 *            ͼƬ��URL��ַ��
		 * @return ͼƬ�ı��ش洢·����
		 */
		private String getImagePath(String imageUrl) {
			int lastSlashIndex = imageUrl.lastIndexOf("/");
			String imageName = imageUrl.substring(lastSlashIndex + 1);
			String imageDir = Environment.getExternalStorageDirectory().getPath()
					+ "/PhotoWallFalls/";
			File file = new File(imageDir);
			if (!file.exists()) {
				file.mkdirs();
			}
			String imagePath = imageDir + imageName;
			return imagePath;
		}
	}
	
	/**
	 * �����ǹ���ˢ�µĲ���
	 */
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (getScrollY() == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.i(TAG, "��downʱ���¼��ǰλ�á�");
					LogUtils2.i("��downʱ���¼��ǰλ�á�==startY="+startY);
				}
				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				
				if (!isRecored && getScrollY() == 0) {
					Log.i(TAG, "��moveʱ���¼��λ��");
					isRecored = true;
					startY = tempY;
				}
				
				LogUtils2.d("tempY=="+tempY+"   startY=="+startY);
				/**
				 *  done״̬��  һ��ʼ��״̬
				 */
				if (state == DONE) {
					if (tempY - startY > 0) {
						state = PULL_To_REFRESH;
						changeHeaderViewByState();
					}
				}

				/**
				 *  ��û�е�����ʾ�ɿ�ˢ�µ�ʱ��,DONE������PULL_To_REFRESH״̬
				 */
				if (state == PULL_To_REFRESH) {
					canReturn = true;

					// ���������Խ���RELEASE_TO_REFRESH��״̬
					if ((tempY - startY) / RATIO >= headContentHeight) {
						state = RELEASE_To_REFRESH;
						isBack = true;
						changeHeaderViewByState();
						LogUtils2.i("��done��������ˢ��״̬ת�䵽�ɿ�ˢ��");
					}else if (tempY - startY <= 0) {
						// ���Ƶ�����
						state = DONE;
						changeHeaderViewByState();
						Log.i(TAG, "��DOne��������ˢ��״̬ת�䵽done״̬");
						LogUtils2.i("��DOne��������ˢ��״̬ת�䵽done״̬");
					}
				}
				
				/**
				 * ���������õ���ָ������ȥˢ�µ�ʱ��
				 */
				if (state != REFRESHING && isRecored && state != LOADING) {
					// ��������ȥˢ����
					if (state == RELEASE_To_REFRESH) {
						LogUtils2.i("*******************");
						canReturn = true;

						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
							Log.i(TAG, "���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");
							LogUtils2.i("���ɿ�ˢ��״̬ת�䵽����ˢ��״̬");
						}
						// һ�����Ƶ�����
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
							Log.i(TAG, "���ɿ�ˢ��״̬ת�䵽done״̬");
							LogUtils2.i("���ɿ�ˢ��״̬ת�䵽done״̬");
						} else {
							// ���ý����ر�Ĳ�����ֻ�ø���paddingTop��ֵ������
						}
					}

					// ����headView��size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}

					// ����headView��paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}
					
					
					if (canReturn) {
						canReturn = false;
						return true;
					}
				}
				break;
				
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// ʲô������
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
						Log.i(TAG, "������ˢ��״̬����done״̬");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						LogUtils2.i("�������..onRefresh");
						onRefresh();
						Log.i(TAG, "���ɿ�ˢ��״̬����done״̬");
					}
				}
				isRecored = false;
				isBack = false;

				break;
				
			}
		}
		return super.onTouchEvent(event);
	}

	// ��״̬�ı�ʱ�򣬵��ø÷������Ը��½���
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("�ɿ�ˢ��");

			LogUtils2.i("��ǰ״̬���ɿ�ˢ��*******");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// ����RELEASE_To_REFRESH״̬ת������
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("����ˢ��");
			} else {
				tipsTextview.setText("����ˢ��");
			}
			Log.i(TAG, "��ǰ״̬������ˢ��");
			LogUtils2.d("��ǰ״̬������ˢ��...");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("����ˢ��...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			Log.i(TAG, "��ǰ״̬,����ˢ��...");
			LogUtils2.d("��ǰ״̬,����ˢ��...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			arrowImageView.setImageResource(R.drawable.goicon);
			tipsTextview.setText("����ˢ��");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			LogUtils2.i("��ǰ״̬��done......");
			break;
		}
	}

	/**
	 * MeasureSpec��װ��parent���ݸ�child��layoutҪ��ÿ��MeasureSpec��ʾ��width/height��Ҫ��
	 * MeasureSpec��size��mode��ɡ����õ�mode��3�֣�
	1. UNSPECIFIED��ʾparentû��ǿ�Ӹ�child�κ�constraint��
	2. EXACTLY��ʾparent�Ѿ�ȷ��child�ľ�ȷsize��
	3. AT_MOST��ʾchild�����趨Ϊspecified size֮�ڵ��κ�ֵ��
	 * @param child
	 */
	private void measureView(View child) {
		
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		
		/**
		 * ����Ӧ���ǲ�����ȡ��View�Ĵ�С�ѣ�Ȼ���ڸ�view�и������ʵĴ�С��ʾ
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

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		lastUpdatedTextView.setText("�������:" + new Date().toLocaleString());
		changeHeaderViewByState();
		invalidate();
		scrollTo(0, 0);
	}

	private void onRefresh() {
		if (refreshListener != null) {
			LogUtils2.i("��ʼ...........onRefresh");
			refreshListener.onRefresh();
		}
	}

	public void addChild(View child) {
		innerLayout.addView(child);
	}

	public void addChild(View child, int position) {
		innerLayout.addView(child, position);
	}

}
