package com.liulishuo.share.base.share;

import android.graphics.Bitmap;

/**
 * Created by echo on 5/18/15.
 * 分享文本内容
 */
public class ShareContentText extends ShareContent {

    private String summary;

    public ShareContentText(String summary) {
        this.summary = summary;
    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public String getImageUrl() {
        return null;
    }

    @Override
    public Bitmap getImageBmp() {
        return null;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_TEXT;
    }

}