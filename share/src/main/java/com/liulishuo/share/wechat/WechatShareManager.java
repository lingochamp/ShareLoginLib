package com.liulishuo.share.wechat;

import com.liulishuo.share.R;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.share.ShareConstants;
import com.liulishuo.share.base.share.IShareManager;
import com.liulishuo.share.base.share.ShareStateListener;
import com.liulishuo.share.base.share.ShareContent;
import com.liulishuo.share.util.ShareUtil;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXMusicObject;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by echo on 5/18/15.
 */
public class WechatShareManager implements IShareManager {

    /**
     * friends 发送的目标场景，表示发送到会话
     */
    public static final int WEIXIN_SHARE_TYPE_TALK = SendMessageToWX.Req.WXSceneSession;

    /**
     * friends TimeLine 发送的目标场景，表示发送朋友圈
     */
    public static final int WEIXIN_SHARE_TYPE_FRENDS = SendMessageToWX.Req.WXSceneTimeline;


    private static final int THUMB_SIZE = 150;


    private Context mContext;


    private IWXAPI mIWXAPI;


    private String mWeChatAppId;

    private static ShareStateListener mShareStateListener;

    public WechatShareManager(Context context) {
        mContext = context;
        mWeChatAppId = ShareBlock.getInstance().getWechatAppId();
        if (!TextUtils.isEmpty(mWeChatAppId)) {
            initWeixinShare(context);
        }

    }

    private void initWeixinShare(Context context) {
        mIWXAPI = WXAPIFactory.createWXAPI(context, mWeChatAppId, true);
        if (!mIWXAPI.isWXAppInstalled()) {
            Toast.makeText(context, context.getString(R.string.share_install_wechat_tips), Toast.LENGTH_SHORT).show();
        } else {
            mIWXAPI.registerApp(mWeChatAppId);
        }
    }


    private void shareText(int shareType, ShareContent shareContent) {
        String text = shareContent.getContent();
        //初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;
        //用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        //transaction字段用于唯一标识一个请求
        req.transaction = ShareUtil.buildTransaction("textshare");
        req.message = msg;
        //发送的目标场景， 可以选择发送到会话 WXSceneSession 或者朋友圈 WXSceneTimeline。 默认发送到会话。
        req.scene = shareType;
        mIWXAPI.sendReq(req);
    }


    private void sharePicture(int shareType, ShareContent shareContent) {
        Bitmap bmp = ShareUtil.extractThumbNail(shareContent.getImageUrl(), THUMB_SIZE, THUMB_SIZE, true);
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        if (bmp != null) {
            msg.thumbData = ShareUtil.bmpToByteArray(bmp);  //设置缩略图
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("imgshareappdata");
        req.message = msg;
        req.scene = shareType;
        mIWXAPI.sendReq(req);
    }

    private void shareWebPage(int shareType, ShareContent shareContent) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getURL();
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        Bitmap bmp = ShareUtil.extractThumbNail(shareContent.getImageUrl(), THUMB_SIZE, THUMB_SIZE, true);
        if (bmp == null) {
            Toast.makeText(mContext, mContext.getString(R.string.share_pic_empty), Toast.LENGTH_SHORT).show();
        } else {
            msg.thumbData = ShareUtil.bmpToByteArray(bmp);
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("webpage");
        req.message = msg;
        req.scene = shareType;
        mIWXAPI.sendReq(req);
    }


    private void shareMusic(int shareType, ShareContent shareContent) {
        WXMusicObject music = new WXMusicObject();
        //Str1+"#wechat_music_url="+str2 ;str1是网页地址，str2是音乐地址。

        music.musicUrl = shareContent.getURL() + "#wechat_music_url=" + shareContent.getMusicUrl();
        WXMediaMessage msg = new WXMediaMessage(music);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        Bitmap thumb = ShareUtil.extractThumbNail(shareContent.getImageUrl(), THUMB_SIZE, THUMB_SIZE, true);

        if (thumb == null) {
            Toast.makeText(mContext, mContext.getString(R.string.share_pic_empty), Toast.LENGTH_SHORT).show();
        } else {
            msg.thumbData = ShareUtil.bmpToByteArray(thumb);
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("music");
        req.message = msg;
        req.scene = shareType;
        mIWXAPI.sendReq(req);
    }

    private boolean shareApp(int shareType, ShareContent shareContent) {
        final WXAppExtendObject appdata = new WXAppExtendObject();
        appdata.extInfo = "";
        
        final WXMediaMessage msg = new WXMediaMessage();
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();
        Bitmap thumb = ShareUtil.extractThumbNail(shareContent.getImageUrl(), THUMB_SIZE, THUMB_SIZE, true);

        if (thumb == null) {
            Toast.makeText(mContext, mContext.getString(R.string.share_pic_empty), Toast.LENGTH_SHORT).show();
        } else {
            msg.thumbData = ShareUtil.bmpToByteArray(thumb);
        }
        msg.mediaObject = appdata;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("app"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = shareType;
        return mIWXAPI.sendReq(req);
    }

    @Override
    public void share(ShareContent content, int shareType, @NonNull ShareStateListener listener) {
        mShareStateListener = listener;
        switch (content.getShareWay()) {
            case ShareConstants.SHARE_WAY_TEXT:
                shareText(shareType, content);
                break;
            case ShareConstants.SHARE_WAY_PIC:
                sharePicture(shareType, content);
                break;
            case ShareConstants.SHARE_WAY_WEBPAGE:
                shareWebPage(shareType, content);
                break;
            case ShareConstants.SHARE_WAY_MUSIC:
                shareMusic(shareType, content);
                break;
            case ShareConstants.SHARE_WEY_APP:
                shareApp(shareType, content);
                break;
        }
    }

    /**
     * 解析分享到微信的结果
     */
    public static void parseShare(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                // 分享成功
                mShareStateListener.onComplete();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                // 用户取消
                mShareStateListener.onCancel();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                // 用户拒绝授权
                mShareStateListener.onError("用户拒绝授权");
                break;
            case BaseResp.ErrCode.ERR_SENT_FAILED:
                // 发送失败
                mShareStateListener.onError("发送失败");
                break;
            case BaseResp.ErrCode.ERR_COMM:
                // 一般错误
                mShareStateListener.onError("一般错误");
                break;
            default:
                mShareStateListener.onError("未知错误");
        }
    }
    
}
