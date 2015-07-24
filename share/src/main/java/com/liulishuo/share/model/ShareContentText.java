package com.liulishuo.share.model;

import com.liulishuo.share.data.ShareConstants;

/**
 * Created by echo on 5/18/15.
 * 分享文本内容
 */
public class ShareContentText extends ShareContent {

    private String content;

    public ShareContentText(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
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
    public String getMusicUrl() {
        return null;
    }

    @Override
    public int getShareWay() {
        return ShareConstants.SHARE_WAY_TEXT;
    }

}