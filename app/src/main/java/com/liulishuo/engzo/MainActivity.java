package com.liulishuo.engzo;

import com.liulishuo.share.ShareBlock;
import com.liulishuo.share.model.ILoginManager;
import com.liulishuo.share.model.IShareManager;
import com.liulishuo.share.model.PlatformActionListener;
import com.liulishuo.share.model.ShareContentText;
import com.liulishuo.share.model.ShareContentWebpage;
import com.liulishuo.share.qq.QQLoginManager;
import com.liulishuo.share.qq.QQShareManager;
import com.liulishuo.share.wechat.WechatLoginManager;
import com.liulishuo.share.wechat.WechatShareManager;
import com.liulishuo.share.weibo.WeiboLoginManager;
import com.liulishuo.share.weibo.WeiboShareManager;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity {

    private SsoHandler mSsoHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final View rooView = findViewById(R.id.view);

        ShareBlock.getInstance().initShare(" ", " ", "",
                "");

        findViewById(R.id.share_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IShareManager iShareManager = new WechatShareManager(MainActivity.this);
                iShareManager
                        .share(new ShareContentWebpage("", "hello", "http://www.liulishuo.com",
                                        getImagePath(rooView)),
                                WechatShareManager.WEIXIN_SHARE_TYPE_TALK);

            }
        });

        findViewById(R.id.share_friends_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IShareManager iShareManager = new WechatShareManager(MainActivity.this);
                iShareManager.share(
                        new ShareContentWebpage("hello", "", "http://www.liulishuo.com",
                                getImagePath(rooView)),
                        WechatShareManager.WEIXIN_SHARE_TYPE_FRENDS);

            }
        });

        findViewById(R.id.share_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IShareManager iShareManager = new WeiboShareManager(MainActivity.this);
                iShareManager
                        .share(new ShareContentText("test"), WeiboShareManager.WEIBO_SHARE_TYPE);
            }
        });

        findViewById(R.id.share_qZone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IShareManager iShareManager = new QQShareManager(MainActivity.this);
                iShareManager.share(
                        new ShareContentWebpage("英语流利说", "test", "http://www.liulishuo.com",
                                getImagePath(rooView)),
                        QQShareManager.QZONE_SHARE_TYPE);

            }
        });

        findViewById(R.id.login_wechat_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILoginManager iLoginManager = new WechatLoginManager
                        (MainActivity.this);
                iLoginManager.login(new PlatformActionListener() {
                    @Override
                    public void onComplete(HashMap<String, Object> userInfo) {
                        //TODO
                    }

                    @Override
                    public void onError() {
                        //TODO
                    }

                    @Override
                    public void onCancel() {
                        //TODO
                    }
                });
            }
        });

        findViewById(R.id.login_weibo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILoginManager iLoginManager = new WeiboLoginManager
                        (MainActivity.this);
                iLoginManager.login(new PlatformActionListener() {
                    @Override
                    public void onComplete(HashMap<String, Object> userInfo) {
                        //TODO
                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });

        findViewById(R.id.login_qzone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILoginManager iLoginManager = new QQLoginManager(MainActivity.this);
                iLoginManager.login(new PlatformActionListener() {
                    @Override
                    public void onComplete(HashMap<String, Object> userInfo) {

                    }

                    @Override
                    public void onError() {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSsoHandler = WeiboLoginManager.getSsoHandler();
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
