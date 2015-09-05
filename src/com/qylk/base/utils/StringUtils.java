package com.qylk.mp.bus.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 字符串帮助类
 */
public class StringUtils {
    public static final int TYPE_NUMBER = 0;
    public static final int TYPE_ENGLISH = 1;
    public static final int TYPE_SYMBOL = 2;
    public static final int TYPE_CHINA = 3;

    // --------------------------------------------------------------------------------------------
    public static String addPrefix(int num, String prefix) {
        return num < 10 ? prefix + num : String.valueOf(num);
    }

    public static String addPrefix(String numStr, String prefix) {
        int num = Integer.parseInt(numStr);
        return addPrefix(num, prefix);
    }

    public static String addPrefixZero(int num) {
        return addPrefix(num, "0");
    }

    public static String addPrefixZero(String numStr) {
        return addPrefix(numStr, "0");
    }

    public static String addPrefixHtmlSpace(int num) {
        return addPrefix(num, "&nbsp;");
    }

    public static String addPrefixHtmlSpace(String numStr) {
        return addPrefix(numStr, "&nbsp;");
    }

    public static String commaInt(Object[] data, String symbol) {
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            sbf.append(data[i]);
            if (i < data.length - 1) {
                sbf.append(symbol);
            }
        }
        return sbf.toString();
    }

    public static String commaInt(Object[] data) {
        return commaInt(data, ",");
    }

    /**
     * bytes[]转换成Hex字符串,可用于URL转换，IP地址转换.
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static String repeat(String str, int times) {
        StringBuffer sbf = new StringBuffer();
        for (int i = 0; i < times; i++) {
            sbf.append(str);
        }
        return sbf.toString();
    }

    /**
     * 判断 char c 是汉字还是数字 还是字母
     *
     * @param c
     * @return int
     * @author gdpancheng@gmail.com 2013-10-16 下午10:41:15
     */
    public static int sepMarkNot(char c) {
        // 数字 48-57
        if (c > 47 && c < 58) {
            return TYPE_NUMBER;
        }
        // 大写字母 65-90
        if (c > 64 && c < 91) {
            return TYPE_ENGLISH;
        }
        // 小写字母 97-122
        if (c > 96 && c < 122) {
            return TYPE_ENGLISH;
        }
        // 汉字（简体）
        if (c >= 0x4e00 && c <= 0x9fbb) {
            return TYPE_CHINA;
        }
        return TYPE_SYMBOL;
    }

    public static int getBytesLengths(String content) {
        int count = 0;
        for (int i = 0; i < content.length(); i++) {
            if (sepMarkNot(content.charAt(i)) == TYPE_CHINA) {
                count = count + 2;
            } else {
                count = count + 1;
            }
        }
        return count;
    }

    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }

    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0);
    }


    public static String utf8Encode(String str) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }

    /**
     * 转换半角到全角
     */
    public static String fullWidthToHalfWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == 12288) {
                source[i] = ' ';
                // } else if (source[i] == 12290) {
                // source[i] = '.';
            } else if (source[i] >= 65281 && source[i] <= 65374) {
                source[i] = (char) (source[i] - 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    /**
     * 转换全角到半角
     */
    public static String halfWidthToFullWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == ' ') {
                source[i] = (char) 12288;
                // } else if (source[i] == '.') {
                // source[i] = (char)12290;
            } else if (source[i] >= 33 && source[i] <= 126) {
                source[i] = (char) (source[i] + 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }
}
