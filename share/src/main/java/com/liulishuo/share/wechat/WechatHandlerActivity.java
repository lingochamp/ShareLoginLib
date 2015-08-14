package com.liulishuo.share.wechat;

import com.liulishuo.share.R;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.api.WechatApiService;
import com.liulishuo.share.data.ShareConstants;
import com.liulishuo.share.model.PlatformActionListener;
import com.liulishuo.share.util.DummySubscriber;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendAuth;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by echo on 5/19/15.
 *
 *
 */
public class WechatHandlerActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI mIWXAPI;

    private PlatformActionListener mPlatformActionListener;

    private static final String API_URL = "https://api.weixin.qq.com";

    /**
     * BaseResp的getType函数获得的返回值，1:第三方授权， 2:分享
     */
    private static final int TYPE_LOGIN = 1;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = WechatHandlerActivity.this;
        mIWXAPI = WechatLoginManager.getIWXAPI();
        if(mIWXAPI!=null){
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(mIWXAPI!=null){
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {
        finish();
    }

    @Override
    public void onResp(BaseResp resp) {


        mPlatformActionListener = WechatLoginManager
                .getPlatformActionListener();
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:

                if (resp.getType() == TYPE_LOGIN) {
                    final String code = ((SendAuth.Resp) resp).token;

                    RequestInterceptor requestInterceptor = new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addQueryParam("appid",
                                    ShareBlock.getInstance().getWechatAppId());
                            request.addQueryParam("secret",
                                    ShareBlock.getInstance().getWechatSecret());
                            request.addQueryParam("code", code);
                            request.addQueryParam("grant_type", "authorization_code");
                        }
                    };

                    getApiService(requestInterceptor).getAccessToken()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DummySubscriber<Response>() {
                                @Override
                                public void onError(Throwable e) {
                                    if (mPlatformActionListener != null) {
                                        mPlatformActionListener
                                                .onError();
                                    }
                                }

                                @Override
                                public void onNext(Response response) {
                                    try {
                                        String json = new String(
                                                ((TypedByteArray) response.getBody())
                                                        .getBytes());

                                        JSONObject jsonObject = new JSONObject(json);
                                        final String accessToken = jsonObject
                                                .getString("access_token");
                                        final String openId = jsonObject.getString("openid");

                                        RequestInterceptor requestInterceptor
                                                = new RequestInterceptor() {
                                            @Override
                                            public void intercept(RequestFacade request) {
                                                request.addQueryParam("access_token", accessToken);
                                                request.addQueryParam("openid", openId);
                                            }
                                        };
                                        getApiService(requestInterceptor).getWechatUserInfo()
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new DummySubscriber<Response>() {
                                                    @Override
                                                    public void onError(Throwable e) {
                                                        if (mPlatformActionListener != null) {
                                                            mPlatformActionListener
                                                                    .onError();
                                                        }
                                                    }

                                                    @Override
                                                    public void onNext(Response response) {
                                                        try {
                                                            String json = new String(
                                                                    ((TypedByteArray) response
                                                                            .getBody()).getBytes());

                                                            JSONObject jsonObject = new JSONObject(
                                                                    json);

                                                            HashMap<String, Object> userInfoHashMap
                                                                    = new HashMap<String, Object>();
                                                            userInfoHashMap
                                                                    .put(ShareConstants.PARAMS_NICK_NAME,
                                                                            jsonObject.getString(
                                                                                    "nickname"));
                                                            userInfoHashMap
                                                                    .put(ShareConstants.PARAMS_SEX,
                                                                            jsonObject.getInt(
                                                                                    "sex"));
                                                            userInfoHashMap
                                                                    .put(ShareConstants.PARAMS_IMAGEURL,
                                                                            jsonObject.getString(
                                                                                    "headimgurl"));
                                                            userInfoHashMap
                                                                    .put(ShareConstants.PARAMS_USERID,
                                                                            jsonObject
                                                                                    .getString(
                                                                                            "unionid"));
                                                            if (mPlatformActionListener != null) {
                                                                mPlatformActionListener
                                                                        .onComplete(
                                                                                userInfoHashMap);
                                                            }

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            onError(e);
                                                        }
                                                    }
                                                });

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        onError(e);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(mContext, mContext.getString(
                            R.string.share_success), Toast.LENGTH_SHORT).show();
                }

                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:

                if (resp.getType() == TYPE_LOGIN) {
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener
                                .onCancel();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(
                            R.string.share_cancel), Toast.LENGTH_SHORT).show();
                }

                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                if (resp.getType() == TYPE_LOGIN) {
                    if (mPlatformActionListener != null) {
                        mPlatformActionListener
                                .onError();
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(
                            R.string.share_failed), Toast.LENGTH_SHORT).show();
                }

                break;
        }
        finish();
    }

    private WechatApiService getApiService(RequestInterceptor requestInterceptor) {
        return new RestAdapter.Builder()
                .setEndpoint(API_URL)
                .setRequestInterceptor(requestInterceptor)
                .build().create(WechatApiService.class);
    }
}
