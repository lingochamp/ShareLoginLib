package com.liulishuo.share;

/**
 * Created by echo on 5/18/15.
 */
public class ShareBlock {


    private static ShareBlock mInstance;

    private ShareBlock() {
    }

    public static ShareBlock getInstance() {
        if (mInstance == null) {
            mInstance = new ShareBlock();
        }
        return mInstance;
    }

    private String mWechatAppId = "";
    private String mWeiboAppId  = "";
    private String mQQAppId = "";
    private String mWechatSecret= "";


    /**
     * init all config
     * @param wechatAppId
     * @param weiboAppId
     * @param qqAppId
     */
    public void initShare(String wechatAppId, String weiboAppId, String qqAppId,String wechatSecret){
        mWechatAppId = wechatAppId;
        mWeiboAppId = weiboAppId;
        mQQAppId = qqAppId;
        mWechatSecret = wechatSecret;

    }


    /**
     * init wechat config
     * @param wechatAppId
     * @param wechatSecret
     */
    public void initWechat(String wechatAppId,String wechatSecret){
        mWechatAppId = wechatAppId;
        mWechatSecret = wechatSecret;
    }


    /**
     * init weibo config
     * @param weiboAppId
     */
    public void initWeibo(String weiboAppId){

        mWeiboAppId = weiboAppId;
    }

    /**
     * init QQ config
     * @param qqAppId
     */
    public void initQQ(String qqAppId){

        mQQAppId = qqAppId;
    }



    public String getWechatAppId() {
        return mWechatAppId;
    }

    public String getWeiboAppId() {
        return mWeiboAppId;
    }

    public String getQQAppId() {
        return mQQAppId;
    }

    public String getWechatSecret() {
        return mWechatSecret;
    }
}
