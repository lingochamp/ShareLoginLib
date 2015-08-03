package com.liulishuo.share.util;
/**
 * Created by echo on 5/18/15.
 */
public class ShareUtil {

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }

}
