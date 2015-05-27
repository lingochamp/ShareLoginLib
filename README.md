# ShareLoginLib
ShareLoginLib likes simple sharesdk or umeng in China . It is a tool to help developers to share their content (image , text or music ) to WeChat,Weibo and QQ .

# Including in your project
-------------------------

```groovy
compile 'com.echodjb.shareloginlib:share:0.2'
```


# How to use (参考MainActivity)
-------------------------

#### 1.初始化申请的第三方key

```java
   ShareBlock.getInstance().initShare(wechatAppid, weiboId, qqId,
                   wechatSecret);
  ```

#### 2.分享到微信
```java
  IShareManager iShareManager = new WechatShareManager(context);
  iShareManager.share(new ShareContentWebpage("title", "content", "dataUrl",
  "imageUrl",WechatShareManager.WEIXIN_SHARE_TYPE_TALK);
 ```
#### 3.微信登录

```java
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
 ```

# 3.Demo
-------------------------

![screenshot](http://7xjb6z.com1.z0.glb.clouddn.com/screenshot.png)
