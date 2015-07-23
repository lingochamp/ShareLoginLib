package com.liulishuo.share;

import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.share.IShareManager;
import com.liulishuo.share.qq.QQLoginManager;
import com.liulishuo.share.qq.QQShareManager;
import com.liulishuo.share.weibo.WeiboLoginManager;
import com.tencent.connect.common.Constants;

import android.content.Intent;
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

    public static void handlerOnActivityResult(ILoginManager loginManager,IShareManager sharemanager,int requestCode, int resultCode, Intent data) {
        if (loginManager != null && loginManager instanceof QQLoginManager) {
            ((QQLoginManager) loginManager).handlerOnActivityResult(requestCode, resultCode, data);
            //fix the bug that qq sso failed.
            if (requestCode == Constants.REQUEST_API) {
                if (resultCode == Constants.RESULT_LOGIN) {
                    ((QQLoginManager) loginManager).getTencent().handleLoginData(data, null);
                }
            }
        } else if (loginManager != null && loginManager instanceof WeiboLoginManager) {
            ((WeiboLoginManager) loginManager).handlerOnActivityResult(requestCode, resultCode, data);
        }
        // 进行分享完毕后的回调处理
        if (sharemanager != null && sharemanager instanceof QQShareManager) {
            ((QQShareManager) sharemanager).handlerOnActivityResult(requestCode, resultCode, data);
        }
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
