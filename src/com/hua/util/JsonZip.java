package com.hua.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;

/**
 * Class for handling JSON Zip files. Support normal app-wide JSON Zip and EPG JSON Zip.
 * 
 */
public class JsonZip {
	
	private final static String TAG = JsonZip.class.getSimpleName();
	private final static long updateTimeInterval = 4 * 60 * 60 * 1000;  // 4 hours in milliseconds
	//public static final String JSON_VERSION_URL_SUFFIX = "/public/mobile/%1$s";

	private String mVersionUrlPrefix = "/public/mobile/%1$s";
	private Context mContext = null;
	private String mJsonVersionUrl;
	private DownloadZip mDownloadZip;
	private boolean mIsDownloading = false;
	private String mLanguage;

	public enum ZipType {
		/**
		 * Specify an app-wide JSON Zip.
		 */
		PKG("json_version.txt"),
		/**
		 *  Specify a JSON Zip for EPG data.
		 */
		EPG("epg_version.txt");

		/**
		 * Version filename of the JSON Zip type 
		 */
		private final String versionFileName;

		ZipType(String versionFileName) {
			this.versionFileName = versionFileName;
		}
	}

	/**
	 * Creates a new JsonZip instance
	 * @param context the app context
	 * @param jsonVersionUrl jsonversion URL, usually available in AppInfo's jsonVersionPath field.
	 * @param versionUrlPrefix version URL Prefix. It should be a format string that can be used to 
	 * insert version file name into the format string, for example '/%1$s'.
	 * @param language either "en" for English or "zh" for Chinese.
	 */
	public JsonZip(Context context, String jsonVersionUrl, String versionUrlPrefix, String language) {
		mContext = context;
		mJsonVersionUrl = jsonVersionUrl;
		if (versionUrlPrefix != null) {
			mVersionUrlPrefix = versionUrlPrefix;
		}
		if (language != null && ("en".equals(language) || "zh".equals(language))) {
			mLanguage = language;
		} else {
			throw new IllegalArgumentException("lanuage must be either \"en\" or \"zh\""); 
		}
	}

	/**
	 * Interface for callback of JSON Zip download events.
	 */
	public interface Callback {
		/**
		 * Callback on download progress.
		 * @param precent Percentage of download completed.
		 */
		void updateProgress(int precent);
		/**
		 * Callback on download completed.
		 * @param zipType Type of JSON Zip.
		 * @param isOK If the download finished succesfully.
		 */
		void onDownloadCompleted(ZipType zipType, boolean isOK);
	}

	/**
	 * Get JSON ZIP URL
	 * @return jsonversionURL from appinfo
	 */
	private String getJsonZipUrl(ZipType zipType) {
		String url = mJsonVersionUrl;
		return url;
	}

