package com.liulishuo.share.qq;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.data.ShareConstants;
import com.liulishuo.share.model.ILoginManager;
import com.liulishuo.share.model.PlatformActionListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by echo on 5/19/15.
 */
public class QQLoginManager implements ILoginManager {


    private Context mContext;

    private String mAppId;

    private Tencent mTencent;

    protected PlatformActionListener mPlatformActionListener;


    public QQLoginManager(Context context) {
        mContext = context;
        mAppId = ShareBlock.getInstance().getQQAppId();
        if (!TextUtils.isEmpty(mAppId)) {
            mTencent = Tencent.createInstance(mAppId, context);
        }
    }


    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }


    @Override
    public void login(PlatformActionListener platformActionListener) {
        if (!mTencent.isSessionValid()) {

            mPlatformActionListener = platformActionListener;
            mTencent.login((Activity) mContext, "all", new IUiListener() {
                @Override
                public void onComplete(Object object) {
                    JSONObject jsonObject = (JSONObject) object;
                    initOpenidAndToken(jsonObject);
                    UserInfo info = new UserInfo(mContext, mTencent.getQQToken());
                    info.getUserInfo(new IUiListener() {
                        @Override
                        public void onComplete(Object object) {

                            try {
                                JSONObject jsonObject = (JSONObject) object;
                                HashMap<String, Object> userInfoHashMap
                                        = new HashMap<String, Object>();
                                userInfoHashMap.put(ShareConstants.PARAMS_NICK_NAME,
                                        jsonObject.getString("nickname"));
                                userInfoHashMap.put(ShareConstants.PARAMS_SEX,
                                        jsonObject.getString("gender"));
                                userInfoHashMap.put(ShareConstants.PARAMS_IMAGEURL,
                                        jsonObject.getString("figureurl_qq_2"));
                                userInfoHashMap
                                        .put(ShareConstants.PARAMS_USERID, mTencent.getOpenId());

                                if (mPlatformActionListener != null) {
                                    mPlatformActionListener
                                            .onComplete(userInfoHashMap);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (mPlatformActionListener != null) {
                                    mPlatformActionListener
                                            .onError();
                                }
                            }


                        }

                        @Override
                        public void onError(UiError uiError) {
                            if (mPlatformActionListener != null) {
                                mPlatformActionListener
                                        .onError();
                            }
                        }

                        @Override
                        public void onCancel() {
                            if (mPlatformActionListener != null) {
                                mPlatformActionListener
                                        .onCancel();
                            }
                        }
                    });
                }

                @Override
                public void onError(UiError uiError) {
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener
                                .onError();
                    }
                }

                @Override
                public void onCancel() {
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener
                                .onCancel();
                    }
                }
            });

        } else {
            mTencent.logout(mContext);
        }
    }
}


