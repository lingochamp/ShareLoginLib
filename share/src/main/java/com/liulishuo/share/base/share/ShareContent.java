package com.liulishuo.share.base.share;

import android.graphics.Bitmap;

/**
 * Created by echo on 5/18/15.
 */

public abstract class ShareContent {

    /**
     * 分享的方式
     * @return
     */
    public abstract int getShareWay();

    /**
     * 分享的描述信息(摘要)
     * @return
     */
    public abstract String getSummary();

    /**
     * 分享的标题
     * @return
     */
    public abstract String getTitle();

    /**
     * 获取跳转的链接
     * @return
     */
    public abstract String getURL();

    /**
     * 分享的本地图片路径或图片网络url
     * @return
     */
    public abstract String getImageUrl();

    /**
     * 分享的图片url
     * @return
     */
    public abstract Bitmap getImageBmp();

    /**
     * 音频url
     */
    public abstract String getMusicUrl();

}
