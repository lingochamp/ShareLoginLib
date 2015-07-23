package com.liulishuo.engzo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.share.IShareManager;
import com.liulishuo.share.base.share.ShareContentText;
import com.liulishuo.share.base.share.ShareContentWebpage;
import com.liulishuo.share.base.share.ShareStateListener;
import com.liulishuo.share.qq.QQLoginManager;
import com.liulishuo.share.qq.QQShareManager;
import com.liulishuo.share.wechat.WechatLoginManager;
import com.liulishuo.share.wechat.WechatShareManager;
import com.liulishuo.share.weibo.WeiboLoginManager;
import com.liulishuo.share.weibo.WeiboShareManager;
import com.tencent.connect.common.Constants;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends Activity {

    private static int currentWay;

    private static final int LOGIN_BY_WEIBO = 20;

    public static final int SHARE_BY_WEIBO = 21;

        private static final int LOGIN_BY_WECHAT = 31;
    
    public static final int SHARE_BY_WEICHAT = 32;

    private static final int LOGIN_BY_QQ = 10;

    public static final int SHARE_BY_QQ = 11;

    private ShareStateListener mShareListener = new ShareStateListener() {
        @Override
        public void onComplete() {
            Toast.makeText(getBaseContext(), "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(String msg) {
            Toast.makeText(getBaseContext(), "分享失败，出错信息：" + msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getBaseContext(), "取消分享", Toast.LENGTH_SHORT).show();
        }
    };

    private LoginListener mLoginListener = new LoginListener() {
        public static final String TAG = "LoginListener";

        @Override
        public void onLoginComplete(String uId, String accessToken, long expiresIn) {
            Toast.makeText(getBaseContext(), "登录成功", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "uid = " + uId);
            Log.d(TAG, "accessToken = " + accessToken);
            Log.d(TAG, "expires_in = " + expiresIn);
        }

        @Override
        public void onError(String msg) {
            Toast.makeText(getBaseContext(), "登录失败,失败信息：" + msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(getBaseContext(), "取消登录", Toast.LENGTH_SHORT).show();
        }
    };

    

    QQLoginManager mQQLoginManager;

    QQShareManager qqShareManager;

    WeiboLoginManager weiboLoginManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View rooView = findViewById(R.id.view);

        ShareBlock.getInstance()
                .initAppName("TestApp")
                .initQQ(QQ_APPID, QQ_SCOPE)
                .initWechat(WECHAT_APPID, WECHAT_SECRET)
                .initWeibo(WEIBO_APPID, WEIBO_REDIRECT_URL, WEIBO_SCOPE);

        findViewById(R.id.share_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWay = SHARE_BY_WEICHAT;

                IShareManager iShareManager = new WechatShareManager(MainActivity.this);
                iShareManager
                        .share(new ShareContentWebpage("", "hello", "http://www.liulishuo.com", getImagePath(rooView)),
                                WechatShareManager.WEIXIN_SHARE_TYPE_TALK
                                , mShareListener);
            }
        });

        findViewById(R.id.share_friends_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWay = SHARE_BY_WEICHAT;
                IShareManager iShareManager = new WechatShareManager(MainActivity.this);
                iShareManager.share(
                        new ShareContentWebpage("hello", "", "http://www.liulishuo.com",
                                getImagePath(rooView)),
                        WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS
                        , mShareListener);

            }
        });

        findViewById(R.id.login_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWay = LOGIN_BY_WECHAT;
                ILoginManager iLoginManager = new WechatLoginManager
                        (MainActivity.this);
                iLoginManager.login(mLoginListener);
            }
        });

        ///////////////////////////// Weibo ///////////////////////////////

        findViewById(R.id.login_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWay = LOGIN_BY_WEIBO;
                weiboLoginManager = new WeiboLoginManager(MainActivity.this);
                weiboLoginManager.login(mLoginListener);
            }
        });

        findViewById(R.id.share_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWay = SHARE_BY_WEIBO;
                IShareManager iShareManager = new WeiboShareManager(MainActivity.this);
                iShareManager
                        .share(new ShareContentText("test"), WeiboShareManager.WEIBO_SHARE_TYPE, mShareListener);
            }
        });

        ///////////////////////////// QQ ///////////////////////////////

        // QQ登录
        findViewById(R.id.login_qq_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWay = LOGIN_BY_QQ;
                mQQLoginManager = new QQLoginManager(MainActivity.this);
                mQQLoginManager.login(mLoginListener);
            }
        });

        findViewById(R.id.share_qq_friend_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentWay = SHARE_BY_QQ;
                qqShareManager = new QQShareManager(MainActivity.this);

                ShareContentWebpage content = new ShareContentWebpage("title", "test", "http://www.baidu.com", getImagePath(rooView));
                qqShareManager.share(content, QQShareManager.QQ_SHARE_TYPE, mShareListener);
            }
        });

        findViewById(R.id.share_qZone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentWay = SHARE_BY_QQ;
                qqShareManager = new QQShareManager(MainActivity.this);
                ShareContentWebpage content = new ShareContentWebpage("title", "test", "http://www.baidu.com", getImagePath(rooView));
                qqShareManager.share(content, QQShareManager.QZONE_SHARE_TYPE, mShareListener);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (currentWay) {
            case LOGIN_BY_QQ:
                mQQLoginManager.handlerOnActivityResult(requestCode, resultCode, data);
                break;
            case SHARE_BY_QQ:
                qqShareManager.handlerOnActivityResult(requestCode, resultCode, data);
                break;
            case LOGIN_BY_WECHAT:
                
                break;
            case SHARE_BY_WEICHAT:
                
                break;
            case LOGIN_BY_WEIBO:
                weiboLoginManager.handlerOnActivityResult(requestCode, resultCode, data);
                break;
            case SHARE_BY_WEIBO:

                break;
            default:
                //fix the bug that qq sso failed.
                if (requestCode == Constants.REQUEST_API) {
                    if (resultCode == Constants.RESULT_LOGIN) {
                        mQQLoginManager.getTencent().handleLoginData(data, null);
                    }
                }
        }
    }

    /**
     * 截取对象是普通view
     */
    private String getImagePath(View view) {

        String imagePath =
                getPathTemp() + File.separator
                        + System.currentTimeMillis() + ".png";
        try {
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = view.getDrawingCache();
            if (bitmap != null) {
                FileOutputStream out = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
            }
        } catch (OutOfMemoryError ex) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return imagePath;
    }

    private String mPathTemp = "";

    /**
     * 临时文件地址 *
     */
    public String getPathTemp() {
        if (TextUtils.isEmpty(mPathTemp)) {
            mPathTemp = MainActivity.this.getExternalCacheDir() + File.separator + "temp";
            File dir = new File(mPathTemp);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        return mPathTemp;
    }


}
