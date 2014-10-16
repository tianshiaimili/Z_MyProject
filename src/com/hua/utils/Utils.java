package com.hua.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;

public class Utils {
	private static final String TAG = "Utils";

	public static String getNetworkConectivity(final Context context){
		int result = checkNetworkConnectivity(context);
		if(result==ConnectivityManager.TYPE_WIFI){
			return "Wifi";
		}else if (result==ConnectivityManager.TYPE_MOBILE){
			return "mobile";
		}
		return "others";
	}
	
	public static int checkNetworkConnectivity(final Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (mWifi.isConnected()) {
			Log.d(TAG, "wifi is connected");
		} else {
			Log.d(TAG, "wifi is not connected");
		}

		if (mMobile != null && mMobile.isConnected()) {
			Log.d(TAG, "mobile is connected");
		} else {
			Log.d(TAG, "mobile is connected");
		}

		if (mWifi.isConnected()) {
			return ConnectivityManager.TYPE_WIFI;
		} else if (mMobile != null && mMobile.isConnected()) {
			return ConnectivityManager.TYPE_MOBILE;
		} else {
			return -1;
		}
		//        if (mWifi.isAvailable() == false && mMobile.isAvailable() == false) {
		//            showDialog(DIALOG_NETWORK_UNAVAILABLE);
		//        }
	}

	public static boolean isAcceptableFlashVersion(final Context context, final String aboveVersion) {
		PackageManager pm = context.getPackageManager();
		try {
			// check flash
			ApplicationInfo flashplayer = pm.getApplicationInfo(
					"com.adobe.flashplayer", 0);
			if (flashplayer != null) {
				Log.d(TAG, "flash present...OK");
				String installFlashVersion = pm.getPackageInfo("com.adobe.flashplayer", 0).versionName;
				Log.d(TAG, "Flash version Installed: " + installFlashVersion);
				Log.d(TAG, "app require flash version : " + aboveVersion);
				if ((installFlashVersion != null) && (aboveVersion != null)) {
					String[] splitInstalledVersion = installFlashVersion.split("\\.");    
					String[] splitRequireVersion = aboveVersion.split("\\.");

					int i;
					for (i=0; i<splitRequireVersion.length; i++) {
						int tempReq = Utils.parseInt(splitRequireVersion[i]);
						int tempInstall = Utils.parseInt(splitInstalledVersion[i]);
						if (tempReq == tempInstall) {
							//                            Log.d(TAG, "== at index: " + i);
							continue;
						} else if (tempReq < tempInstall) {
							//                            Log.d(TAG, "< at index: " + i);
							return true;
						} else if (tempReq > tempInstall) {
							//                            Log.d(TAG, "> at index: " + i);
							return false;
						}
					}
					if (splitInstalledVersion[i] != null) {
						return true;
					}
				}
			}
			Log.d(TAG, "no flash appear");

		} catch (NameNotFoundException e) {
			Log.e(TAG, e.toString());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return false;
	}


	public static int parseInt(String s)
	{
		try {
			if (s != null)
				return Integer.parseInt(s);
		}
		catch(NumberFormatException e) {

		}

		return 0;
	}

	public static long parseLong(String s)
	{
		try {
			if (s != null)
				return Long.parseLong(s);
		}
		catch(NumberFormatException e) {

		}

		return 0;
	}

	/**
	 * parseBoolean
	 * @param s
	 * @return true if s = "true", "y", "yes"; false otherwise
	 */
	public static boolean parseBoolean(String s) {
		String[] TRUES = {"true", "y", "yes"};
		//String[] FALSES = {"false", "n", "no"};	not used
		
		if (s == null || "".equals(s)) return false;
		
		for (String bool:TRUES) {
			if (s.equalsIgnoreCase(bool)) return true;
		}
		
		return false;
	}

	private static Calendar getCurTimeCalendar()
	{
		// Gregorian calendar of local (Hong Kong) time zone
		Calendar n = new GregorianCalendar(TimeZone.getTimeZone("GMT+08:00"));
		return n;
	}

	/**
	 * Get start time of a day 
	 * @param day -- 0 means current day
	 *              > 0 days in future
	 *              < 0 past days
	 * @return
	 */
	public static long getDayStartInMillis(int day)
	{
		Calendar n = getDayStart(day);
		long millis = n.getTimeInMillis();
		return millis;
	}

	public static Calendar getDayStart(int day)
	{
		Calendar n = getCurTimeCalendar();
		// get today's start
		n.set(n.get(Calendar.YEAR), n.get(Calendar.MONTH), n.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		n.set(Calendar.MILLISECOND, 0); // clear this field, too
		n.add(Calendar.SECOND, day * 24 * 60 * 60);
		return n;
	}

	public static long getDayStartInMillisBySpecifiedTime(long millis)
	{
		Calendar n = getCurTimeCalendar();
		n.setTimeInMillis(millis);
		n.set(n.get(Calendar.YEAR), n.get(Calendar.MONTH), n.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		n.set(Calendar.MILLISECOND, 0); // clear this field, too
		return n.getTimeInMillis();
	}

	private static final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
		"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	
	public static String getDayMonthYear(long millis) 
	{
		String dateTime = null;
		String formatString = LanguageHelper.getLocalizedString("progsearch.datebanner.format");
		Calendar n = getCurTimeCalendar();
		n.setTimeInMillis(millis);
		if (LanguageHelper.getCurrentLanguage().equalsIgnoreCase("zh")) {
		    dateTime = String.format(formatString, n.get(Calendar.YEAR), 
		    		                               n.get(Calendar.MONTH) + 1, 
		    		                               n.get(Calendar.DAY_OF_MONTH));
		}
		else {
			dateTime = String.format(formatString, n.get(Calendar.DAY_OF_MONTH), 
					                               monthNames[n.get(Calendar.MONTH)], 
                                                   n.get(Calendar.YEAR));
		}
		return dateTime;
	}

	public static void close(Reader r)
	{
		try { if (r != null) r.close(); } catch (IOException e) { }
	}

	public static void close(InputStream s)
	{
		try { if (s != null) s.close(); } catch (IOException e) { }
	}

	public static boolean isNameStringValid(String value) {
		if (value!=null) {
			if (value.length()>2&&value.length()<11) {
				Log.d("value", ""+value.length());
				String[] split = value.split("[@$%!]");
				if (split.length>0) {
					Log.d("split", split[0]+":"+value);
					if (split[0].contentEquals(value)) {
						Log.d("split", split[0]+":"+value);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static void LongLog(String tag, String str) {
		if (str.length() > 4000) {
			Log.d(tag, "LongLog>" + str.substring(0, 4000));
			LongLog(tag, str.substring(4000));
		} else
			Log.d(tag, "LongLog>" + str);
	}
}
