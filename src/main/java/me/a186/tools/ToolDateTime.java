package me.a186.tools;

import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Punk on 2016/3/14.
 */
public abstract class ToolDateTime {
    private static Logger log = Logger.getLogger(ToolDateTime.class);

    public static final String pattern_ymd = "yyyy-MM-dd"; // pattern_ymd
    public static final String pattern_ymd_hms = "yyyy-MM-dd HH:mm:ss"; // pattern_ymdtime
    public static final String pattern_ymd_hms_s = "yyyy-MM-dd HH:mm:ss:SSS"; // pattern_ymdtimeMillisecond

    /**
     * 解析
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date parse(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            log.error("ToolDateTime.parse异常：date值" + date + "，pattern值" + pattern);
            return null;
        }
    }

    /**
     * 返回两个日期之间隔了多少小时
     *
     * @param start
     * @param end
     * @return
     */
    public static int getDateHourSpace(Date start, Date end) {
        int hour = (int) ((end.getTime() - start.getTime()) / (60 * 60 * 1000));
        return hour;
    }

    /**
     * 返回两个日期之间间隔了多少分钟
     *
     * @param start
     * @param end
     * @return
     */
    public static int getDateMinuteSpace(Date start, Date end) {
        int hour = (int) ((end.getTime() - start.getTime()) / (60 * 1000));
        return hour;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getDate() {
        return new Date();
    }

    /**
     * 返回两个日期之间隔了多少天
     *
     * @param start
     * @param end
     * @return
     */
    public static int getDateDaySpace(Date start, Date end) {
        int day = (int) ((end.getTime() - start.getTime()) / (60 * 60 * 24 * 1000));
        return day;
    }

    /**
     * 获取当前时间的时间戳
     *
     * @return
     */
    public static long getDateByTime() {
        return new Date().getTime();
    }

    /**
     * 主要给jfinal使用，数据库只认java.sql.*
     *
     * @param time
     * @return
     */
    public static Timestamp getSqlTimestamp(long time) {
        return new Timestamp(time);
    }

    public static Timestamp getSqlTimestamp(Date date) {
        if (null == date) {
            return getSqlTimestamp();
        }
        return getSqlTimestamp(date.getTime());
    }

    public static Timestamp getSqlTimestamp() {
        return getSqlTimestamp(new Date().getTime());
    }
}
