# ShareLoginLib
ShareLoginLib likes simple sharesdk or umeng in China . It is a tool to help developers to share their content (image , text or music ) to WeChat,Weibo and QQ .

# How to use (参考MainActivity)

#### 1.分享到微信
```java
  IShareManager iShareManager = new WechatShareManager(context);
  iShareManager.share(new ShareContentWebpage("title", "content", "dataUrl",
  "imageUrl",WechatShareManager.WEIXIN_SHARE_TYPE_TALK);
 ```
#### 2.微信登录

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
