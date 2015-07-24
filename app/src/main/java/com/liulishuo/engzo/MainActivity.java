package com.liulishuo.engzo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.base.login.ILoginManager;
import com.liulishuo.share.base.login.LoginListener;
import com.liulishuo.share.base.share.IShareManager;
import com.liulishuo.share.base.share.ShareContentWebpage;
import com.liulishuo.share.base.share.ShareStateListener;
import com.liulishuo.share.qq.QQLoginManager;
import com.liulishuo.share.qq.QQShareManager;
import com.liulishuo.share.wechat.WechatLoginManager;
import com.liulishuo.share.wechat.WechatShareManager;
import com.liulishuo.share.weibo.WeiboLoginManager;
import com.liulishuo.share.weibo.WeiboShareManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 步骤：
 * 1.添加混淆参数
 * 2.在包中放入微信必须的activity
 * 3.配置manifest中的activity
 *
 * 分享：
 * 1.qq支持本地图片地址+图片url
 * 2.weibo支持bitmap和本地图片（但不推荐使用本地图片的url形式）
 * 3.wechat支持bitmap
 */
public class MainActivity extends Activity {

    ILoginManager mCurrentLoginManager;

    IShareManager mCurrentShareManager;

    public static final String TAG = "MainActivity";

    public static final String QQ_APPID = " ";

    public static final String QQ_SCOPE = " ";

    public static final String WEIBO_APPID = " ";

    public static final String WEIBO_SCOPE = " ";

    public static final String WEIBO_REDIRECT_URL = " ";

    public static final String WECHAT_APPID = " ";

    public static final String WECHAT_SECRET = " ";

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Drawable drawable = getResources().getDrawable(R.drawable.kale);
        bitmap = ((BitmapDrawable) drawable).getBitmap();

        final View rooView = findViewById(R.id.view);

        ShareBlock.getInstance()
                .initAppName("TestAppName")
                .initQQ(QQ_APPID, QQ_SCOPE)
                .initWechat(WECHAT_APPID, WECHAT_SECRET)
                .initWeibo(WEIBO_APPID, WEIBO_REDIRECT_URL, WEIBO_SCOPE);

        // 微信分享到回话
        findViewById(R.id.share_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentShareManager = new WechatShareManager(MainActivity.this);
                mCurrentShareManager.share(
                        new ShareContentWebpage("", "hello", "http://www.liulishuo.com", bitmap),
                        WechatShareManager.WEIXIN_SHARE_TYPE_TALK
                        , mShareListener);
            }
        });

        // 微信分享到朋友圈
        findViewById(R.id.share_friends_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentShareManager = new WechatShareManager(MainActivity.this);
                mCurrentShareManager.share(
                        new ShareContentWebpage("hello", "", "http://www.liulishuo.com", bitmap)
                        , WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS
                        , mShareListener);
            }
        });

        // 微信登录
        findViewById(R.id.login_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentLoginManager = new WechatLoginManager(MainActivity.this);
                mCurrentLoginManager.login(mLoginListener);
            }
        });

        ///////////////////////////// Weibo ///////////////////////////////

        findViewById(R.id.login_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentLoginManager = new WeiboLoginManager(MainActivity.this);
                mCurrentLoginManager.login(mLoginListener);
            }
        });

        findViewById(R.id.share_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentShareManager = new WeiboShareManager(MainActivity.this);
//                mCurrentShareManager.share(new ShareContentText("test"), WeiboShareManager.WEIBO_SHARE_TYPE, mShareListener);
                mCurrentShareManager.share(
                        new ShareContentWebpage("hello", "lalala", "http://www.liulishuo.com", bitmap)
                        , WeiboShareManager.WEIBO_SHARE_TYPE, mShareListener);
            }
        });

        ///////////////////////////// QQ ///////////////////////////////

        // QQ登录
        findViewById(R.id.login_qq_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentLoginManager = new QQLoginManager(MainActivity.this);
                mCurrentLoginManager.login(mLoginListener);
            }
        });

        findViewById(R.id.share_qq_friend_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentShareManager = new QQShareManager(MainActivity.this);
                mCurrentShareManager.share(
                        new ShareContentWebpage("title", "test", "http://www.baidu.com",
                                "https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superplus/img/logo_white_ee663702.png")
                        , QQShareManager.QQ_SHARE_TYPE, mShareListener);
            }
        });

        findViewById(R.id.share_qZone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentShareManager = new QQShareManager(MainActivity.this);
//                ShareContentWebpage content = new ShareContentWebpage("title", "test", "http://www.baidu.com", getImagePath(rooView));
                ShareContentWebpage content = new ShareContentWebpage("title", "test", "http://www.baidu.com", getImagePath(rooView));
                mCurrentShareManager.share(content, QQShareManager.QZONE_SHARE_TYPE, mShareListener);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareBlock.handlerOnActivityResult(mCurrentLoginManager, mCurrentShareManager, requestCode, resultCode, data);
    }


    private LoginListener mLoginListener = new LoginListener() {
        public static final String TAG = "LoginListener";

            @Override
            public void onLoginComplete(String uId, String accessToken, long expiresIn) {
                Log.d(TAG, "uid = " + uId);
                Log.d(TAG, "accessToken = " + accessToken);
                Log.d(TAG, "expires_in = " + expiresIn);
                // 如果是微信登录，这个回调是在新线程中的，不是在主线程中。所以请不要进行ui操作！
                Log.d(TAG, "登录成功");
                Toast.makeText(getBaseContext(), "登录成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String msg) {
                Toast.makeText(getBaseContext(), "登录失败,失败信息：" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "取消登录");
                Toast.makeText(getBaseContext(), "取消登录", Toast.LENGTH_SHORT).show();
            }
    };

    private ShareStateListener mShareListener = new ShareStateListener() {
            @Override
            public void onComplete() {
                Log.d(TAG, "分享成功");
                Toast.makeText(getBaseContext(), "分享成功", Toast.LENGTH_SHORT).show();
            }

        @Override
        public void onError(String msg) {
            Log.d(TAG, "分享失败，出错信息：" + msg);
            Toast.makeText(getBaseContext(), "分享失败，出错信息：" + msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "取消分享");
                Toast.makeText(getBaseContext(), "取消分享", Toast.LENGTH_SHORT).show();
            }
    };


    /**
     * 截取对象是普通view，得到这个view在本地存放的地址
     */
    private String getImagePath(View view) {

        String imagePath = getPathTemp() + File.separator + System.currentTimeMillis() + ".png";
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
