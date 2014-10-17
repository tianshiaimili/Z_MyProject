
package com.hua.bean;

import java.util.List;

public class NewModle extends BaseModle {
    /**
     * 
     * {"hasCover":false,"hasHead":1,
     * "replyCount":10257,"hasImg":1,
     * "digest":"旺角被占领道路交通恢复，在场警员与市民鼓掌欢呼。",
     * "adTitle":"网易新闻有态度！","hasIcon":false,
     * "docid":"A8OF785R0001124J",
     * "title":"香港开始清除旺角\"占中\"路障","order":1,"priority":250,"lmodify":"2014-10-17 10:09:54",
     * "url_3w":"http://news.163.com/14/1017/09/A8OF785R0001124J.html",
     * "template":"manual","timeConsuming":"1分钟","alias":"Top News",
     * "cid":"C1348646712614","url":"http://3g.163.com/news/14/1017/09/A8OF785R0001124J.html",
     * "hasAD":1,
     * "source":"新华网","subtitle":"",
     * "imgsrc":"http://img4.cache.netease.com/3g/2014/10/17/2014101711124264c2d.jpg",
     * "tname":"头条","ename":"androidnews",
     * "ptime":"2014-10-17 09:16:31"}
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * docid 文档ID？
     */
    private String docid;
    /**
     * 新闻的标题  如"香港开始清除旺角\"占中\"路障"
     */
    private String title;
    /**
     *新闻的简要说明
     */
    private String digest;
    /**
     * 图片的urlַ
     */
    private String imgsrc;
    /**
     * 新闻来源 如来自新华网
     */
    private String source;
    /**
     * 新闻发布时间
     */
    private String ptime;
    /**
     * TAG 有可能是"独家"、或者"视频"这样的标签
     */
    private String tag;
    /**
     * �б�
     */
    private ImagesModle imagesModle;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ImagesModle getImagesModle() {
        return imagesModle;
    }

    public void setImagesModle(ImagesModle imagesModle) {
        this.imagesModle = imagesModle;
    }

    /**
     * ͷ���б�
     */
    private List<ImagesModle> imgHeadLists;

    public List<ImagesModle> getImgHeadLists() {
        return imgHeadLists;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public void setImgHeadLists(List<ImagesModle> imgHeadLists) {
        this.imgHeadLists = imgHeadLists;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }
}
