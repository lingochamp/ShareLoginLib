package com.liulishuo.share.model;

import com.liulishuo.share.data.ShareConstants;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic extends ShareContent {

    private String imageUrl;

    public ShareContentPic(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String getContent() {
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
    public String getMusicUrl() {
        return null;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_PIC;
    }
}
