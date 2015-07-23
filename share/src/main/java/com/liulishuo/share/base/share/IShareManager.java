package com.liulishuo.share.base.share;

/**
 * Created by echo on 5/21/15.
 */
public interface IShareManager {

    void share(ShareContent shareContent, int shareType, ShareStateListener listener);

}
