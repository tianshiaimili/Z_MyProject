package com.hua.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Banner {
	private static final String TAG = Banner.class.getSimpleName();
	
	private static Banner instance;
	
	private ArrayList<BannerData> bannerList = new ArrayList<BannerData>();

	private boolean isBannerListCompleted = false;
	
	private Banner() {
		isBannerListCompleted = false;
	}
	
	public static void initInstance() {
		if (instance == null) {
			instance = new Banner();
		}
	}
	
	public static Banner getInstance() {
		if (instance == null){
			instance = new Banner();
		}
		return instance;
	}
	
	public boolean isBannerListCompleted() {
		return isBannerListCompleted;
	}
	
	public ArrayList<BannerData> getBannerList() {
		return bannerList;
	}

	public void setBannerList(ArrayList<BannerData> bannerList) {
		this.bannerList = bannerList;
	}
	
	public BannerData getBannerDataById(String id){
		if (bannerList != null){
			for (int i = 0; i < bannerList.size(); i++) {
				if(bannerList.get(i).getId().equalsIgnoreCase(id)){
					return bannerList.get(i);
				}
			}
		}
		return null;
	}
	
	public boolean parseBanner(String jsonResponse) {
		if (bannerList != null) {
			bannerList.clear();
		} else {
			bannerList = new ArrayList<BannerData>();
		}
		
		try {
			JSONObject jsonObj = new JSONObject(jsonResponse);
			JSONArray itemArray = jsonObj.getJSONArray("banner");
			for (int i=0; i<itemArray.length(); i++) {
				JSONObject itemObject = itemArray.getJSONObject(i);
				BannerData banner = new BannerData();
				
				banner.setId(itemObject.optString("id"));
				banner.setStartTime(itemObject.optLong("startTime"));
				banner.setEndTime(itemObject.optLong("endTime"));
				banner.setTitle1(itemObject.optString("title1"));
				banner.setTitle2(itemObject.optString("title2"));
				JSONArray itemTagsArray = itemObject.getJSONArray("tags");
				for (int j=0; j<itemTagsArray.length(); j++) {
					banner.addTagList(itemTagsArray.optString(j));
				}
				banner.setDescription1(itemObject.optString("description1"));
				banner.setImage1(itemObject.optString("image1"));
				banner.setImage2(itemObject.optString("image2"));
				banner.setLink1(itemObject.optString("link1"));
				
				bannerList.add(banner);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
		isBannerListCompleted = true;
		return true;
	}
	
	/*
	 * should be same as ArticleData
	 */
	public static class BannerData {
		private String id;
		private long startTime;
		private long endTime;
		private String title1;
		private String title2;
		private String description1;
		private ArrayList<String> tagList = new ArrayList<String>();
		private String image1;
		private String image2;
		private String link1;
		
		
		
		public BannerData() {
			super();
			// TODO Auto-generated constructor stub
		}
		public BannerData(String id, String title1) {
			super();
			this.id = id;
			this.title1 = title1;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public long getStartTime() {
			return startTime;
		}
		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
		public long getEndTime() {
			return endTime;
		}
		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}
		public String getTitle1() {
			return title1;
		}
		public void setTitle1(String title1) {
			this.title1 = title1;
		}
		public String getTitle2() {
			return title2;
		}
		public void setTitle2(String title2) {
			this.title2 = title2;
		}
		public String getDescription1() {
			return description1;
		}
		public void setDescription1(String description1) {
			this.description1 = description1;
		}
		public ArrayList<String> getTagList() {
			return tagList;
		}
		public synchronized void addTagList(String tagList) {
			if (this.tagList == null) {
				this.tagList = new ArrayList<String>();
			}
			this.tagList.add(tagList);
		}
		public void setTagList(ArrayList<String> tagList) {
			this.tagList = tagList;
		}
		public String getImage1() {
			return image1;
		}
		public void setImage1(String image1) {
			this.image1 = image1;
		}
		public String getImage2() {
			return image2;
		}
		public void setImage2(String image2) {
			this.image2 = image2;
		}
		public String getLink1() {
			return link1;
		}
		public void setLink1(String link1) {
			this.link1 = link1;
		}
		public void setLink1_clean(String link1) {
			if ((link1.startsWith("url")) || (link1.startsWith("pid"))){
				this.link1 = link1.substring(4);
			}else if (link1.startsWith("ch")) {
				this.link1 = link1.substring(3);
			}else {
				this.link1 = link1;
			}
		}
		public String getLinkOfType(String type){
			if(link1.startsWith(type)){
				return link1.substring(type.length()+1);
			}
			return null;
		}
		
		public Map<String, String> getTypeAndIdByLink(){
			Map<String, String> map = new HashMap();
			String id;
			if(link1.startsWith("url")){
				id = link1.substring(4);
				map.put("url", id);
			}
			if(link1.startsWith("pid")){
				id = link1.substring(4);
				map.put("product", id);
			}
			if(link1.startsWith("sid")){
				id = link1.substring(4);
				map.put("series", "autoGenNode_"+id);
			}else {
				map.put("product", link1);
			}
			return map;
		}
		
	}
	
	
	public ArrayList<BannerData> getBannerListWithTag(String ...requestTagList){
		ArrayList<BannerData> returnList = new ArrayList<BannerData>();
		if (requestTagList == null){
			return returnList;
		}
		for (BannerData data : bannerList ){
			if (data.getTagList()!=null ){
				for (String tag : requestTagList){
					if (data.getTagList().contains(tag)){
						returnList.add(data);
						break;
					}
				}
			}
		}
		return returnList;
	}
	
}

