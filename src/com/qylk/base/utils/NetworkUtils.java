package com.qylk.mp.bus.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkUtils {
    public static final int NETWORK_CLASS_UNKNOWN = 0;
    public static final int NETWORK_CLASS_2_G = 1;
    public static final int NETWORK_CLASS_3_G = 2;
    public static final int NETWORK_CLASS_4_G = 3;

    public static final ConnectivityManager getConnectivityManager(Context mContext) {
        return (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static final WifiManager getWifiManager(Context mContext) {
        return (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
    }

    public static final TelephonyManager getTelephonyManager(Context mContext) {
        return (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static boolean isConnectedToMobile(Context mContext) {
        ConnectivityManager cm = getConnectivityManager(mContext);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo != null && networkInfo.isConnected();
    }


    public static boolean isConnectedToWifi(Context mContext) {
        ConnectivityManager cm = getConnectivityManager(mContext);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getNetWork(Context mContext) {
        NetworkInfo ni = getAvailableNetworkInfo(mContext);
        if (ni != null) {
            int type = ni.getType();
            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    return "WIFI";
                case ConnectivityManager.TYPE_MOBILE:
                case ConnectivityManager.TYPE_MOBILE_DUN:
                case ConnectivityManager.TYPE_MOBILE_HIPRI:
                case ConnectivityManager.TYPE_MOBILE_MMS:
                case ConnectivityManager.TYPE_MOBILE_SUPL:
                    // 网络类型
                    TelephonyManager tm = getTelephonyManager(mContext);
                    if (tm == null) {
                        return "MOBILE";
                    }
                    int netType = getNetworkClass(tm.getNetworkType());
                    if (netType == NETWORK_CLASS_2_G) {
                        return "2G";
                    } else if (netType == NETWORK_CLASS_3_G) {
                        return "3G";
                    } else if (netType == NETWORK_CLASS_4_G) {
                        return "4G";
                    } else {
                        return ni.getTypeName();
                    }
                default:
                    return ni.getTypeName();
            }
        }
        return "";
    }

    public static NetworkInfo getAvailableNetworkInfo(Context mContext) {
        ConnectivityManager cm = getConnectivityManager(mContext);
        if (cm == null) {
            return null;
        }
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isConnectedOrConnecting()) {
            return ni;
        } else {
            return null;
        }
    }

    public static boolean isNetworkAvailable(Context mContext) {
        return getAvailableNetworkInfo(mContext) != null;
    }

    public static int getNetworkClass(int networkType) {
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2_G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return NETWORK_CLASS_3_G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4_G;
            default:
                return NETWORK_CLASS_UNKNOWN;
        }
    }

    public static String getAPN(Context mContext) {
        String currentAPN = "";
        try {
            NetworkInfo info = getConnectivityManager(mContext)
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            currentAPN = info.getExtraInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null == currentAPN ? "" : currentAPN;
    }

    public static String getOperator(Context mContext) {
        TelephonyManager tm = getTelephonyManager(mContext);
        if (tm == null) {
            return null;
        }
        return tm.getNetworkOperatorName();
    }

    public static int getWiFiStrength(Context mContext) {
        WifiManager wm = getWifiManager(mContext);
        if (wm == null) {
            return -1;
        }
        WifiInfo wifiInfo = wm.getConnectionInfo();
        if (wifiInfo != null) {
            return wifiInfo.getRssi();
        } else {
            return -1;
        }
    }

    public static List<String> getLocalIpAddressV4() {
        List<String> ipV4List = new ArrayList<String>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) { //这里做了一步IPv4的判定
                        ipV4List.add(inetAddress.getHostAddress().toString());
                    }
                }
            }
        } catch (Exception e) {
        }
        return ipV4List;
    }

    public static String getWifiSSID(Context context) {
        WifiManager wm = getWifiManager(context);
        if (wm == null) {
            return null;
        }
        WifiInfo wifiInfo = wm.getConnectionInfo();
        return wifiInfo == null ? "" : wifiInfo.getSSID();
    }

    public static int getWifiIpAddress(Context context) {
        WifiManager wm = getWifiManager(context);
        if (wm == null) {
            return -1;
        }
        WifiInfo wifiInfo = wm.getConnectionInfo();
        return wifiInfo == null ? -1 : wifiInfo.getIpAddress();
    }
}
