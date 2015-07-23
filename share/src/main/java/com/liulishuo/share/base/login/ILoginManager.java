package com.liulishuo.share.base.login;

import android.support.annotation.NonNull;

/**
 * Created by echo on 5/21/15.
 */
public interface ILoginManager {

    void login(LoginListener listener);

    void getUserInfo(final @NonNull GetUserListener listener);
}
