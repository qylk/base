package com.qylk.mp.bus.utils;

public class HTTPUtils {
    public static boolean isUrl(String url) {
        return url != null && android.util.Patterns.WEB_URL.matcher(url).matches();
    }
}
