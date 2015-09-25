package com.qylk.base.utils;

public class HTTPUtils {
    public static boolean isUrl(String url) {
        return url != null && android.util.Patterns.WEB_URL.matcher(url).matches();
    }
}
