package com.liulishuo.share.base.login;

import com.liulishuo.share.base.BaseListener;

import java.util.HashMap;

/**
 * @author Jack Tony
 * @date 2015/7/22
 */
public interface GetUserListener extends BaseListener {

    /**
     * 登录成功
     * @param userInfo
     */
    void onComplete(HashMap<String, String> userInfo);
}
