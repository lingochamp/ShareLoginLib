package com.liulishuo.share.base.share;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by echo on 5/18/15.
 * 分享网页模式
 */
public class ShareContentWebpage extends ShareContent {

    private String title;

    private String summary;

    private String url;

    private String imageUrl;
    
    private Bitmap bitmap;

    /**
     * 给QQ分享使用
     * 
     * @param title 标题
     * @param summary 描述
     * @param url 点击分享链接后跳转的链接
     * @param imageUrl 图片的url，可以是网页的，可以是本地文件
     */
    public ShareContentWebpage(String title, String summary, String url, String imageUrl) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.imageUrl = imageUrl;
    }

    /**
     * 给weibo和wechat使用
     * 
     * @param title 标题
     * @param summary 描述 
     * @param url 点击分享链接后跳转的链接
     * @param image 图片的bitmap（请用缩略图）
     */
    public ShareContentWebpage(String title, String summary, String url, @NonNull Bitmap image) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.bitmap = image;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_WEBPAGE;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public Bitmap getImageBmp() {
        return bitmap;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }
}
