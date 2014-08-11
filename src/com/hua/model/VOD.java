package com.hua.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hua.util.Utils;

public class VOD {
	private static final String TAG = VOD.class.getSimpleName(); 
	
	private static VOD instance;
	
	private String vodRootNodeId = "1";
	private LinkedHashMap<String, VODCategoryNodeData> vodCategoryNodeList = new LinkedHashMap<String, VODCategoryNodeData>();
	private LinkedHashMap<String, VODData> vodList = new LinkedHashMap<String, VODData>();
	
	private boolean isRootCategoryListCompleted = false;
	private boolean isVODDataListCompleted = false;
	
	
	public static void initInstance() {
		if (instance == null) {
			instance = new VOD();
		}
	}
	
	public static VOD getInstance() {
		if (instance == null) {
			instance = new VOD();
		}
		return instance;
	}
	
	public boolean isRootCategoryListCompleted() {
		return isRootCategoryListCompleted;
	}
	public boolean isVODDataListCompleted() {
		return isVODDataListCompleted;
	}
	
	public LinkedHashMap<String, VODData> getVodlist (){
		return vodList;
	}
	
	/**
	 * call at begining
	 */
	public String getVODRootNodeID() {
		return vodRootNodeId;
	}
	
	
	//getRootNode
	public VODCategoryNodeData getVODRootNode(){
		if (vodRootNodeId!=null){
			return getVODCategoryByNodeId(vodRootNodeId);
		} else {
			return null;
		}
	}
	
	private void setVodRootNodeID(String nodeid){
		vodRootNodeId = nodeid;
	}
	
	public void clearVodChannelList() {
		if (vodCategoryNodeList != null)
			vodCategoryNodeList.clear(); 
		if (vodList != null)
			vodList.clear(); 
		isRootCategoryListCompleted = false;
		isVODDataListCompleted = false;
	}
	
	/**
	 * get products arrary in vodDetail.json
	 */
	public LinkedHashMap<String, VODData> getVODDataByNodeId(String nodeid) {
		if (vodList != null) {
			LinkedHashMap<String, VODData> returnvodList = new LinkedHashMap<String, VODData>();
			for (Entry<String, VODData> entry : vodList.entrySet()) {
			   if (entry.getValue().getNodeId().equalsIgnoreCase(nodeid)){
				   returnvodList.put(entry.getKey(), entry.getValue());
			   }	
			}
			return returnvodList;
		}
		return null;
	}
	
	/**
	 * get product with episode id in vodDetail.json
	 * please make sure episode id is unique before use, otherwise it will only return the first one
	 */
	public VODData getVODDataByEpisodeId(String eid) {
		if (vodList != null) {
			for (Entry<String, VODData> entry : vodList.entrySet()) {
			   if (entry.getValue().getEpisodeId().equalsIgnoreCase(eid)) {
				   return entry.getValue();
			   }	
			}
		}
		return null;
	}
	
	
	public LinkedHashMap<String, VODData> sortVODDataListByAirDate (LinkedHashMap<String, VODData> listWantSort, boolean isDesc){
		try{
			LinkedHashMap<String, VODData> returnList = new LinkedHashMap<String, VODData>();
			List<String> sortKeys =  new ArrayList <String>(listWantSort.keySet());
			List<VODData> sortValues =  new ArrayList <VODData>(listWantSort.values());
			for (int i=1; i<sortValues.size(); i++){
				for (int j=0; j<sortValues.size()-i; j++){
					if (sortValues.get(j).getOnAirDate().compareTo(sortValues.get(j+1).getOnAirDate()) < 0){
						Collections.swap(sortKeys, j, j+1);
						Collections.swap(sortValues, j, j+1);
					}
				}
			}
			for (int i = 0; i < sortKeys.size(); i++) {
				if (isDesc) returnList.put(sortKeys.get(i) , sortValues.get(i));
				else returnList.put(sortKeys.get(sortKeys.size()-i) , sortValues.get(sortKeys.size()-i));
			}
			
			return returnList;
		}catch (Exception e){
			System.out.println(e);
			return null;
		}
	}
	
	
	/**
	 * get products arrary in vodCatalog.json
	 */
	public VODCategoryNodeData getVODCategoryByNodeId (String nodeId) {
		if (vodCategoryNodeList != null) {
			VODCategoryNodeData ansObj = null;
			ansObj = getChildVODCategoryByNodeId(nodeId, ansObj, vodCategoryNodeList);
			return ansObj;
		}
		return null;
	}
	
	private VODCategoryNodeData getChildVODCategoryByNodeId(String nodeId, VODCategoryNodeData ansObj ,LinkedHashMap<String, VODCategoryNodeData> chindlist){
		for (String key : chindlist.keySet()) {
			if (key.equalsIgnoreCase(nodeId)){
				ansObj = chindlist.get(key);
			}else if (chindlist.get(key).childNodes.size()>0){
				ansObj = getChildVODCategoryByNodeId(nodeId, ansObj, chindlist.get(key).childNodes);
			}
		}
		return ansObj;
	}
	
	/**
	 * get VODCategoryNodeData by series/category id in vodCatalog.json
	 */
	public VODCategoryNodeData getVODCategoryById (String id) {
		if (vodCategoryNodeList != null) {
			VODCategoryNodeData ansObj = null;
			ansObj = getChildVODCategoryById(id, ansObj, vodCategoryNodeList);
			return ansObj;
		}
		return null;
	}
	
