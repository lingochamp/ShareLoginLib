package com.liulishuo.share.base.share;

import android.graphics.Bitmap;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic extends ShareContent {

    private String imageUrl;

    private Bitmap imageBmp;

    /**
     * 给weibo、wechat使用
     */
    public ShareContentPic(Bitmap bitmap) {
        imageBmp = bitmap;
    }

    /**
     * 给QQ使用
     */
    public ShareContentPic(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getSummary() {
        return null;
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
        return imageUrl;
    }

    @Override
    public Bitmap getImageBmp() {
        return imageBmp;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_PIC;
    }
}
