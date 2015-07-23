package com.liulishuo.share.weibo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.share.ShareConstants;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.GetUserListener;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.weibo.model.User;
import com.liulishuo.share.weibo.model.UsersAPI;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by echo on 5/19/15.
 */
public class WeiboLoginManager implements ILoginManager {

    private Context mContext;

    private String mSinaAppKey;

    private LoginListener mLoginListener;

    private Oauth2AccessToken mAccessToken;

    /**
     * 注意：SsoHandler 仅当 SDK 支持 SSO 时有效
     */
    private static SsoHandler mSsoHandler;

    public WeiboLoginManager(Context context) {
        mContext = context;
        mSinaAppKey = ShareBlock.getInstance().getWeiboAppId();
    }

    @Override
    public void login(@NonNull LoginListener loginListener) {
        mLoginListener = loginListener;
        AccessTokenKeeper.clear(mContext);
        AuthInfo authInfo = new AuthInfo(mContext, mSinaAppKey, 
                ShareBlock.getInstance().getWeiboRedirectUrl(),
                ShareBlock.getInstance().getWeiboScope());
        // TODO: 2015/7/22 判断微博客户端是否安装，如果没安装，跳转到网页版 
        mSsoHandler = new SsoHandler((Activity) mContext, authInfo);
        mSsoHandler.authorize(new AuthLoginListener());
    }

    /**
     * * 1. SSO 授权时，需要在 onActivityResult 中调用 {@link SsoHandler#authorizeCallBack} 后，
     * 该回调才会被执行。
     * 2. 非SSO 授权时，当授权结束后，该回调就会被执行
     */
    private class AuthLoginListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            final Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken != null && accessToken.isSessionValid()) {
                mAccessToken = accessToken;
                mLoginListener.onLoginComplete(accessToken.getUid(), accessToken.getToken(), accessToken.getExpiresTime() / 1000000);
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            mLoginListener.onError(e.getMessage());
        }

        @Override
        public void onCancel() {
            mLoginListener.onCancel();
        }
    }

    @Override
    public void getUserInfo(final @NonNull GetUserListener listener) {
        AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
        UsersAPI userAPI = new UsersAPI(mContext, mSinaAppKey, mAccessToken);
        userAPI.show(Long.parseLong(mAccessToken.getUid()), new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    // 调用 User#parse 将JSON串解析成User对象
                    User user = User.parse(response);
                    if (user != null) {
                        HashMap<String, String> userInfoHashMap = new HashMap<>();
                        userInfoHashMap.put(ShareConstants.PARAMS_NICK_NAME, user.name);
                        userInfoHashMap.put(ShareConstants.PARAMS_SEX, user.gender);
                        userInfoHashMap.put(ShareConstants.PARAMS_IMAGEURL, user.avatar_large);
                        userInfoHashMap.put(ShareConstants.PARAMS_USERID, user.id);
                        listener.onComplete(userInfoHashMap);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                listener.onError(e.getMessage());
            }
        });
    }

    public void handlerOnActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
