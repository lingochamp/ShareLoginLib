package com.liulishuo.share;

/**
 * Created by echo on 5/18/15.
 */
public class ShareBlock {


    private static ShareBlock mInstance;

    private ShareBlock() {
    }

    public static ShareBlock getInstance() {
        if (mInstance == null) {
            mInstance = new ShareBlock();
        }
        return mInstance;
    }

    private String mWechatAppId = "";

    private String mWeiboAppId = "";

    private String mQQAppId = "";

    private String mWechatSecret = "";

    private String mRedriectUrl = "http://www.liulishuo.com";  //default url


    /**
     * init all config
     */
    public void initShare(String wechatAppId, String weiboAppId, String qqAppId,
            String wechatSecret) {
        mWechatAppId = wechatAppId;
        mWeiboAppId = weiboAppId;
        mQQAppId = qqAppId;
        mWechatSecret = wechatSecret;

    }


    /**
     * init wechat config
     */
    public void initWechat(String wechatAppId, String wechatSecret) {
        mWechatAppId = wechatAppId;
        mWechatSecret = wechatSecret;
    }


    /**
     * init weibo config
     */
    public void initWeibo(String weiboAppId) {

        mWeiboAppId = weiboAppId;
    }

    /**
     * init QQ config
     */
    public void initQQ(String qqAppId) {

        mQQAppId = qqAppId;
    }

    public void initWeiboRedriectUrl(String url) {
        mRedriectUrl = url;
    }


    public String getWechatAppId() {
        return mWechatAppId;
    }

    public String getWeiboAppId() {
        return mWeiboAppId;
    }

    public String getQQAppId() {
        return mQQAppId;
    }

    public String getWechatSecret() {
        return mWechatSecret;
    }


    public String getRedriectUrl() {
        return mRedriectUrl;
    }
}
