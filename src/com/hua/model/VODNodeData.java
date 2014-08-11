package com.hua.model;

import java.util.ArrayList;
import java.util.List;

import com.hua.model.VOD.VODCategoryNodeData;
import com.hua.model.VOD.VODData;

public class VODNodeData {
	public String tilte;
	public String subTilte;
	public List<VODCategoryNodeData> serierNodeList;
	public List<VODData> productNodeList;
	public int nodeNum;
	
	
	/**
	 * 自己创建一个构造方法
	 */
	public VODNodeData(String tilte, String subTilte) {
		super();
		this.tilte = tilte;
		this.subTilte = subTilte;
	}
	
	
	public VODNodeData() {
		super();
		serierNodeList = new ArrayList<VODCategoryNodeData>();
		productNodeList = new ArrayList<VODData>();
	}

	
	


	public int getNodeNum() {
		int num = 0;
		if(serierNodeList != null && serierNodeList.size() > 0){
			num += serierNodeList.size();
		}
		if(productNodeList != null && productNodeList.size() > 0){
			num += productNodeList.size();
		}
		return num;
	}


	public void setNodeNum(int nodeNum) {
		this.nodeNum = nodeNum;
	}


	public String getTilte() {
		return tilte;
	}


	public void setTilte(String tilte) {
		this.tilte = tilte;
	}


	public List<VODCategoryNodeData> getSerierNodeList() {
		return serierNodeList;
	}

	public void setSerierNodeList(List<VODCategoryNodeData> serierNodeList) {
		this.serierNodeList = serierNodeList;
	}

	public List<VODData> getProductNodeList() {
		return productNodeList;
	}

	public void setProductNodeList(List<VODData> productNodeList) {
		this.productNodeList = productNodeList;
	}


	public String getSubTilte() {
		return subTilte;
	}


	public void setSubTilte(String subTilte) {
		this.subTilte = subTilte;
	}
	
	
	
}
