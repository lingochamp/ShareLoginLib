package com.liulishuo.share;

import android.support.annotation.NonNull;

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

    private String mAppName;
    
    public ShareBlock initAppName(@NonNull String appName) {
        mAppName = appName;
        return this;
    }
    
    private String mWechatAppId;

    private String mWechatSecret;

    /**
     * init all config
     */
/*    public void initShare(String wechatAppId, String weiboAppId, String qqAppId, String wechatSecret) {
        mWechatAppId = wechatAppId;
        mWeiboAppId = weiboAppId;
        mQQAppId = qqAppId;
        mWechatSecret = wechatSecret;
    }*/

    /**
     * init wechat config
     */
    public ShareBlock initWechat(@NonNull String wechatAppId, @NonNull String wechatSecret) {
        mWechatAppId = wechatAppId;
        mWechatSecret = wechatSecret;
        return this;
    }
    
    /**
     * init weibo config
     */
    private String mWeiboAppId;
    private String mWeiboRedirectUrl;
    private String mWeiboScope;
    
    public ShareBlock initWeibo(@NonNull String weiboAppId,@NonNull String redirectUrl,@NonNull String scope) {
        mWeiboAppId = weiboAppId;
        mWeiboRedirectUrl = redirectUrl;
        mWeiboScope = scope;
        return this;
    }

    /**
     * init QQ config
     */
    private String mQQAppId;
    private String mQQScope;
    
    public ShareBlock initQQ(@NonNull String qqAppId, @NonNull String scope) {
        mQQAppId = qqAppId;
        mQQScope = scope;
        return this;
    }
    
    

    public String getWechatSecret() {
        return mWechatSecret;
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

    public String getWeiboRedirectUrl() {
        return mWeiboRedirectUrl;
    }

    public String getWeiboScope() {
        return mWeiboScope;
    }

    public String getQQScope() {
        return mQQScope;
    }

    public String getAppName() {
        return mAppName;
    }
}
