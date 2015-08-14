package com.liulishuo.share.wechat;

import com.liulishuo.share.R;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.data.ShareConstants;
import com.liulishuo.share.model.IShareManager;
import com.liulishuo.share.util.BitmapUtil;
import com.liulishuo.share.util.ShareUtil;
import com.liulishuo.share.model.ShareContent;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.widget.Toast;

import java.net.URL;

/**
 * Created by echo on 5/18/15.
 */
public class WechatShareManager implements IShareManager{


    /**
     * friends
     */
    public static final int WEIXIN_SHARE_TYPE_TALK = SendMessageToWX.Req.WXSceneSession;

    /**
     * friends TimeLine
     */
    public static final int WEIXIN_SHARE_TYPE_FRENDS = SendMessageToWX.Req.WXSceneTimeline;


    // 缩略图大小 = 116 微信里头xdpi就是以这个尺寸展示的, 并且这个尺寸平衡了大小与32k缩略图的限制
    private static final int THUMB_SIZE = 116;


    private Context mContext;


    private IWXAPI mIWXAPI;


    private String mWeChatAppId;


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
        }else{
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
        WXImageObject imgObj = new WXImageObject();
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("imgshareappdata");
        req.message = msg;
        req.scene = shareType;
        sendShare(shareContent.getImageUrl(), req);
    }


    private void shareWebPage(final int shareType, final ShareContent shareContent) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = shareContent.getURL();
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("webpage");
        req.message = msg;
        req.scene = shareType;
        sendShare(shareContent.getImageUrl(), req);
    }


    private void shareMusic(int shareType, ShareContent shareContent) {

        WXMusicObject music = new WXMusicObject();
        //Str1+"#wechat_music_url="+str2 ;str1是网页地址，str2是音乐地址。

        music.musicUrl =shareContent.getURL()+ "#wechat_music_url="+shareContent.getMusicUrl();
        WXMediaMessage msg = new WXMediaMessage(music);
        msg.title = shareContent.getTitle();
        msg.description = shareContent.getContent();

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = ShareUtil.buildTransaction("music");
        req.message = msg;
        req.scene = shareType;
        sendShare(shareContent.getImageUrl(), req);
    }

    @Override
    public void share(ShareContent content,int shareType) {
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
        }
    }

    private void sendShare(final String imageUrl, final SendMessageToWX.Req req) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap image = BitmapUtil.getBitmapFromUrl(imageUrl);
                    if (image != null) {
                        // todo image.length <= 10485760
                        if (req.message.mediaObject instanceof WXImageObject) {
                            req.message.mediaObject = new WXImageObject(image);
                        }
                        req.message.thumbData = ShareUtil.bmpToByteArray(BitmapUtil.scaleCenterCrop(image, THUMB_SIZE, THUMB_SIZE));
                    }
                    // 就算图片没有了 尽量能发出分享
                    mIWXAPI.sendReq(req);
                } catch (Throwable throwable) {

                }
            }
        }).start();
    }

}
