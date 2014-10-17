package com.hua.activity;


import java.io.File;

import android.app.Service;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.hua.app.BaseApplication;
import com.hua.utils.LayoutUtils;
import com.hua.utils.LogUtils2;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class MTNApplication extends BaseApplication {

	private static final String TAG = MTNApplication.class.getSimpleName();
	private static MTNApplication app;
//	private AppServiceAccessAide mAppServiceAccessAide;
	
	public static MTNApplication getMTNApplication() {
		return app;
	}
	
	@Override
	public void onCreate() {
		app = this;
		super.onCreate();
//		mAppServiceAccessAide = new AppServiceAccessAide();
//		startService(new Intent(this, DownloadService.class));
		initImageLoader(getApplicationContext());
	}
	
//	public AppServiceAccessAide getAppServiceAccessAide() {
//		return mAppServiceAccessAide;
//	}
	
	public static void addService(Service service) {
//		getMTNApplication().getAppServiceAccessAide().addService(service);
	}
	
	public static void removeService(Service service) {
//		getMTNApplication().getAppServiceAccessAide().removeService(service);
	}
	
//	public static DownloadService getDownloadService() {
//		return (DownloadService)getMTNApplication().getAppServiceAccessAide().getService(DownloadService.class);
//	}
	
	public static void startFragment(Fragment oldFragment, Fragment newFragment) {
		FragmentActivity act = oldFragment.getActivity();
		if(act == null) {
			Log.d(TAG, "startFragment() failed : no host activity.");
			return;
		}
		if(act instanceof MainActivityPhone) {
			((MainActivityPhone)act).startFragment(newFragment);
		} else if(act instanceof MainActivityTablet) {
			((MainActivityTablet)act).startFragment(newFragment);
		} else {
			Log.d(TAG, "startFragment() failed : host activity not valid : " + act);
		}
	}
	
	public static void setBackButtonVisibility(Fragment oldFragment, boolean visible) {
		FragmentActivity act = oldFragment.getActivity();
		if(act == null) {
			Log.d(TAG, "setBackButtonVisibility() failed : no host activity.");
			return;
		}
		if(act instanceof MainActivityPhone) {
			((MainActivityPhone)act).setBackButtonVisible(visible);
		} else if(act instanceof MainActivityTablet) {
			// never show back button for tablet app.
			// ((MainActivityTablet)act).setBackButtonVisibility(visibility);
		} else {
			Log.d(TAG, "setBackButtonVisibility() failed : host activity not valid : " + act);
		}
	}
	
	public static void setFilterButtonVisibility(Fragment oldFragment, int visibility) {
		FragmentActivity act = oldFragment.getActivity();
		if(act == null) {
			Log.d(TAG, "setBackButtonVisibility() failed : no host activity.");
			return;
		}
		if(act instanceof MainActivityPhone) {
			((MainActivityPhone)act).setFilterButtonVisibility(visibility);
		} else if(act instanceof MainActivityTablet) {
			((MainActivityTablet)act).setFilterButtonVisibility(visibility);
		} else {
			Log.d(TAG, "setFilterButtonVisibility() failed : host activity not valid : " + act);
		}
	}
	
	public static boolean isTablet(Context context) {
		boolean isTablet = LayoutUtils.isTablet(context);
		//isTablet = true;
		return isTablet;
	}
	
	
	  /** 初始化ImageLoader */
    public static void initImageLoader(Context context) {
    	LogUtils2.e("initImageLoader------------");
    	String filePath = Environment.getExternalStorageDirectory() +
                "/Android/data/" + context.getPackageName() + "/cache/";

        File cacheDir = StorageUtils.getOwnCacheDirectory(context, filePath);// 获取到缓存的目录地址
        Log.d("cacheDir", cacheDir.getPath());
        // 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(context)
                        // .memoryCacheExtraOptions(480, 800) // max width, max
                        // height，即保存的每个缓存文件的最大长宽
                        // .discCacheExtraOptions(480, 800, CompressFormat.JPEG,
                        // 75, null) // Can slow ImageLoader, use it carefully
                        // (Better don't use it)设置缓存的详细信息，最好不要设置这个
                        .threadPoolSize(3)// 线程池内加载的数量
                        .threadPriority(Thread.NORM_PRIORITY - 2)
                        .denyCacheImageMultipleSizesInMemory()
                        .memoryCache(new WeakMemoryCache())
                        // .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024
                        // * 1024)) // You can pass your own memory cache
                        // implementation你可以通过自己的内存缓存实现
                        .memoryCacheSize(2 * 1024 * 1024)
                        // /.discCacheSize(50 * 1024 * 1024)
                        .discCacheFileNameGenerator(new Md5FileNameGenerator())// 将保存的时候的URI名称用MD5
                                                                               // 加密
                        // .discCacheFileNameGenerator(new
                        // HashCodeFileNameGenerator())//将保存的时候的URI名称用HASHCODE加密
                        .tasksProcessingOrder(QueueProcessingType.LIFO)
                         .discCacheFileCount(100) //缓存的File数量
                        .discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
                        .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                        .imageDownloader(new BaseImageDownloader(context, 5 *
                                1000, 30 * 1000)) // connectTimeout (5 s),
                        // readTimeout(30)// 超时时间
                        .writeDebugLogs() // Remove for release app
                        .build();
        
//        ImageLoader mImageLoader = ImageLoader.getInstance();
//        mImageLoader.
        //这样创建默认的ImageLoader配置器
//        ImageLoaderConfiguration mImageLoaderConfiguration = ImageLoaderConfiguration.createDefault(context);
        
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);// 全局初始化此配置
//        ImageSize mImageSize = new ImageSize(100, 100);
    }
	
	
}
