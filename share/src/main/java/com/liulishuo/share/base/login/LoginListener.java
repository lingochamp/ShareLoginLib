package com.liulishuo.share.base.login;

import com.liulishuo.share.base.BaseListener;

/**
 * @author Jack Tony
 * @date 2015/7/22
 */
public interface LoginListener extends BaseListener {

    void onLoginComplete(String uId, String accessToken, long expiresIn, String wholeData);

}
