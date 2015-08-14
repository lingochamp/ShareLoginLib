package com.liulishuo.share.util;

import rx.Subscriber;

/**
 * Created by echo on 5/20/15.
 */
public class DummySubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T t) {

    }
}