	/**
	 * Check if JSON Zip update required.
	 * @param zipType Type of JSON Zip.
	 */
	public boolean shouldUpdateJSONZipVersion(ZipType zipType) {
		File jsonZipVersion = new File(mContext.getCacheDir(), zipType.versionFileName);
		Time currentTime = new Time();
		currentTime.setToNow();
		
		// no need to update if both version file / version ZIP exist and the version file date is not expired
		if (jsonZipVersion.exists() &&
			Math.abs(jsonZipVersion.lastModified() - currentTime.toMillis(false)) < updateTimeInterval) {
			File jsonZip = new File(mContext.getCacheDir(), getJsonZipFilename(zipType));
			
			if (jsonZip.exists()) {
				return false;
			} else {
				return true;
			}
		} else {
			return true;
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
			URL url = new URL(getJsonZipUrl(zipType));
			URLConnection connection = url.openConnection();
			
            connection.connect();
            
            InputStream input = new BufferedInputStream(url.openStream());
            BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			StringBuilder newContent = new StringBuilder();
            String line;
		    while ((line = buffer.readLine()) != null) {
		    	newContent.append(line);
		    }
            
		    String oldContent = getCurrentJSONZipPath(zipType);
            if (oldContent != null && oldContent.equals(newContent)) {
            	// update file date only
            	File versionFile = new File(mContext.getCacheDir(), zipType.versionFileName);
            	Time time = new Time();
            	time.setToNow();
            	versionFile.setLastModified(time.toMillis(false));
            } else {
            	OutputStream output = new FileOutputStream(new File(mContext.getCacheDir(), zipType.versionFileName));
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
	 * Read the current JSON Zip download Path
	 * @param zipType Type of JSON Zip.
	 * @return relative path of JSON Zip, null if not exist
	 */
	private String getCurrentJSONZipPath(ZipType zipType) {
		File jsonZipVersion = new File(mContext.getCacheDir(), zipType.versionFileName);
		
		if (!jsonZipVersion.exists()) {
			Log.v(TAG, "version file not exist!");
			return null;
		}
		
		//Read text from file
		StringBuilder url = new StringBuilder();

		try {
		    BufferedReader buffer = new BufferedReader(new FileReader(jsonZipVersion));
		    String line;

		    while ((line = buffer.readLine()) != null) {
		        url.append(line);
		    }
		    buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		// Modify URL according to current selected language
		int index = url.lastIndexOf(".zip");
		url = url.replace(index, index + 4, (mLanguage.toUpperCase()) + ".zip");
		
		return url.toString();
	}
	
	private String getJsonZipFilename(ZipType zipType) {
		String filename = getCurrentJSONZipPath(zipType);
		filename = filename.substring(filename.lastIndexOf("/") + 1, filename.length());
		Log.d(TAG, "getJsonZipFilename: " + filename);
		return filename;
	}
	
	/**
	 * Start download JSON zip
	 * @param zipType Type of JSON Zip.
	 * @param callback Callback to update progress and action after download completed.
	 */
	public void startDownload(ZipType zipType, JsonZip.Callback callback) {
		if (!mIsDownloading) {
			mIsDownloading = true;
			mDownloadZip = new DownloadZip(zipType, callback);
			if (android.os.Build.VERSION.SDK_INT < 11){//before honeycomb
				mDownloadZip.execute();
			} else{
				mDownloadZip.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	}
	
	public void cancelDownload(){
		mDownloadZip.cancel(true);
	}
	
	/**
	 * Clear both JSON version file and JSON ZIP
	 */
	public void clearJSONZip(ZipType zipType) {
		File jsonZipVersion = new File(mContext.getCacheDir(), zipType.versionFileName);
		
		if (getCurrentJSONZipPath(zipType) != null) {
			File jsonZip = new File(mContext.getCacheDir(), getJsonZipFilename(zipType));
			jsonZip.delete();
		}
		jsonZipVersion.delete();
	}
	
	/**
	 * Delete catched data e.g. JSON Zip
	 */
	public void clearAppCache(Context context) {
		try {
	        File dir = context.getCacheDir();
	        if (dir != null && dir.isDirectory()) {
	            deleteDir(dir);
	        }
	    } catch (Exception e) {}
	}
	
	private static boolean deleteDir(File dir) {
	    if (dir != null && dir.isDirectory()) {
	        String[] children = dir.list();
	        for (int i = 0; i < children.length; i++) {
	            boolean success = deleteDir(new File(dir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return dir.delete();
	}
	
	
	/**
	 * Check if downloading zip
	 * @return true if a download is in progress, false otherwise.
	 */
	public boolean isDownloading() {
		return mIsDownloading;
	}
	
	private class DownloadZip extends AsyncTask<String, Integer, String> {
		private boolean mCompleted = false;
		private File mOldZip;
		private File mNewZip;
		private ZipType mZipType;
		
		private JsonZip.Callback mCallback;
		
		public DownloadZip(ZipType zipType, JsonZip.Callback callback) {
			mZipType = zipType;
			mCallback = callback;
		}
		
		@Override
		protected String doInBackground(String... urls) {
			// retrieving old URL file
			if (getCurrentJSONZipPath(mZipType) != null) {
				mOldZip = new File(mContext.getCacheDir(), getJsonZipFilename(mZipType));
			} else {
				mOldZip = null;
			}
			
			// update version file if needed
			if (shouldUpdateJSONZipVersion(mZipType)) {
				if (!downloadJSONZipVersion(mZipType)) {
					Log.d(TAG, "something wrong while downloading version file"); 
					return null;
				}
			}
			
			// expecting an updated URL
			String zipURL = getCurrentJSONZipPath(mZipType);

			if (zipURL == null) return null;
			
			// start download getJSONZipURL(),
			try {
				URL url = new URL(zipURL);
				Log.d(TAG, "ZIP URL " + url.toString());

				mNewZip = new File(mContext.getCacheDir(), getJsonZipFilename(mZipType));
				
				if (mOldZip != null && mOldZip.exists() && mNewZip.equals(mOldZip)) {
					// same file exist, no need to download
Log.d(TAG, "same file exist, no need to download");
					mCompleted = true;
					mNewZip = null;
					return null;
				}

				URLConnection connection = url.openConnection();
				connection.connect();
				// this will be useful so that you can show a typical 0-100% progress bar
				int fileLength = connection.getContentLength();

				// download the file
				InputStream input = new BufferedInputStream(url.openStream());
Log.d(TAG, "mNewZip : " + mNewZip.getAbsolutePath());
				OutputStream output = new FileOutputStream(mNewZip);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1) {
					total += count;
					// publishing the progress....
					publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				mCompleted = true;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// if success remove previous zip 
			if (mCompleted) {
				if (mOldZip != null && mNewZip != null && !mOldZip.equals(mNewZip)) {
					// remove old zip in cache
					mOldZip.delete();
					mOldZip = null;
				}
			}
			mCallback.onDownloadCompleted(mZipType, mCompleted);
			mIsDownloading = false;
			super.onPostExecute(result);
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// call progress update callback
			mCallback.updateProgress(values[0]);
			super.onProgressUpdate(values);
		}

		@SuppressLint("NewApi")
		@Override
		protected void onCancelled(String result) {
			mIsDownloading = false;
			super.onCancelled(result);
		}
	}
	
	/**
	 * Get the JSON data in string from JSON zip
	 * @param relativePath the relative file and filename to be retrieved inside the Zip file.
	 * @return JSON Zip file data in String
	 */
	public synchronized String getJSONData(ZipType zipType, String relativePath) {
		String outputString = "";
		
		try {
			FileInputStream fin = new FileInputStream(new File(mContext.getCacheDir(), getJsonZipFilename(zipType)));
			ZipInputStream zin = new ZipInputStream(fin);
			ZipEntry ze = null;
			
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.getName().equals(relativePath)) {
					break;
				}
			}
			
			if (ze != null) {
				final int BUFFER_SIZE = 1024;
				byte buffer[] = new byte[BUFFER_SIZE];
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				int count = 0;
				
				while ((count = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
					bout.write(buffer, 0, count);
				}
				
				outputString = bout.toString();
				bout.close();
			} else {
				Log.d(TAG, "json not exist in zip: " + relativePath);
			}
			
			
			zin.closeEntry();
			zin.close();
			fin.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return outputString;
	}
}
