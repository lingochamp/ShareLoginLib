package com.liulishuo.share.qq;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.login.GetUserListener;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.share.ShareConstants;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by echo on 5/19/15.
 */
public class QQLoginManager implements ILoginManager {

    private Activity mActivity;

    private Tencent mTencent;

    public QQLoginManager(Activity activity) {
        mActivity = activity;
        String appId = ShareBlock.getInstance().getQQAppId();
        if (!TextUtils.isEmpty(appId)) {
            mTencent = Tencent.createInstance(appId, activity);
        }
    }

    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void login(final @NonNull LoginListener loginListener) {
        if (!mTencent.isSessionValid()) {
            mTencent.login(mActivity, ShareBlock.getInstance().getQQScope(), new IUiListener() {
                @Override
                public void onComplete(Object object) {
                    JSONObject jsonObject = (JSONObject) object; // qq_json
                    initOpenidAndToken(jsonObject); // 初始化id和access token
                    loginListener.onLoginComplete(mTencent.getOpenId(), mTencent.getAccessToken(), mTencent.getExpiresIn());
                }

                @Override
                public void onError(UiError uiError) {
                    loginListener.onError(uiError.errorCode + " - " + uiError.errorMessage + " - " + uiError.errorDetail);
                }

                @Override
                public void onCancel() {
                    loginListener.onCancel();
                }
            });
        } else {
            mTencent.logout(mActivity);
        }
    }

    @Override
    public void getUserInfo(final @NonNull GetUserListener listener) {
        UserInfo info = new UserInfo(mActivity, mTencent.getQQToken());
        // 执行获取用户信息的操作
        info.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object object) {
                try {
                    JSONObject jsonObject = (JSONObject) object;
                    HashMap<String, String> userInfoHashMap = new HashMap<>();
                    userInfoHashMap.put(ShareConstants.PARAMS_NICK_NAME, jsonObject.getString("nickname"));
                    userInfoHashMap.put(ShareConstants.PARAMS_SEX, jsonObject.getString("gender"));
                    userInfoHashMap.put(ShareConstants.PARAMS_IMAGEURL, jsonObject.getString("figureurl_qq_2"));
                    userInfoHashMap.put(ShareConstants.PARAMS_USERID, mTencent.getOpenId());
                    listener.onComplete(userInfoHashMap);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onError("userInfo data error");
                }
            }

            @Override
            public void onError(UiError uiError) {
                listener.onError(uiError.errorCode + " - " + uiError.errorMessage + " - " + uiError.errorDetail);
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }
        });
    }

    public void handlerOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (mTencent != null) {
            mTencent.onActivityResult(requestCode, resultCode, data);
        }
    }

    public Tencent getTencent() {
        return mTencent;
    }
}


