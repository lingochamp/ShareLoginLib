package com.liulishuo.share.base.share;

import android.graphics.Bitmap;

/**
 * Created by echo on 5/18/15.
 * 音乐模式
 */
public class ShareContentMusic extends ShareContent{

    private String title;

    private String summary;

    private String url;

    private String imageUrl;

    private String musicUrl;

    private Bitmap imageBmp;

    /**
     * 给weibo、wehat使用
     */
    public ShareContentMusic(String title, String summary, String url, Bitmap imageBmp, String musicUrl) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.imageBmp = imageBmp;
        this.musicUrl = musicUrl;
    }

    /**
     * 给QQ使用
     */
    public ShareContentMusic(String title, String summary, String url, String imageUrl, String musicUrl) {
        this.title = title;
        this.summary = summary;
        this.url = url;
        this.imageUrl = imageUrl;
        this.musicUrl = musicUrl ;
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
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public Bitmap getImageBmp() {
        return imageBmp;
    }

    @Override
    public String getMusicUrl() {
        return musicUrl;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_MUSIC;
    }

}
