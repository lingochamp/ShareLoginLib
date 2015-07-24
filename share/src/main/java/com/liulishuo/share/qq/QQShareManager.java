package com.liulishuo.share.qq;

import com.liulishuo.share.R;
import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.model.IShareManager;
import com.liulishuo.share.model.ShareContent;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by echo on 5/18/15.
 */
public class QQShareManager implements IShareManager{

    public static final int QZONE_SHARE_TYPE = 0;


    private String mAppId;

    private Tencent mTencent;

    private QQShare mQQShare;

    private Context mContext;


    public QQShareManager(Context context) {
        mAppId = ShareBlock.getInstance().getQQAppId();
        mContext = context;
        if (!TextUtils.isEmpty(mAppId)) {
            mTencent = Tencent.createInstance(mAppId, context);
            mQQShare = new QQShare(context, mTencent.getQQToken());
        }
    }


    private void shareWebPage(Activity activity, ShareContent shareContent) {
        Bundle params = new Bundle();
        shareWebPageQzone(activity, shareContent, params);
    }


    private void shareWebPageQzone(Activity activity, ShareContent shareContent, Bundle params) {
//        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareContent.getTitle());
//        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent.getContent());
//        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE,
//                QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
//        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareContent.getURL());
//        ArrayList<String> imageUrls = new ArrayList<String>();
//        imageUrls.add(shareContent.getImageUrl());
//        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
//        doShareToQzone(activity, params);


        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareContent.getTitle());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareContent.getURL());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent.getContent());
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareContent.getImageUrl());
        doShareToQQ(activity, params);

    }


    /**
     * 用异步方式启动分享
     * @param params
     */
    private void doShareToQQ(final Activity activity,final Bundle params) {
        // QQ分享要在主线程做
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mQQShare != null) {
                    mQQShare.shareToQQ(activity, params, iUiListener);
                }
            }
        });
    }


    private final IUiListener iUiListener = new IUiListener() {
        @Override
        public void onCancel() {
            Toast.makeText(mContext, mContext.getString(
                    R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onComplete(Object response) {
            Toast.makeText(mContext, mContext.getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(mContext, mContext.getString(
                    R.string.share_failed), Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    public void share(ShareContent shareContent, int shareType) {

        shareWebPage((Activity) mContext, shareContent);
    }
}
