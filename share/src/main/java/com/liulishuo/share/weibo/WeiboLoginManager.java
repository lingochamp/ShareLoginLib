package com.liulishuo.share.weibo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.login.GetUserListener;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.share.ShareConstants;
import com.liulishuo.share.util.HttpUtil;
import com.liulishuo.share.weibo.model.AbsOpenAPI;
import com.liulishuo.share.weibo.model.User;
import com.liulishuo.share.weibo.model.UsersAPI;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

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
                AccessTokenKeeper.writeAccessToken(mContext, mAccessToken);
                mLoginListener.onLoginComplete(accessToken.getUid(), accessToken.getToken(), accessToken.getExpiresTime() / 1000000,
                        oAuthData2Json(mAccessToken));
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

    /**
     * 得到用户信息的操作(在登录完毕后直接调用)
     */
    @Override
    public void getUserInfo(final @NonNull GetUserListener listener) {
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

    /**
     * @see "http://open.weibo.com/wiki/2/users/show"
     */
    @Override
    public void getUserInfo(String accessToken, String uid, final @NonNull GetUserListener listener) {
        StringBuilder builder = new StringBuilder();
        builder.append(AbsOpenAPI.API_SERVER)
                .append("/users/show.json")
                .append("?access_token=").append(accessToken)
                .append("&uid").append(uid);
        HttpUtil.doGetAsyn(builder.toString(), new HttpUtil.CallBack() {
            @Override
            public void onRequestComplete(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    HashMap<String, String> userInfoHashMap = new HashMap<>();
                    userInfoHashMap.put(ShareConstants.PARAMS_NICK_NAME, jsonObject.getString("screen_name"));
                    userInfoHashMap.put(ShareConstants.PARAMS_SEX, jsonObject.getString("gender"));
                    userInfoHashMap.put(ShareConstants.PARAMS_IMAGEURL, jsonObject.getString("avatar_large"));
                    userInfoHashMap.put(ShareConstants.PARAMS_USERID, jsonObject.getString("id"));

                    listener.onComplete(userInfoHashMap);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onError("user data parse error");
                }
            }

            @Override
            public void onError() {
                listener.onError("get user data error : {network error}");
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

    private String oAuthData2Json(@NonNull Oauth2AccessToken data) {
        JSONObject sina_json = new JSONObject();
        try {
            sina_json.put("uid", data.getUid());
            sina_json.put("refresh_token", data.getRefreshToken());
            sina_json.put("access_token", data.getToken());
            sina_json.put("expires_in", String.valueOf(data.getExpiresTime() / 1000000));
            return sina_json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
