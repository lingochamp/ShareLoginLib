package com.liulishuo.share.weibo;

import com.liulishuo.share.R;
import com.liulishuo.share.data.ShareConstants;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.model.IShareManager;
import com.liulishuo.share.util.ShareUtil;
import com.liulishuo.share.model.ShareContent;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.mm.sdk.openapi.SendMessageToWX;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * Created by echo on 5/18/15.
 */
public class WeiboShareManager implements IShareManager {


    private static String mSinaAppKey;

    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    public static final String REDIRECT_URL = "http://www.liulishuo.com";

    private Context mContext;

    public static final int WEIBO_SHARE_TYPE = 0;


    /**
     * 微博分享的接口实例
     */
    private IWeiboShareAPI mSinaAPI;


    public WeiboShareManager(Context context) {
        mContext = context;
        mSinaAppKey = ShareBlock.getInstance().getWeiboAppId();
        if (!TextUtils.isEmpty(mSinaAppKey)) {
            // 创建微博 SDK 接口实例
            mSinaAPI = WeiboShareSDK.createWeiboAPI(context, mSinaAppKey);
            mSinaAPI.registerApp();
        }
    }


    private void shareText(ShareContent shareContent) {

        //初始化微博的分享消息
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        weiboMultiMessage.textObject = getTextObj(shareContent.getContent());
        //初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = ShareUtil.buildTransaction("sinatext");
        request.multiMessage = weiboMultiMessage;
        allInOneShare(mContext, request);

    }

    private void sharePicture(ShareContent shareContent) {

        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        weiboMultiMessage.imageObject = getImageObj(shareContent.getImageUrl());
        //初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = ShareUtil.buildTransaction("sinapic");
        request.multiMessage = weiboMultiMessage;
        allInOneShare(mContext, request);
    }

    private void shareWebPage(ShareContent shareContent) {

        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        weiboMultiMessage.textObject = getTextObj(shareContent.getContent());
        weiboMultiMessage.imageObject = getImageObj(shareContent.getImageUrl());
        // 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = ShareUtil.buildTransaction("sinawebpage");
        request.multiMessage = weiboMultiMessage;
        allInOneShare(mContext, request);

    }


    private void shareMusic(ShareContent shareContent) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        weiboMultiMessage.mediaObject = getMusicObj(shareContent);
        //初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = ShareUtil.buildTransaction("sinamusic");
        request.multiMessage = weiboMultiMessage;
        allInOneShare(mContext, request);
    }


    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(String imageUrl) {
        ImageObject imageObject = new ImageObject();
        Bitmap bmp = BitmapFactory.decodeFile(imageUrl);
        imageObject.setImageObject(bmp);
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj(ShareContent shareContent) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareContent.getTitle();
        mediaObject.description = shareContent.getContent();

        // 设置 Bitmap 类型的图片到视频对象里
        Bitmap bmp = ShareUtil.extractThumbNail(shareContent.getImageUrl(), 150, 150, true);
        mediaObject.setThumbImage(bmp);
        mediaObject.actionUrl = shareContent.getURL();
        mediaObject.defaultText = shareContent.getContent();
        return mediaObject;
    }


    /**
     * 创建多媒体（音乐）消息对象。
     *
     * @return 多媒体（音乐）消息对象。
     */
    private MusicObject getMusicObj(ShareContent shareContent) {
        // 创建媒体消息
        MusicObject musicObject = new MusicObject();
        musicObject.identify = Utility.generateGUID();
        musicObject.title = shareContent.getTitle();
        musicObject.description = shareContent.getContent();

        // 设置 Bitmap 类型的图片到视频对象里
        Bitmap bmp = BitmapFactory.decodeFile(shareContent.getImageUrl());
        musicObject.setThumbImage(bmp);
        musicObject.actionUrl = shareContent.getURL();
        musicObject.dataUrl = REDIRECT_URL;
        musicObject.dataHdUrl = REDIRECT_URL;
        musicObject.duration = 10;
        musicObject.defaultText = shareContent.getContent();
        return musicObject;
    }


    private void allInOneShare(final Context context, SendMultiMessageToWeiboRequest request) {

        AuthInfo authInfo = new AuthInfo(context, mSinaAppKey, REDIRECT_URL, SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context);
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }

        mSinaAPI.sendRequest((Activity) context, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
                Toast.makeText(context, context.getString(
                        R.string.share_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(context, newToken);
                Toast.makeText(context, context.getString(
                        R.string.share_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, context.getString(
                        R.string.share_cancel), Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void share(ShareContent shareContent, int shareType) {

        if (mSinaAPI == null) {
            return;
        }

        switch (shareContent.getShareWay()) {
            case ShareConstants.SHARE_WAY_TEXT:
                shareText(shareContent);
                break;
            case ShareConstants.SHARE_WAY_PIC:
                sharePicture(shareContent);
                break;
            case ShareConstants.SHARE_WAY_WEBPAGE:
                shareWebPage(shareContent);
                break;
            case ShareConstants.SHARE_WAY_MUSIC:
                shareMusic(shareContent);
                break;
        }


    }
}
