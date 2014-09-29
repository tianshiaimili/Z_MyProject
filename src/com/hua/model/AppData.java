package com.hua.model;

import java.util.List;

/**
 * 存放Mypeoject解析数据的po类
 * @author zero
 *
 */
public class AppData {

	
	
	public class HomeBannerData{
		
		String homeBannerImageUrl;//
		String appName;
		String homeBannerId;
		public String getHomeBannerImageUrl() {
			return homeBannerImageUrl;
		}
		public void setHomeBannerImageUrl(String homeBannerImageUrl) {
			this.homeBannerImageUrl = homeBannerImageUrl;
		}
		public String getAppName() {
			return appName;
		}
		public void setAppName(String appName) {
			this.appName = appName;
		}
		public String getHomeBannerId() {
			return homeBannerId;
		}
		public void setHomeBannerId(String homeBannerId) {
			this.homeBannerId = homeBannerId;
		}	
		
	}
	
	/**
	 * 这是模拟首页listview item的 App的数据
	 * @author zero
	 *
	 */
	public class TempAppData{
		
		String appIco;
		String appName;
		/**
		 * 评分
		 */
		String appScore;
		/**
		 * 下载数量
		 */
		String appDownLoadNum;
		/**
		 * app大小
		 */
		String appSize;
		/**
		 * app的评论
		 */
		String appComment;
		/**
		 * 下载url
		 */
		String appDowmUrl;
		
//		List<TempAppData> tempAppDataList;
//		
//		public List<TempAppData> getTempAppDataList() {
//			return tempAppDataList;
//		}
//		public void setTempAppDataList(List<TempAppData> tempAppDataList) {
//			this.tempAppDataList = tempAppDataList;
//		}
		public String getAppIco() {
			return appIco;
		}
		public void setAppIco(String appIco) {
			this.appIco = appIco;
		}
		public String getAppName() {
			return appName;
		}
		public void setAppName(String appName) {
			this.appName = appName;
		}
		public String getAppScore() {
			return appScore;
		}
		public void setAppScore(String appScore) {
			this.appScore = appScore;
		}
		public String getAppDownLoadNum() {
			return appDownLoadNum;
		}
		public void setAppDownLoadNum(String appDownLoadNum) {
			this.appDownLoadNum = appDownLoadNum;
		}
		public String getAppSize() {
			return appSize;
		}
		public void setAppSize(String appSize) {
			this.appSize = appSize;
		}
		public String getAppComment() {
			return appComment;
		}
		public void setAppComment(String appComment) {
			this.appComment = appComment;
		}
		public String getAppDowmUrl() {
			return appDowmUrl;
		}
		public void setAppDowmUrl(String appDowmUrl) {
			this.appDowmUrl = appDowmUrl;
		}
		
		
		
	}
	
	
}
