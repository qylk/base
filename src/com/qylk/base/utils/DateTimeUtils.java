package com.qylk.base.utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;

public class DateTimeUtils {

    public static String fromatTimeStamp2Date(Context context, long timestamp) {
        check(timestamp);
        Time time = new Time();
        time.set(timestamp);
        Time now = new Time();
        now.setToNow();

        int format_flags = DateUtils.FORMAT_NO_MIDNIGHT | DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM | DateUtils.FORMAT_24HOUR;

        if (time.year != now.year) {
            format_flags |= DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_DATE;
        } else if (time.yearDay != now.yearDay) {
            format_flags |= DateUtils.FORMAT_SHOW_DATE;
        } else {
            format_flags |= DateUtils.FORMAT_SHOW_TIME;
        }
        return DateUtils.formatDateTime(context, timestamp, format_flags);
    }

    private static final void check(long timestamp) {
        if (timestamp < 0) {
            throw new IllegalArgumentException("timestamp should be positive");
        }
    }

    public static String calculateTimeGap(long start, long end) {
        long timeGap = end - start;
        if (timeGap < 0) {
            throw new IllegalArgumentException("start time should not be larger than end time");
        } else if (timeGap < 60 * 1000) {
            return "刚刚";
        } else if (timeGap < 60 * 60 * 1000) {
            return timeGap / (60 * 1000) + "分钟前";
        } else if (timeGap < 24 * 60 * 60 * 1000) {
            return timeGap / (60 * 60 * 1000) + "小时前";
        } else {
            return timeGap / (24 * 60 * 60 * 1000) + "天前";
        }
    }
}
