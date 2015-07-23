package com.liulishuo.share.wechat;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by echo on 5/19/15.
 * 用来处理微信登录、微信分享的activity。这里真不知道微信非要个activity干嘛，愚蠢的设计。
 * 参考文档:https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&id=open1419317853&lang=zh_CN
 */
public class WechatHandlerActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI mIWXAPI;

    /**
     * BaseResp的getType函数获得的返回值，1:第三方授权， 2:分享
     */
    private static final int TYPE_LOGIN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIWXAPI = WechatLoginManager.getIWXAPI();
        if (mIWXAPI != null) {
            mIWXAPI.handleIntent(getIntent(), this);
        }
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mIWXAPI != null) {
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
        if (resp.getType() == TYPE_LOGIN && resp instanceof SendAuth.Resp) {
            onLoginResp((SendAuth.Resp) resp);
            WechatLoginManager.parseLoginResp((SendAuth.Resp) resp);
        } else {
            WechatShareManager.parseShare(resp);
        }
        finish();
    }

    /**
     * 得到最原始的resp，可以得到code
     */
    protected void onLoginResp(SendAuth.Resp resp) {

    }

}
