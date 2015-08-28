# ShareLoginLib
ShareLoginLib likes simple sharesdk or umeng in China . It is a tool to help developers to share their content (image , text or music ) to WeChat,Weibo and QQ .

# Including in your project
-------------------------

```groovy
compile 'com.echodjb.shareloginlib:share:0.5'
```


# How to use (参考MainActivity)
-------------------------

#### 1.初始化申请的第三方key

```java
   ShareBlock.getInstance().initShare(wechatAppid, weiboId, qqId,
                   wechatSecret);
```

#### 2.初始化微博回调地址

```java
      ShareBlock.getInstance().initWeiboRedriectUrl(weiboRedriectUrl);
```

#### 3.分享到微信
```java
  IShareManager iShareManager = new WechatShareManager(context);
  iShareManager.share(new ShareContentWebpage("title", "content", "dataUrl",
  "imageUrl",WechatShareManager.WEIXIN_SHARE_TYPE_TALK);
```
#### 4.微信登录

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

# Demo
-------------------------

![screenshot](http://7xjb6z.com1.z0.glb.clouddn.com/screenshot.png)

Libraries
---------
 * Retrofit - http://square.github.io/retrofit
 * OkHttp - http://square.github.io/okhttp
 * RxJava - https://github.com/ReactiveX/RxJava

# LICENCE
-------------------------

  The MIT License (MIT)

  Copyright (c) 2015 LingoChamp Inc.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
