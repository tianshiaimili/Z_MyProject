package com.hua.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hua.activity.R;
import com.hua.contants.Constant;
import com.hua.model.AppData.HomeBannerData;
import com.hua.model.AppData.TempAppData;
import com.hua.utils.JsonZip.ZipType;

/**
 * 对图片进行管理的工具类。
 * 
 */
public class MyImageLoader {

	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	public static Set<MyLoadImageTask> taskCollection;
	/**
	 * 记录所有界面上的图片，用以可以随时控制对图片的释放。
	 */
	private static List<ImageView> imageViewList = new ArrayList<ImageView>();

	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private static LruCache<String, Bitmap> mMemoryCache;

	/**
	 * ImageLoader的实例。
	 */
	private static MyImageLoader mImageLoader;
	private static Context mContext;

	public MyImageLoader(Context context) {

		taskCollection = new HashSet<MyLoadImageTask>();
		mContext = context;
		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		// 设置图片缓存大小为程序最大可用内存的1/8
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getByteCount();
			}
		};
	}

	/**
	 * 获取ImageLoader的实例。
	 * 
	 * @return ImageLoader的实例。
	 */
	public static MyImageLoader getInstance(Context context) {
		if (mImageLoader == null) {
			mImageLoader = new MyImageLoader(context);
		}
		return mImageLoader;
	}

	/**
	 * 将一张图片存储到LruCache中。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @param bitmap
	 *            LruCache的键，这里传入从网络上下载的Bitmap对象。
	 */
	public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemoryCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 * 
	 * @param key
	 *            LruCache的键，这里传入图片的URL地址。
	 * @return 对应传入键的Bitmap对象，或者null。
	 */
	public static Bitmap getBitmapFromMemoryCache(String key) {
		return mMemoryCache.get(key);
	}

	/**
	 * 计算图片显示合适的比例
	 * 
	 * @param options
	 * @param reqWidth
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth) {
		// 源图片的宽度
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (width > reqWidth) {
			// 计算出实际宽度和目标宽度的比率
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 可参考http://blog.csdn.net/hustpzb/article/details/8363372
	 * BitmapFactory.decodeFile(imageFile); 用BitmapFactory解码一张图片时，有时会遇到该错误。
	 * 这往往是由于图片过大造成的。要想正常使用，则需要分配更少的内存空间来存储。
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		// BitmapFactory.Options options2 = new BitmapFactory.Options();
		// options2.inJustDecodeBounds = true;
		// BitmapFactory.decodeFile(pathName, options2);
		// options2.inSampleSize =
		/**
		 * decodeFile（..）并不分配空间，但可计算出原始图片的长度和宽度，即opts.width和opts.height。
		 * 有了这两个参数，再通过一定的算法，即可得到一个恰当的inSampleSize。
		 */
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

	/**
	 * 获取图片的本地存储路径。
	 * 
	 * @param imageUrl
	 *            图片的URL地址。
	 * @return 图片的本地存储路径。
	 */
	private static String getImagePath(String imageUrl) {
		LogUtils2.i("ImageURL==" + imageUrl);
		int lastSlashIndex = imageUrl.lastIndexOf("/");
		String imageName = imageUrl.substring(lastSlashIndex + 1);
		String imageDir = Environment.getExternalStorageDirectory().getPath()
				+ "/PhotoWallFalls/";
		File file = new File(imageDir);
		if (!file.exists()) {
			LogUtils2.d("xxxxxxxxxxxxxxxxx");
			file.mkdirs();
		}
		String imagePath = imageDir + imageName;
		LogUtils2.d("imagePath==" + imagePath);
		return imagePath;
	}

	/**
	 * 下载图片
	 * 
	 * @param imageUrl
	 */
	public static void downloadImages(String imageUrl, int columnWidth) {

		if (imageUrl == null || imageUrl.equals(""))
			return;
		String urlHead = "http";
		String tempUrlHead = imageUrl.substring(0, 5);
		LogUtils2.d("tempUrlHead==" + tempUrlHead);
		LogUtils2.d("imageUrl==" + imageUrl);
		if (tempUrlHead.equals("http:")) {
			LogUtils2.d("--------------");
			downloadImageForHttp(imageUrl, columnWidth);
		} else if (tempUrlHead.equals("https")) {
			downloadImageForHttps(imageUrl, columnWidth);
		}

	}

	/**
	 * 通过http下载的图片
	 * 
	 * @param imageUrl
	 */
	public static void downloadImageForHttp(String imageUrl, int columnWidth) {
		LogUtils2.d("downloadImageForHttp----");
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
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
			byte[] buff = new byte[1024];
			int length;
			while ((length = bis.read(buff)) != -1) {
				bos.write(buff, 0, length);
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
			LogUtils2.d("8888888888888888");
			LogUtils2.d("getImagePath(imageUrl)==" + getImagePath(imageUrl));
			// 根据下载来的图片 适当改变大小，然后放入到缓存
			Bitmap bitmap = MyImageLoader.decodeSampledBitmapFromResource(
					imageFile.getPath(), columnWidth);
			if (bitmap != null) {
				addBitmapToMemoryCache(imageUrl, bitmap);
			}
		}

	}

	/**
	 * 通过https下载图片
	 * 
	 * @param imageUrl
	 */
	public static void downloadImageForHttps(String imageUrl, int columnWidth) {

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Log.d("TAG", "monted sdcard");
		} else {
			Log.d("TAG", "has no sdcard");
		}
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;
		File imageFile = null;
		HttpsURLConnection conn = null;

		try {
			URL url = new URL(imageUrl);
			SSLContext sslctxt = SSLContext.getInstance("TLS");

			sslctxt.init(null, new TrustManager[] { new MyX509TrustManager() },
					new java.security.SecureRandom());

			conn = (HttpsURLConnection) url.openConnection();

			conn.setSSLSocketFactory(sslctxt.getSocketFactory());
			conn.setHostnameVerifier(new MyHostnameVerifier());
			conn.connect();
			imageFile = new File(getImagePath(imageUrl));
			bis = new BufferedInputStream(conn.getInputStream());
			fos = new FileOutputStream(imageFile);
			bos = new BufferedOutputStream(fos);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = bis.read(buffer)) != -1) {

				bos.write(buffer, 0, length);
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
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (imageFile != null) {
			LogUtils2.d("89999998");
			LogUtils2.d("getImagePath(imageUrl)==" + getImagePath(imageUrl));
			// bitmap = BitmapFactory.decodeFile(getImagePath(imageUrl));
			// 根据下载来的图片 适当改变大小，然后放入到缓存
			Bitmap bitmap = MyImageLoader.decodeSampledBitmapFromResource(
					imageFile.getPath(), columnWidth);
			if (bitmap != null) {
				LogUtils2.d("add to the cache------");
				addBitmapToMemoryCache(imageUrl, bitmap);
			}
		}

	}

	/**
	 * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
	 * 
	 * @param imageUrl
	 *            图片的URL地址
	 * @return 加载到内存的图片。
	 */
	public static Bitmap loadImage(String imageUrl, int columnWidth) {
		File imageFile = new File(getImagePath(imageUrl));
		if (!imageFile.exists()) {
			// imageLoader.downloadImages(imageUrl,columnWidth);
			downloadImages(imageUrl, columnWidth);
		}
		if (imageUrl != null) {
			Bitmap bitmap = MyImageLoader.decodeSampledBitmapFromResource(
					imageFile.getPath(), columnWidth);
			if (bitmap != null) {
				addBitmapToMemoryCache(imageUrl, bitmap);
				return bitmap;
			}
		}
		return null;
	}

	static class MyX509TrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			if (null != chain) {
				for (int k = 0; k < chain.length; k++) {
					X509Certificate cer = chain[k];
					print(cer);
				}
			}
			LogUtils2.d("check client trusted. authType=" + authType);

		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			if (null != chain) {
				for (int k = 0; k < chain.length; k++) {
					X509Certificate cer = chain[k];
					print(cer);
				}
			}
			LogUtils2.d("check servlet trusted. authType=" + authType);
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {

			LogUtils2.d("get acceptedissuer");

			return null;
		}

		private void print(X509Certificate cer) {

			int version = cer.getVersion();
			String sinname = cer.getSigAlgName();
			String type = cer.getType();
			String algorname = cer.getPublicKey().getAlgorithm();
			BigInteger serialnum = cer.getSerialNumber();
			Principal principal = cer.getIssuerDN();
			String principalname = principal.getName();

			LogUtils2.d("version=" + version + ", sinname=" + sinname
					+ ", type=" + type + ", algorname=" + algorname
					+ ", serialnum=" + serialnum + ", principalname="
					+ principalname);
		}

	}

	static class MyHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			LogUtils2.d("hostname=" + hostname + ",PeerHost= "
					+ session.getPeerHost());
			return true;
		}

	}

	/**
	 * 图片异步加载的类
	 */
	public static class MyLoadImageTask extends
			AsyncTask<Integer, Void, Bitmap> {

		private static Context context;
		/**
		 * 图片的url集合
		 */
		private List<String> imageUrlList;
		/**
		 * 记录每个图片对应的位置
		 */
		private int mItemPosition;

		/**
		 * 图片的URL地址
		 */
		private String mImageUrl;

		/**
		 * 可重复使用的ImageView
		 */
		private ImageView mImageView;

		/**
		 * 图片显示的宽度
		 */
		private int columnWidth;
		/**
		 * 内部的task集合
		 */
		public static Set<MyLoadImageTask> tempTtaskCollection;
		
		/**
		 * 内部类 记录所有界面上的图片，用以可以随时控制对图片的释放。
		 */
		public static List<ImageView> innerImageViewList = new ArrayList<ImageView>();
		/**
		 * 内部类 保存bitmap集合
		 */
		public static List<Bitmap> innerBitmapList = new ArrayList<Bitmap>();
		
		

		public MyLoadImageTask() {
		}

		/**
		 * 将可重复使用的ImageView传入
		 * 
		 * @param imageView
		 */
		public MyLoadImageTask(ImageView imageView, List<String> list,
				int tempColumnWidth, Context tempContext) {
			mImageView = imageView;
			imageUrlList = list;
			columnWidth = tempColumnWidth;
			context = tempContext;
			tempTtaskCollection = new HashSet<MyLoadImageTask>();
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Bitmap doInBackground(Integer... params) {

			mItemPosition = params[0];
			mImageUrl = imageUrlList.get(mItemPosition);
			Bitmap bitmap = getBitmapFromMemoryCache(mImageUrl);
			if (bitmap == null) {
				// 缓存中 没有 则从网络获取
				bitmap = loadImage(mImageUrl, columnWidth);
			}

			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {

			if (bitmap != null) {
				double ratio = bitmap.getWidth() / (columnWidth * 1.0);
				int scaledHeight = (int) (bitmap.getHeight() / ratio);
				addImage(bitmap, columnWidth, scaledHeight);
			}
			if (taskCollection != null) {

				taskCollection.remove(this);
			}
			if (tempTtaskCollection != null) {

				tempTtaskCollection.remove(this);
			}
			// super.onPostExecute(result);
		}

		/**
		 * 向ImageView中添加一张图片
		 * 
		 * @param bitmap
		 *            待添加的图片
		 * @param imageWidth
		 *            图片的宽度
		 * @param imageHeight
		 *            图片的高度
		 */
		private void addImage(Bitmap bitmap, int imageWidth, int imageHeight) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					imageWidth, imageHeight);
			if (mImageView != null) {
				mImageView.setImageBitmap(bitmap);
			} else {
				ImageView imageView = new ImageView(context);
				imageView.setLayoutParams(params);
				imageView.setImageBitmap(bitmap);
				imageView.setScaleType(ScaleType.FIT_XY);
				// imageView.setPadding(5, 5, 5, 5);
				imageView.setTag(R.string.image_url, mImageUrl);
				// imageView.setOnClickListener(new OnClickListener() {
				// @Override
				// public void onClick(View v) {
				// Intent intent = new Intent(context,
				// ImageDetailsActivity.class);
				// intent.putExtra("image_position", mItemPosition);
				// context.startActivity(intent);
				// }
				// });
				// findColumnToAdd(imageView, imageHeight).addView(imageView);
				if(imageViewList != null)
				imageViewList.add(imageView);
				
				if(innerImageViewList != null)
				innerImageViewList.add(imageView);
				Constant.bannerImageViews.add(imageView);
				
				if(innerBitmapList != null){
					Constant.bannerBitmaps.add(bitmap);
				}
				
				LogUtils2.d("9999++=="+innerImageViewList.size());
				LogUtils2.d("8888++=="+Constant.bannerImageViews.size());
				if(Constant.bannerImageViews.size() == 4){
//					Constant.setBannerImageViews(innerImageViewList);
				}
			}
		}

	}

	
	/**
	 * Download JSON ZIP 
	 * @param zipType Type of JSON Zip.
	 * @return true if file updated and downloaded; false if download fail.
	 */	
	private boolean downloadJSONZipVersion(ZipType zipType) {
		boolean updated = false;
		try {
			URL url = new URL(/*getJsonZipUrl(zipType)*/"");
			URLConnection connection = url.openConnection();
			
            connection.connect();
            
            InputStream input = new BufferedInputStream(url.openStream());
            BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			StringBuilder newContent = new StringBuilder();
            String line;
		    while ((line = buffer.readLine()) != null) {
		    	newContent.append(line);
		    }
            
		    String oldContent = "";//getCurrentJSONZipPath(zipType);
            if (oldContent != null && oldContent.equals(newContent)) {
            	// update file date only
            	File versionFile = new File(mContext.getCacheDir(), /*zipType.versionFileName*/"");
            	Time time = new Time();
            	time.setToNow();
            	versionFile.setLastModified(time.toMillis(false));
            } else {
            	OutputStream output = new FileOutputStream(new File(mContext.getCacheDir(), /*zipType.versionFileName*/""));
            	output.write(newContent.toString().getBytes());
            	output.flush();
            	output.close();
            }
        	updated = true;
            
            input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return updated;
	}
	
	/**
	 * 下载文件 到手机中
	 * @param url_address
	 * @return
	 * @throws Exception
	 */
	public static boolean  downDataFiles(String imageUrl)
			throws Exception {
		LogUtils2.d("downDataFiles---------------");
		String path =getDataFileNamePath(imageUrl);
		LogUtils2.d("path=="+path);
		URL url = new URL(imageUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			LogUtils2.e("ppppttrrtrtrtpp");
			// byte[] buffer = readInputStream(conn.getInputStream());
			InputStream in = conn.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			LogUtils2.e("ppppttrrtrtrtpp");
			File file = new File(path);
			LogUtils2.e("p22222222222p");
			FileOutputStream outputStream = new FileOutputStream(file);
			
			LogUtils2.e("ppppppppppp");
			
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
//				baos.write(buffer, 0, len);
				outputStream.write(buffer, 0, len);
				outputStream.flush();
			}
			in.close();
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * 获取已经下载了的json数据文件路径
	 * @param imageUrl
	 * @return
	 */
	public  static String getDataFileNamePath(String imageUrl) {
		LogUtils2.i("ImageURL==" + imageUrl);
		int lastSlashIndex = imageUrl.lastIndexOf("/");
		String imageName = imageUrl.substring(lastSlashIndex + 1);
		String imageDir = Environment.getExternalStorageDirectory().getPath()
				+ Constant.APPFILE_DATA_PATH;
		File file = new File(imageDir);
		if (!file.exists()) {
			LogUtils2.d("xxxxxx88888887787787xxxxx");
			file.mkdirs();
		}
		String imagePath = imageDir + imageName;
		LogUtils2.d("imagePath==" + imagePath);
		return imagePath;
	}
	
	
	/**
	 * 判断要下载的文件是否已经存在
	 * @param imageUrl
	 * @return
	 */
	public static boolean isDataFileExist(String imageUrl){
		
		int lastSlashIndex = imageUrl.lastIndexOf("/");
		String imageName = imageUrl.substring(lastSlashIndex + 1);
		
		String dataFilePath = Environment.getExternalStorageDirectory().getPath()
				+ Constant.APPFILE_DATA_PATH+imageName;
		LogUtils2.i("imageDir----"+dataFilePath);
		File file = new File(dataFilePath);
		if(!file.exists()){
			LogUtils2.e("file not exist-----");
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 读取已经存在的数据文件 获取json格式的数据
	 * @param url_address
	 * @param c
	 * @return
	 * @throws Exception
	 */
	public static Object getDataFromExistFile(String filePath)
			throws Exception {
		LogUtils2.d("getDataFromExistFile-----------");
		if(filePath == null || filePath.equals("")){
			return null;
		}
		
		File file = new File(filePath);
		
		FileInputStream inputStream = new FileInputStream(file);
		BufferedInputStream  reader = new BufferedInputStream(inputStream);
//		FileOutputStream outputStream = new 
//		BufferedWriter writer = new 
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = reader.read(buffer)) != -1){
			
			bos.write(buffer, 0, len);
			bos.flush();
		}
		
		reader.close();
		
		byte[] buf = bos.toByteArray();
		String temp = new String(buf);
		return temp;
		
//		return bos.toByteArray();
	}
	
	/**
	 * 解析json数据获取 HomeBannerData list集合
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	public static List<HomeBannerData> fromHomeBannerDataJson(String jsonStr)
			throws Exception {
		LogUtils2.e("----fromJson------");
		Gson gson = new Gson();
		Type list = new TypeToken<List<HomeBannerData>>() {
		}.getType();
		
//		Type type = new TypeToken<>
		
		List<HomeBannerData> data = gson.fromJson(jsonStr, list);
		
		return data;
	}
	
	/**
	 * 解析json数据获取 HomeBannerData list集合
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	public static List<TempAppData> fromTempAppDataJson(String jsonStr)
			throws Exception {
		LogUtils2.e("----fromJson------");
		Gson gson = new Gson();
		Type list = new TypeToken<List<TempAppData>>() {
		}.getType();
		
//		Type type = new TypeToken<>
		
		List<TempAppData> data = gson.fromJson(jsonStr, list);
		
		return data;
	}
	
}
