package com.qylk.mp.bus.utils;

import android.graphics.Bitmap;
import android.os.Build;

public class MemoryUtil {

    public static int getBitmapMemory(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
    }
}
