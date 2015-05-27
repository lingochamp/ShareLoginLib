package com.liulishuo.share.api;


import retrofit.client.Response;
import retrofit.http.GET;
import rx.Observable;

/**
 * Created by echo on 5/20/15.
 */
public interface WechatApiService {


    /**
     * 通过code获取access_token
     * @return
     */
    @GET("/sns/oauth2/access_token")
    Observable<Response> getAccessToken();


    /**
     * 获取用户个人信息（UnionID机制）
     * @return
     */
    @GET("/sns/userinfo")
    Observable<Response> getWechatUserInfo();

}
