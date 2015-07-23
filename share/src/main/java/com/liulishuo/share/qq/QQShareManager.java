package com.liulishuo.share.qq;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.share.IShareManager;
import com.liulishuo.share.base.share.ShareContent;
import com.liulishuo.share.base.share.ShareStateListener;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by echo on 5/18/15.
 */
public class QQShareManager implements IShareManager {

    public static final int QZONE_SHARE_TYPE = 0;
    public static final int QQ_SHARE_TYPE = 1;

    private Tencent mTencent;

    private Activity mActivity;

    private ShareStateListener mShareStateListener;

    public QQShareManager(Activity activity) {
        String appId = ShareBlock.getInstance().getQQAppId();
        mActivity = activity;
        if (!TextUtils.isEmpty(appId)) {
            mTencent = Tencent.createInstance(appId, activity);
        }
    }

    /**
     * 分享到QQ
     *
     * WTF...
     * http://wiki.open.qq.com/wiki/mobile/API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#1.13_.E5.88.86.E4.BA.AB.E6.B6.88.E6.81.AF.E5.88.B0QQ.EF.BC.88.E6.97.A0.E9.9C.80QQ.E7.99.BB.E5.BD.95.EF.BC.89
     *
     * QQShare.PARAM_TITLE 	        必填 	String 	分享的标题, 最长30个字符。
     * QQShare.SHARE_TO_QQ_KEY_TYPE 	必填 	Int 	分享的类型。图文分享(普通分享)填Tencent.SHARE_TO_QQ_TYPE_DEFAULT
     * QQShare.PARAM_TARGET_URL 	必填 	String 	这条分享消息被好友点击后的跳转URL。
     * QQShare.PARAM_SUMMARY 	        可选 	String 	分享的消息摘要，最长40个字。
     * QQShare.SHARE_TO_QQ_IMAGE_URL 	可选 	String 	分享图片的URL或者本地路径
     * QQShare.SHARE_TO_QQ_APP_NAME 	可选 	String 	手Q客户端顶部，替换“返回”按钮文字，如果为空，用返回代替
     * QQShare.SHARE_TO_QQ_EXT_INT 	可选 	Int 	分享额外选项，两种类型可选（默认是不隐藏分享到QZone按钮且不自动打开分享到QZone的对话框）：
     * QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN，分享时自动打开分享到QZone的对话框。
     * QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE，分享时隐藏分享到QZone按钮
     *
     * target必须是真实的可跳转链接才能跳到QQ = =！
     * 
     * 发送给QQ好友
     */
    private void shareToQQ(Activity activity, ShareContent shareContent) {
        /*final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "要分享的摘要");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://www.qq.com/news/1.html");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "测试应用222222");
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");*/
        
        final Bundle params = new Bundle();
        if (shareContent.getTitle() == null) {
            // 分享的是纯图片
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareContent.getImageUrl());
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        } else {
            // 分享的是图文
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, shareContent.getTitle());
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent.getContent());
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareContent.getURL());
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareContent.getImageUrl());
        } 
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, ShareBlock.getInstance().getAppName());
        
        mTencent.shareToQQ(activity, params, shareListener);
    }

    /**
     * 分享到QQ空间
     * 
     * @see "http://wiki.open.qq.com/wiki/Android_API%E8%B0%83%E7%94%A8%E8%AF%B4%E6%98%8E#1.14_.E5.88.86.E4.BA.AB.E5.88.B0QQ.E7.A9.BA.E9.97.B4.EF.BC.88.E6.97.A0.E9.9C.80QQ.E7.99.BB.E5.BD.95.EF.BC.89"
     *
     * QzoneShare.SHARE_TO_QQ_KEY_TYPE 	    选填      Int 	SHARE_TO_QZONE_TYPE_IMAGE_TEXT（图文）
     * QzoneShare.SHARE_TO_QQ_TITLE 	    必填      Int 	分享的标题，最多200个字符。
     * QzoneShare.SHARE_TO_QQ_SUMMARY 	    选填      String 	分享的摘要，最多600字符。
     * QzoneShare.SHARE_TO_QQ_TARGET_URL    必填      String 	需要跳转的链接，URL字符串。
     * QzoneShare.SHARE_TO_QQ_IMAGE_URL     选填      String 	分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片
     * （注：图片最多支持9张图片，多余的图片会被丢弃）。
     *
     * 注意:QZone接口暂不支持发送多张图片的能力，若传入多张图片，则会自动选入第一张图片作为预览图。多图的能力将会在以后支持。
     */
    public void shareToQzone(Activity activity, ShareContent content) {
        /*params.putString(QzoneShare.SHARE_TO_QQ_KEY_TYPE,SHARE_TO_QZONE_TYPE_IMAGE_TEXT );
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, "标题");//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "摘要");//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "跳转URL");//必填
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "图片链接ArrayList");*/
        
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, content.getTitle());
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, content.getContent());
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, content.getURL());
        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add(content.getImageUrl());
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        mTencent.shareToQzone(activity, params, shareListener);
    }
    
    private final IUiListener shareListener = new IUiListener() {
        
        @Override
        public void onCancel() {
            mShareStateListener.onCancel();
        }

        @Override
        public void onComplete(Object response) {
            mShareStateListener.onComplete();
        }

        @Override
        public void onError(UiError e) {
            mShareStateListener.onError(e.errorCode + " - " + e.errorMessage + " - " + e.errorDetail);
        }
    };

    @Override
    public void share(ShareContent shareContent, int shareType, @NonNull ShareStateListener listener) {
        mShareStateListener = listener;
        if (shareType == QQ_SHARE_TYPE) {
            shareToQQ(mActivity, shareContent);
        } else if (shareType == QZONE_SHARE_TYPE){
            shareToQzone(mActivity,shareContent);
        } 
    }

    public void handlerOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (mTencent != null) {
            mTencent.onActivityResult(requestCode, resultCode, data);
        }
    }
}
