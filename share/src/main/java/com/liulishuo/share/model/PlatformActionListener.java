package com.liulishuo.share.model;

import java.util.HashMap;

/**
 * Created by echo on 5/20/15.
 */
public interface PlatformActionListener {

    /**
     * 登录成功
     * @param userInfo
     */
    void onComplete(HashMap<String, Object> userInfo);

    /**
     * 登录失败
     */
    void onError();

    /**
     * 取消登录
     */
    void onCancel();

}