	private VODCategoryNodeData getChildVODCategoryById(String id, VODCategoryNodeData ansObj ,LinkedHashMap<String, VODCategoryNodeData> childlist){
		for (String key : childlist.keySet()) {
			VODCategoryNodeData child = childlist.get(key);
			if (child.getId().equalsIgnoreCase(id)){
				ansObj = childlist.get(key);
			}else if (child.childNodes.size()>0){
				ansObj = getChildVODCategoryById(id, ansObj, child.childNodes);
			}
		}
		return ansObj;
	}
	
	/**
	 *  parse vodCatalog.json
	 */
	public boolean parseVODCatergories(String jsonResponse) {
		if (vodCategoryNodeList != null) {
			vodCategoryNodeList.clear();	// TODO: override the clear function for recursive clear; 
		} else {
			vodCategoryNodeList =  new LinkedHashMap<String, VODCategoryNodeData>(); 
		}
		
		try {
			JSONObject jsonObject = new JSONObject(jsonResponse);
			JSONArray rootNodeArray = jsonObject.getJSONArray("vodCatalog");
			setVodRootNodeID(rootNodeArray.getJSONObject(0).optString("nodeId"));
			
			if (jsonObject != null && rootNodeArray.length() > 0) {
				vodCategoryNodeList = parseVODCatergory(rootNodeArray, null);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		isRootCategoryListCompleted = true;
		return true;
	}
	
	private LinkedHashMap<String, VODCategoryNodeData> parseVODCatergory(JSONArray jsonArray, JSONObject parentData) throws JSONException {
		LinkedHashMap<String, VODCategoryNodeData> vodChildNodeList = new LinkedHashMap<String, VODCategoryNodeData>();
		
		for (int i=0; i<jsonArray.length(); i++) {
			JSONObject itemObject = jsonArray.getJSONObject(i);
			VODCategoryNodeData vodCategoryNode = new VODCategoryNodeData();
			
			vodCategoryNode.setNodeId(itemObject.optString("nodeId"));
			vodCategoryNode.setType(itemObject.optString("type"));
			
			JSONObject itemDataObject = itemObject.getJSONObject("data");
			vodCategoryNode.setId(itemDataObject.optString("id"));
			vodCategoryNode.setName(itemDataObject.optString("name"));
			vodCategoryNode.setLibId(itemDataObject.optString("libId"));
			vodCategoryNode.setRolloverMessage(itemDataObject.optString("rolloverMessage"));
			vodCategoryNode.setDisclaimer(itemDataObject.optString("disclaimer"));
			vodCategoryNode.setLock(Utils.parseBoolean(itemDataObject.optString("isLock")));
			vodCategoryNode.setSubscriptionCheck(Utils.parseBoolean(itemDataObject.optString("subscriptionCheck")));
			vodCategoryNode.setAdultContent(Utils.parseBoolean(itemDataObject.optString("isAdultContent")));
			vodCategoryNode.setSdImg1Path(itemDataObject.optString("sdImg1Path"));
			vodCategoryNode.setSdImg2Path(itemDataObject.optString("sdImg2Path"));
			vodCategoryNode.setSdImg3Path(itemDataObject.optString("sdImg3Path"));
			vodCategoryNode.setSdImg4Path(itemDataObject.optString("sdImg4Path"));
			vodCategoryNode.setHdImg1Path(itemDataObject.optString("hdImg1Path"));
			vodCategoryNode.setHdImg2Path(itemDataObject.optString("hdImg2Path"));
			vodCategoryNode.setHdImg3Path(itemDataObject.optString("hdImg3Path"));
			
			if ((parentData != null) && ((!parentData.optString("inactiveTabImagePath").equals("")) || (!parentData.optString("activeTabImagePath").equals(""))) &&
					((itemDataObject.optString("inactiveTabImagePath").equals("")) || (itemDataObject.optString("activeTabImagePath").equals("")))) {
				vodCategoryNode.setInactiveTabImagePath(parentData.optString("inactiveTabImagePath"));
				vodCategoryNode.setActiveTabImagePath(parentData.optString("activeTabImagePath"));
				vodCategoryNode.setCategoryImagePath(parentData.optString("categoryImagePath"));
			}else{
				vodCategoryNode.setInactiveTabImagePath(itemDataObject.optString("inactiveTabImagePath"));
				vodCategoryNode.setActiveTabImagePath(itemDataObject.optString("activeTabImagePath"));
				vodCategoryNode.setCategoryImagePath(itemDataObject.optString("categoryImagePath"));
			}
			
			vodCategoryNode.setSeriesEncodingSD(Utils.parseBoolean(itemDataObject.optString("seriesEncodingSD")));
			vodCategoryNode.setActors(itemDataObject.optString("actors"));
			vodCategoryNode.setLanguages(itemDataObject.optString("languages"));
			vodCategoryNode.setSeriesEncodingSuperHD(Utils.parseBoolean(itemDataObject.optString("seriesEncodingSuperHD")));
			vodCategoryNode.setSeriesEncodingHD(Utils.parseBoolean(itemDataObject.optString("seriesEncodingHD")));
			vodCategoryNode.setScheduleId(itemDataObject.optString("scheduleId"));
			vodCategoryNode.setSynopsis(itemDataObject.optString("synopsis"));
			vodCategoryNode.setEnableNextEpisode(Utils.parseBoolean(itemDataObject.optString("enableNextEpisode")));
			vodCategoryNode.setPaymentType(itemDataObject.optString("paymentType"));
			vodCategoryNode.setSeasonNumber(itemDataObject.optString("seasonNumber"));
			vodCategoryNode.setBreakdownSeriesName(itemDataObject.optString("breakdownSeriesName"));
			vodCategoryNode.setDescription(itemDataObject.optString("description"));
			
			JSONArray tagArray = itemDataObject.optJSONArray("tags");
			if (tagArray!=null && tagArray.length()>0){
				for (int counter=0; counter<tagArray.length();counter++){
					vodCategoryNode.addTag(tagArray.optString(counter));
				}
			}
			JSONArray itemChildNodeArray = itemObject.optJSONArray("childNodes");
			if (itemChildNodeArray != null && itemChildNodeArray.length() > 0) {
				vodCategoryNode.childNodes = parseVODCatergory(itemChildNodeArray, itemDataObject);
			}
			
			vodChildNodeList.put(vodCategoryNode.getNodeId(), vodCategoryNode);
		}
		
		return vodChildNodeList;
	}
	
	
	/**
	 *  parse vodDetail.json
	 */
	public boolean parseVODDetails(String jsonResponse) {
		if (vodList != null) {
			vodList.clear();	// TODO: override the clear function for recursive clear; 
		} else {
			vodList = new LinkedHashMap<String, VODData>(); 
		}

		try {
			JSONObject replyObject = new JSONObject(jsonResponse);
			JSONArray vodDetailArray = replyObject.getJSONArray("vodDetail");

			if (replyObject != null && vodDetailArray.length() > 0) {
				// start parse vod detail
				for (int i=0; i<vodDetailArray.length(); i++) {
					JSONObject vodDetail = vodDetailArray.getJSONObject(i);
					String vodNodeid = vodDetail.getString("nodeId");
								
					JSONArray productArray = vodDetail.getJSONArray("products");
					for (int j=0; j<productArray.length(); j++) {
						JSONObject vodProduct = productArray.getJSONObject(j);
						VODData productNode = new VODData();

						productNode.setNodeId(vodNodeid);
						productNode.setFlvAssetStatus(Utils.parseBoolean(vodProduct.optString("flvAssetStatus")));
						productNode.setViewingPeriod(vodProduct.optInt("viewingPeriod"));
						productNode.setOffAirDate(vodProduct.optString("offAirDate"));
						productNode.setDisclaimer(vodProduct.optString("disclaimer"));
						productNode.setHlsAssetId(vodProduct.optString("hlsAssetId"));
						productNode.setProductEncodingHD(Utils.parseBoolean(vodProduct.optString("productEncodingHD")));
						productNode.setHasChapter(Utils.parseBoolean(vodProduct.optString("hasChapter")));
						productNode.setCpId(vodProduct.optString("cpId"));
						productNode.setLock(Utils.parseBoolean(vodProduct.optString("isLock")));
						productNode.setFlvAssetId(vodProduct.optString("flvAssetId"));
						productNode.setSubscriptionCheck(Utils.parseBoolean(vodProduct.optString("subscriptionCheck")));
						productNode.setEpisodeId(vodProduct.optString("episodeId"));
						productNode.setAdultContent(Utils.parseBoolean(vodProduct.optString("isAdultContent")));
						productNode.setDisplayWhenAssetReady(Utils.parseBoolean(vodProduct.optString("displayWhenAssetReady")));
						productNode.setActor(vodProduct.optString("actor"));
						productNode.setWebAssetStatus(Utils.parseBoolean(vodProduct.optString("webAssetStatus")));
						productNode.setNpvrId(vodProduct.optString("npvrId"));
						productNode.setLibId(vodProduct.optString("libId"));
						productNode.setWebImg2Path(vodProduct.optString("webImg2Path"));
						productNode.setHlsAssetStatus(Utils.parseBoolean(vodProduct.optString("hlsAssetStatus")));
						productNode.setProductEncodingSuperHD(Utils.parseBoolean(vodProduct.optString("productEncodingSuperHD")));
						productNode.setRestricted(Utils.parseBoolean(vodProduct.optString("isRestricted")));
						productNode.setClassification(vodProduct.optString("classification"));
						productNode.setPaymentType(vodProduct.optString("paymentType"));
						productNode.setEpisodeName(vodProduct.optString("episodeName"));
						productNode.setProductEncodingSD(Utils.parseBoolean(vodProduct.optString("productEncodingSD")));
						productNode.setWebImg1Path(vodProduct.optString("webImg1Path"));
						productNode.setWebActors(vodProduct.optString("webActors"));
						productNode.setRestrictedToApp(Utils.parseBoolean(vodProduct.optString("isRestrictedToApp")));
						productNode.setScheduleId(vodProduct.optString("scheduleId"));
						productNode.setLanguages(vodProduct.optString("languages"));
						productNode.setEpisodeTitle(vodProduct.optString("episodeTitle"));
						productNode.setDuration(vodProduct.optInt("duration"));
						productNode.setWebAssetId(vodProduct.optString("webAssetId"));
						productNode.setWebDirectors(vodProduct.optString("webDirectors"));
						productNode.setWebSynopsis(vodProduct.optString("webSynopsis"));
						productNode.setPayPerView(Utils.parseBoolean(vodProduct.optString("isPayPerView")));
						productNode.setWebImg1PathWidth(vodProduct.optInt("webImg1PathWidth"));
						productNode.setWebImg1PathHeight(vodProduct.optInt("webImg1PathHeight"));
						productNode.setWebImg2PathWidth(vodProduct.optInt("webImg2PathWidth"));
						productNode.setWebImg2PathHeight(vodProduct.optInt("webImg2PathHeight"));
						productNode.setOnAirDate(vodProduct.optString("onAirDate"));
						productNode.setHdImg1Path(vodProduct.optString("hdImg1Path"));
						productNode.setBreakdownProductName(vodProduct.optString("breakdownProductName"));
						productNode.setDescription(vodProduct.optString("description"));
						productNode.setHdImg2Path(vodProduct.optString("hdImg2Path"));
						productNode.setEpisodeNumber(vodProduct.optString("episodeNumber"));
						productNode.setTrailerId(vodProduct.optString("trailerId"));
						productNode.setDownloadable(Utils.parseBoolean(vodProduct.optString("downloadable")));
						productNode.setProductDate(vodProduct.optString("productDate"));
						productNode.setExternalRatingId1(vodProduct.optString("externalRatingId1"));
						productNode.setLibImg1Path(vodProduct.optString("libraryImg1Path"));
						productNode.setOffAir(Utils.parseBoolean(vodProduct.optString("isOffair")));
						
						
						JSONArray trailersArray = vodProduct.optJSONArray("trailers");
						if (trailersArray != null && trailersArray.length() > 0) {
							for (int k=0; k<trailersArray.length(); k++) {
								JSONObject vodTrailer = trailersArray.getJSONObject(k);
								VODTrailerData trailerNode = new VODTrailerData();

								trailerNode.setTrailerId(vodTrailer.optString("trailerId"));
								trailerNode.setDisplayName(vodTrailer.optString("displayName"));
								trailerNode.setType(vodTrailer.optString("type"));

								productNode.addTrailer(trailerNode);
							}
						}
						vodList.put(vodNodeid+productNode.getEpisodeId() , productNode);
					}
				}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		isVODDataListCompleted = true;
		return true;
	}
	
	/**
	 *  VOD category node (tree structure)
	 *
	 */
	public class VODCategoryNodeData implements Comparable<VODCategoryNodeData>{
		private String nodeId;
		private String type;
		
		private String id;
		private String name;
		private String inactiveTabImagePath;
		private String activeTabImagePath;
		private String categoryImagePath;
		private String libId;
		private String rolloverMessage;
		private String disclaimer;
		private boolean isLock;
		private boolean subscriptionCheck;
		private boolean isAdultContent;
		private String sdImg1Path;
		private String sdImg2Path;
		private String sdImg3Path;
		private String sdImg4Path;
		private String hdImg1Path;
		private String hdImg2Path;
		private String hdImg3Path;
		private boolean seriesEncodingSD;
		private String actors;
		private String languages;
		private boolean seriesEncodingSuperHD;
		private boolean seriesEncodingHD;
		private String scheduleId;
		private String synopsis;
		private boolean enableNextEpisode;
		private String paymentType;
		private String breakdownSeriesName;
		private String seasonNumber;
		private String description;
		
		private List<String> tags;
		public LinkedHashMap<String, VODCategoryNodeData> childNodes = new LinkedHashMap<String, VODCategoryNodeData>();

		public String getNodeId() {
			return nodeId;
		}

		public void setNodeId(String nodeId) {
			this.nodeId = nodeId;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getInactiveTabImagePath() {
			return inactiveTabImagePath;
		}

		public void setInactiveTabImagePath(String inactiveTabImagePath) {
			this.inactiveTabImagePath = inactiveTabImagePath;
		}

		public String getActiveTabImagePath() {
			return activeTabImagePath;
		}

		public void setActiveTabImagePath(String activeTabImagePath) {
			this.activeTabImagePath = activeTabImagePath;
		}

		public String getCategoryImagePath() {
			return categoryImagePath;
		}

		public void setCategoryImagePath(String categoryImagePath) {
			this.categoryImagePath = categoryImagePath;
		}

		public String getLibId() {
			return libId;
		}

		public void setLibId(String libId) {
			this.libId = libId;
		}

		public String getRolloverMessage() {
			return rolloverMessage;
		}

		public void setRolloverMessage(String rolloverMessage) {
			this.rolloverMessage = rolloverMessage;
		}

		public String getDisclaimer() {
			return disclaimer;
		}

		public void setDisclaimer(String disclaimer) {
			this.disclaimer = disclaimer;
		}

		public boolean isLock() {
			return isLock;
		}

		public void setLock(boolean isLock) {
			this.isLock = isLock;
		}

		public boolean isSubscriptionCheck() {
			return subscriptionCheck;
		}

		public void setSubscriptionCheck(boolean subscriptionCheck) {
			this.subscriptionCheck = subscriptionCheck;
		}

		public boolean isAdultContent() {
			return isAdultContent;
		}

		public void setAdultContent(boolean isAdultContent) {
			this.isAdultContent = isAdultContent;
		}

		public String getSdImg1Path() {
			return sdImg1Path;
		}

		public void setSdImg1Path(String sdImg1Path) {
			this.sdImg1Path = sdImg1Path;
		}

		public String getSdImg2Path() {
			return sdImg2Path;
		}

		public void setSdImg2Path(String sdImg2Path) {
			this.sdImg2Path = sdImg2Path;
		}

		public String getSdImg3Path() {
			return sdImg3Path;
		}

		public void setSdImg3Path(String sdImg3Path) {
			this.sdImg3Path = sdImg3Path;
		}

		public String getSdImg4Path() {
			return sdImg4Path;
		}

		public void setSdImg4Path(String sdImg4Path) {
			this.sdImg4Path = sdImg4Path;
		}

		public String getHdImg1Path() {
			return hdImg1Path;
		}

		public void setHdImg1Path(String hdImg1Path) {
			this.hdImg1Path = hdImg1Path;
		}

		public String getHdImg2Path() {
			return hdImg2Path;
		}

		public void setHdImg2Path(String hdImg2Path) {
			this.hdImg2Path = hdImg2Path;
		}

		public String getHdImg3Path() {
			return hdImg3Path;
		}

		public void setHdImg3Path(String hdImg3Path) {
			this.hdImg3Path = hdImg3Path;
		}

		public boolean isSeriesEncodingSD() {
			return seriesEncodingSD;
		}

		public void setSeriesEncodingSD(boolean seriesEncodingSD) {
			this.seriesEncodingSD = seriesEncodingSD;
		}

		public String getActors() {
			return actors;
		}

		public void setActors(String actors) {
			this.actors = actors;
		}

		public String getLanguages() {
			return languages;
		}

		public void setLanguages(String languages) {
			this.languages = languages;
		}

		public boolean isSeriesEncodingSuperHD() {
			return seriesEncodingSuperHD;
		}

		public void setSeriesEncodingSuperHD(boolean seriesEncodingSuperHD) {
			this.seriesEncodingSuperHD = seriesEncodingSuperHD;
		}

		public boolean isSeriesEncodingHD() {
			return seriesEncodingHD;
		}

		public void setSeriesEncodingHD(boolean seriesEncodingHD) {
			this.seriesEncodingHD = seriesEncodingHD;
		}

		public String getScheduleId() {
			return scheduleId;
		}

		public void setScheduleId(String scheduleId) {
			this.scheduleId = scheduleId;
		}

		public String getSynopsis() {
			return synopsis;
		}

		public void setSynopsis(String synopsis) {
			this.synopsis = synopsis;
		}

		public boolean isEnableNextEpisode() {
			return enableNextEpisode;
		}

		public void setEnableNextEpisode(boolean enableNextEpisode) {
			this.enableNextEpisode = enableNextEpisode;
		}

		public String getPaymentType() {
			return paymentType;
		}

		public void setPaymentType(String paymentType) {
			this.paymentType = paymentType;
		}
		
		
		
		public String getBreakdownSeriesName() {
			return breakdownSeriesName;
		}

		public void setBreakdownSeriesName(String breakdownSeriesName) {
			this.breakdownSeriesName = breakdownSeriesName;
		}

		public String getSeasonNumber() {
			return seasonNumber;
		}

		public void setSeasonNumber(String seasonNumber) {
			this.seasonNumber = seasonNumber;
		}
		
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public List<String> getTags(){
			return tags;
		}
		
		public synchronized void addTag(String tag) {
			if (tags == null) {
				tags = new ArrayList<String>();
			}
			tags.add(tag);
		}
		
		public List<VODCategoryNodeData> getChildNodeWithTag(String ...requestTagList){
			ArrayList<VODCategoryNodeData> returnList = new ArrayList<VODCategoryNodeData>();  
			if (requestTagList == null){
				return returnList;
			}
			
			if (childNodes!=null){
				for (VODCategoryNodeData catalogNode : childNodes.values()){
					if(catalogNode.tags != null && catalogNode.tags.size()>0){
						for (String tag : requestTagList){
							if (catalogNode.tags.contains(tag)){
								returnList.add(catalogNode);
								break;
							}
						}
					}
				}
			}
			return returnList;
		}
		
		
		public List<VODCategoryNodeData> getAllSeriesNode(){
			ArrayList<VODCategoryNodeData> returnList = new ArrayList<VODCategoryNodeData>();  

			if (childNodes == null || childNodes.size() == 0){
				return returnList;
			}
			
			for (VOD.VODCategoryNodeData child : childNodes.values()){
				if (!"series".equalsIgnoreCase(child.getType())){
					returnList.addAll(child.getAllSeriesNode());
				} else{
					returnList.add(child);
				}
			}
			return returnList;
		}
		
		public List<VODCategoryNodeData> getAllSeriesNodeWithChildTag(String ...requestTagList){
			ArrayList<VODCategoryNodeData> returnList = new ArrayList<VODCategoryNodeData>();  
			if (requestTagList == null){
				return returnList;
			}
			
			if (childNodes!=null && childNodes.size()>0){
				for (VODCategoryNodeData catalogNode : childNodes.values()){
					if(catalogNode.tags != null && catalogNode.tags.size()>0){
						for (String tag : requestTagList){
							if (catalogNode.tags.contains(tag)){
								returnList.addAll(catalogNode.getAllSeriesNode());
								break;
							}
						}
					}
				}
			}
			return returnList;
		}
		
		
		public List<VODData> getAllVODProduct(){
			ArrayList<VODData> returnList = new ArrayList<VODData>();

			if (childNodes!=null && childNodes.size()>0){
				for (VODCategoryNodeData catalogNode : childNodes.values()){
					returnList.addAll(catalogNode.getAllVODProduct());
				}
			} else {
				if (!"series".equalsIgnoreCase(type)){
					Collection<VODData> allProdcut = getInstance().getVODDataByNodeId(nodeId).values();
					if (allProdcut!=null){
						returnList.addAll(allProdcut);
					}
				}
			}
			return returnList;
		}
		
		/**
		 * 
		 * @author Situ
		 * @return product list of this node
		 */
		public List<VODData> getProductList(){
			ArrayList<VODData> returnList = new ArrayList<VODData>();
			Collection<VODData> allProdcut = getInstance().getVODDataByNodeId(nodeId).values();
			if (allProdcut != null) {
				returnList.addAll(allProdcut);
			}
			return returnList;
		}
		
		/**
		 * 
		 * @author Situ
		 * @return child nodes that are "series"
		 */
		public List<VODCategoryNodeData> getSeriesNodeList(){
			ArrayList<VODCategoryNodeData> returnList = new ArrayList<VODCategoryNodeData>();  

			if (childNodes == null || childNodes.size() == 0){
				return returnList;
			}
			
			for (VOD.VODCategoryNodeData child : childNodes.values()){
				if ("series".equalsIgnoreCase(child.getType())){
					returnList.add(child);
				}
			}
			return returnList;
		}
		
		/**
		 * 
		 * @author Situ
		 * @return product and series list
		 */
		public List<Object> getProductAndSeriesList(){
			ArrayList<Object> returnList = new ArrayList<Object>();  
			returnList.addAll(this.getProductList());
			returnList.addAll(this.getSeriesNodeList());
			return returnList;
		}		
		
		
		public List<VODData> getAllVODProductWithChildTag(String ...requestTagList){
			ArrayList<VODData> returnList = new ArrayList<VODData>();

			
			if (requestTagList == null){
				return returnList;
			}

			if (childNodes!=null && childNodes.size()>0){
				for (VODCategoryNodeData catalogNode : childNodes.values()){
					if(catalogNode.tags != null && catalogNode.tags.size()>0){
						for (String tag : requestTagList){
							if (catalogNode.tags.contains(tag)){
								returnList.addAll(catalogNode.getAllVODProduct());
								break;
							}
						}
					}
				}
			} else {
				if (!"series".equalsIgnoreCase(type)){
					Collection<VODData> allProdcut = getInstance().getVODDataByNodeId(nodeId).values();
					if (allProdcut!=null){
						returnList.addAll(allProdcut);
					}
				}
			}
			return returnList;
		}

		@Override
		public int compareTo(VODCategoryNodeData another) {
			//sort in descending order of id
			if (this.id.compareTo(another.id)<1){
				return 1;
			} else {
				return -1;
			}

		}
		
	}
	
	/**
	 *  VOD product detail extract from vodDetail.json
	 *
	 */
	public class VODData implements Comparable<VODData>{
		private String nodeId;
		private boolean flvAssetStatus;
		private int viewingPeriod;
		private String offAirDate;
		private String disclaimer;
		private String hlsAssetId;
		private boolean productEncodingHD;
		private boolean hasChapter;
		private String cpId;
		private boolean isLock;
		private String flvAssetId;
		private boolean subscriptionCheck;
		private String episodeId;
		private boolean isAdultContent;
		private boolean displayWhenAssetReady;
		private String actor;
		private boolean webAssetStatus;
		private String npvrId;
		private String libId;
		private String webImg2Path;
		private boolean hlsAssetStatus;
		private boolean productEncodingSuperHD;
		private boolean isRestricted;
		private String classification;
		private String paymentType;
		private String episodeName;
		private boolean productEncodingSD;
		private String webImg1Path;
		private String webActors;
		private boolean isRestrictedToApp;
		private String scheduleId;
		private String languages;
		private String episodeTitle;
		private int duration;
		private String webAssetId;
		private String webDirectors;
		private String webSynopsis;
		private boolean isPayPerView;
		private int webImg1PathWidth;
		private int webImg1PathHeight;
		private int webImg2PathWidth;
		private int webImg2PathHeight;
		private String onAirDate;
		private List<VODTrailerData> trailerList;
		private String hdImg1Path;
		private String breakdownProductName;
		private String description;
		private String hdImg2Path;
		private String episodeNumber;
		private String trailerId;
		private boolean downloadable;
		private String productDate;
		private String externalRatingId1;
		private String libImg1Path;
		private boolean isOffAir;
		
		
		public String getNodeId() {
			return nodeId;
		}
		public void setNodeId(String nodeId) {
			this.nodeId = nodeId;
		}
		public String getEpisodeId() {
			return episodeId;
		}
		public void setEpisodeId(String episodeId) {
			this.episodeId = episodeId;
		}
		public int getViewingPeriod() {
			return viewingPeriod;
		}
		public void setViewingPeriod(int viewingPeriod) {
			this.viewingPeriod = viewingPeriod;
		}
		public boolean isFlvAssetStatus() {
			return flvAssetStatus;
		}
		public void setFlvAssetStatus(boolean flvAssetStatus) {
			this.flvAssetStatus = flvAssetStatus;
		}
		public String getOffAirDate() {
			return offAirDate;
		}
		public void setOffAirDate(String offAirDate) {
			this.offAirDate = offAirDate;
		}
		public String getDisclaimer() {
			return disclaimer;
		}
		public void setDisclaimer(String disclaimer) {
			this.disclaimer = disclaimer;
		}
		public String getHlsAssetId() {
			return hlsAssetId;
		}
		public void setHlsAssetId(String hlsAssetId) {
			this.hlsAssetId = hlsAssetId;
		}
		public boolean isProductEncodingHD() {
			return productEncodingHD;
		}
		public void setProductEncodingHD(boolean productEncodingHD) {
			this.productEncodingHD = productEncodingHD;
		}
		public boolean isHasChapter() {
			return hasChapter;
		}
		public void setHasChapter(boolean hasChapter) {
			this.hasChapter = hasChapter;
		}
		public String getCpId() {
			return cpId;
		}
		public void setCpId(String cpId) {
			this.cpId = cpId;
		}
		public boolean isLock() {
			return isLock;
		}
		public void setLock(boolean isLock) {
			this.isLock = isLock;
		}
		public String getFlvAssetId() {
			return flvAssetId;
		}
		public void setFlvAssetId(String flvAssetId) {
			this.flvAssetId = flvAssetId;
		}
		public boolean isSubscriptionCheck() {
			return subscriptionCheck;
		}
		public void setSubscriptionCheck(boolean subscriptionCheck) {
			this.subscriptionCheck = subscriptionCheck;
		}
		public boolean isAdultContent() {
			return isAdultContent;
		}
		public void setAdultContent(boolean isAdultContent) {
			this.isAdultContent = isAdultContent;
		}
		public boolean isDisplayWhenAssetReady() {
			return displayWhenAssetReady;
		}
		public void setDisplayWhenAssetReady(boolean displayWhenAssetReady) {
			this.displayWhenAssetReady = displayWhenAssetReady;
		}
		public String getActor() {
			return actor;
		}
		public void setActor(String actor) {
			this.actor = actor;
		}
		public boolean isWebAssetStatus() {
			return webAssetStatus;
		}
		public void setWebAssetStatus(boolean webAssetStatus) {
			this.webAssetStatus = webAssetStatus;
		}
		public String getNpvrId() {
			return npvrId;
		}
		public void setNpvrId(String npvrId) {
			this.npvrId = npvrId;
		}
		public String getLibId() {
			return libId;
		}
		public void setLibId(String libId) {
			this.libId = libId;
		}
		public String getWebImg2Path() {
			return webImg2Path;
		}
		public void setWebImg2Path(String webImg2Path) {
			this.webImg2Path = webImg2Path;
		}
		public boolean isHlsAssetStatus() {
			return hlsAssetStatus;
		}
		public void setHlsAssetStatus(boolean hlsAssetStatus) {
			this.hlsAssetStatus = hlsAssetStatus;
		}
		public boolean isProductEncodingSuperHD() {
			return productEncodingSuperHD;
		}
		public void setProductEncodingSuperHD(boolean productEncodingSuperHD) {
			this.productEncodingSuperHD = productEncodingSuperHD;
		}
		public boolean isRestricted() {
			return isRestricted;
		}
		public void setRestricted(boolean isRestricted) {
			this.isRestricted = isRestricted;
		}
		public String getClassification() {
			return classification;
		}
		public void setClassification(String classification) {
			this.classification = classification;
		}
		public String getPaymentType() {
			return paymentType;
		}
		public void setPaymentType(String paymentType) {
			this.paymentType = paymentType;
		}
		public String getEpisodeName() {
			return episodeName;
		}
		public void setEpisodeName(String episodeName) {
			this.episodeName = episodeName;
		}
		public boolean isProductEncodingSD() {
			return productEncodingSD;
		}
		public void setProductEncodingSD(boolean productEncodingSD) {
			this.productEncodingSD = productEncodingSD;
		}
		public String getWebImg1Path() {
			return webImg1Path;
		}
		public void setWebImg1Path(String webImg1Path) {
			this.webImg1Path = webImg1Path;
		}
		public String getWebActors() {
			return webActors;
		}
		public void setWebActors(String webActors) {
			this.webActors = webActors;
		}
		public boolean isRestrictedToApp() {
			return isRestrictedToApp;
		}
		public void setRestrictedToApp(boolean isRestrictedToApp) {
			this.isRestrictedToApp = isRestrictedToApp;
		}
		public String getScheduleId() {
			return scheduleId;
		}
		public void setScheduleId(String scheduleId) {
			this.scheduleId = scheduleId;
		}
		public String getLanguages() {
			return languages;
		}
		public void setLanguages(String languages) {
			this.languages = languages;
		}
		public String getEpisodeTitle() {
			return episodeTitle;
		}
		public void setEpisodeTitle(String episodeTitle) {
			this.episodeTitle = episodeTitle;
		}
		public int getDuration() {
			return duration;
		}
		public void setDuration(int duration) {
			this.duration = duration;
		}
		public String getWebAssetId() {
			return webAssetId;
		}
		public void setWebAssetId(String webAssetId) {
			this.webAssetId = webAssetId;
		}
		public String getWebDirectors() {
			return webDirectors;
		}
		public void setWebDirectors(String webDirectors) {
			this.webDirectors = webDirectors;
		}
		public String getWebSynopsis() {
			return webSynopsis;
		}
		public void setWebSynopsis(String webSynopsis) {
			this.webSynopsis = webSynopsis;
		}
		public boolean isPayPerView() {
			return isPayPerView;
		}
		public void setPayPerView(boolean isPayPerView) {
			this.isPayPerView = isPayPerView;
		}
		public List<VODTrailerData> getTrailerList() {
			return trailerList;
		}
		public int getWebImg1PathWidth() {
			return webImg1PathWidth;
		}
		public void setWebImg1PathWidth(int webImg1PathWidth) {
			this.webImg1PathWidth = webImg1PathWidth;
		}
		public int getWebImg1PathHeight() {
			return webImg1PathHeight;
		}
		public void setWebImg1PathHeight(int webImg1PathHeight) {
			this.webImg1PathHeight = webImg1PathHeight;
		}
		public int getWebImg2PathWidth() {
			return webImg2PathWidth;
		}
		public void setWebImg2PathWidth(int webImg2PathWidth) {
			this.webImg2PathWidth = webImg2PathWidth;
		}
		public int getWebImg2PathHeight() {
			return webImg2PathHeight;
		}
		public void setWebImg2PathHeight(int webImg2PathHeight) {
			this.webImg2PathHeight = webImg2PathHeight;
		}
		public String getOnAirDate() {
			return onAirDate;
		}
		public void setOnAirDate(String onAirDate) {
			this.onAirDate = onAirDate;
		}
		public synchronized void addTrailer(VODTrailerData trailer) {
			if (trailerList == null) {
				trailerList = new ArrayList<VODTrailerData>();
			}
			trailerList.add(trailer);
		}
		
		public String getHdImg1Path() {
			return hdImg1Path;
		}
		public void setHdImg1Path(String hdImg1Path) {
			this.hdImg1Path = hdImg1Path;
		}
		public String getBreakdownProductName() {
			return breakdownProductName;
		}
		public void setBreakdownProductName(String breakdownProductName) {
			this.breakdownProductName = breakdownProductName;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		
		public String getHdImg2Path() {
			return hdImg2Path;
		}
		public void setHdImg2Path(String hdImg2Path) {
			this.hdImg2Path = hdImg2Path;
		}
		
		public String getEpisodeNumber() {
			return episodeNumber;
		}
		public void setEpisodeNumber(String episodeNumber) {
			this.episodeNumber = episodeNumber;
		}
		public String getTrailerId() {
			return trailerId;
		}
		public void setTrailerId(String trailerId) {
			this.trailerId = trailerId;
		}
		public boolean isDownloadable() {
			return downloadable;
		}
		public void setDownloadable(boolean downloadable) {
			this.downloadable = downloadable;
		}
		public String getProductDate() {
			return productDate;
		}
		public void setProductDate(String productDate) {
			this.productDate = productDate;
		}
		public String getExternalRatingId1() {
			return externalRatingId1;
		}
		public void setExternalRatingId1(String externalRatingId1) {
			this.externalRatingId1 = externalRatingId1;
		}
		public String getLibImg1Path() {
			return libImg1Path;
		}
		public void setLibImg1Path(String libImg1Path) {
			this.libImg1Path = libImg1Path;
		}
		
		public boolean isOffAir() {
			return isOffAir;
		}
		public void setOffAir(boolean isOffAir) {
			this.isOffAir = isOffAir;
		}
		@Override
		public int compareTo(VODData another) {
			//descending order
			if (this.episodeId.compareTo(another.episodeId) < 1){
				return 1;
			} else{
				return -1;
			}
			
		}
	}
	
	public List<VODCategoryNodeData> getVodCategoryRootNodeList(){
		if (vodCategoryNodeList!=null){
			return new ArrayList<VODCategoryNodeData>(vodCategoryNodeList.values());
		} else{
			return null;
		}
	}
	
	public List<VODCategoryNodeData> getVodCategoryRootNodeListWithTag(String... tagList){
		ArrayList<VODCategoryNodeData> returnArray = new ArrayList<VODCategoryNodeData>();
		
		if (tagList == null){
			return returnArray;
		}
		
		if (vodCategoryNodeList!=null){
			for (VODCategoryNodeData catalogNode : vodCategoryNodeList.values()){
				if(catalogNode.tags != null && catalogNode.tags.size()>0){
					for (String tag : tagList){
						if (catalogNode.tags.contains(tag)){
							returnArray.add(catalogNode);
							break;
						}
					}
				}
			}
		}
		return returnArray;
	}
	
	public List<VODData> getVODProductFromNodes(VODCategoryNodeData... categoryNodes){
		ArrayList<VODData> returnList = new ArrayList<VODData>();
		
		if (categoryNodes!=null && categoryNodes.length > 0){
			for (int i =0; i< categoryNodes.length; i++){
				returnList.addAll(categoryNodes[i].getAllVODProduct());
			}
		}
		
		return returnList;
	}
	
	public List<VODCategoryNodeData> getSeriesFromNodes(VODCategoryNodeData... categoryNodes){
		ArrayList<VODCategoryNodeData> returnList = new ArrayList<VODCategoryNodeData>();
		
		if (categoryNodes!=null && categoryNodes.length > 0){
			for (int i =0; i< categoryNodes.length; i++){
				returnList.addAll(categoryNodes[i].getAllSeriesNode());
			}
		}
		
		return returnList;
	}
	
	public List<VODCategoryNodeData> sortVODCategoryNodeData(List<VODCategoryNodeData> nodeList, Comparator<VODCategoryNodeData> comparator){
		Collections.sort(nodeList, comparator);
		return nodeList;
	}
	
	public List<VODData> sortVODProduct(List<VODData> productList, Comparator<VODData> comparator){
		Collections.sort(productList, comparator);
		return productList;
	}
	
	public class VODTrailerData {
		private String trailerId;
		private String displayName;
		private String type;
		
		public String getTrailerId() {
			return trailerId;
		}
		public void setTrailerId(String trailerId) {
			this.trailerId = trailerId;
		}
		public String getDisplayName() {
			return displayName;
		}
		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
	}
}
