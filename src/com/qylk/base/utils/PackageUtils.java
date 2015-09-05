package com.qylk.mp.bus.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.lang.reflect.Field;


public class PackageUtils {

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            versionName = info.versionName;
        } catch (Exception e) {
        }
        return versionName;
    }

    /**
     * 获取手机的硬件信息
     */
    public static String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        /**
         * 通过反射获取系统的硬件信息 获取私有的信息
         */
        try {
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    /**
     * 获得android设备-唯一标识，android2.2 之前无法稳定运行.
     */
    public static String getDeviceId(Context mCm) {
        return Settings.Secure.getString(mCm.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}