package com.demo.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具
 *
 * @author Song gh
 * @version 2024.1.17
 */
public class DateUtils {

// ------------------------------ 常量 ------------------------------
    /** [时间格式] 年月日时分秒 */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** [时间格式] 年月日 */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    /** [时间格式] 时分秒 */
    public static final String TIME_PATTERN = "HH:mm:ss";

    /** [时间格式] 年月日时分秒(无间隔符) */
    public static final String COMPACT_DATETIME_PATTERN = "yyyyMMddHHmmss";
    /** [时间格式] 年月日(无间隔符) */
    public static final String COMPACT_DATE_PATTERN = "yyyyMMdd";
    /** [时间格式] 时分秒(无间隔符) */
    public static final String COMPACT_TIME_PATTERN = "HHmmss";

// ------------------------------ 格式转换 ------------------------------

    /**
     * Date 转 String, 手动指定格式
     *
     * @param date    指定日期
     * @param pattern 日期格式
     */
    public static String toStr(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * Date 转 String, 取日期时间
     *
     * @return 日期, 格式为 yyyy-MM-dd HH:mm:ss
     */
    public static String toStrDateTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        return dateFormat.format(date);
    }

    /**
     * Date 转 String, 只取日期
     *
     * @return 日期, 格式为 yyyy-MM-dd
     */
    public static String toStrDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        return dateFormat.format(date);
    }

    /**
     * Date 转 String, 只取时间
     *
     * @return 日期, 格式为 yyyy-MM-dd
     */
    public static String toStrTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_PATTERN);
        return dateFormat.format(date);
    }

    /**
     * Date 转 String, 取日期时间(无间隔符)
     *
     * @return 日期, 格式为 yyyyMMddHHmmss
     */
    public static String toStrCompactDateTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(COMPACT_DATETIME_PATTERN);
        return dateFormat.format(date);
    }

    /**
     * Date 转 String, 只取日期(无间隔符)
     *
     * @return 日期, 格式为 yyyyMMdd
     */
    public static String toStrCompactDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(COMPACT_DATE_PATTERN);
        return dateFormat.format(date);
    }

    /**
     * Date 转 String, 只取时间(无间隔符)
     *
     * @return 日期, 格式为 HHmmss
     */
    public static String toStrCompactTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(COMPACT_TIME_PATTERN);
        return dateFormat.format(date);
    }

    /**
     * String 转 Date, 取日期时间
     *
     * @param strDate 日期, 格式为 yyyy-MM-dd HH:mm:ss
     */
    public static Date toDateTime(String strDate) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("解析 " + DATETIME_PATTERN + " 格式日期失败, 当前内容: " + strDate, e);
        }
    }

    /**
     * String 转 Date, 取日期时间(无间隔符)
     *
     * @param strDate 日期, 格式为 yyyyMMddHHmmss
     */
    public static Date toCompactDateTime(String strDate) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(COMPACT_DATETIME_PATTERN);
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("解析 " + COMPACT_DATETIME_PATTERN + " 格式日期失败, 当前内容: " + strDate, e);
        }
    }

    /** Date 转 LocalDateTime */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /** LocalDateTime 转 Date */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

// ------------------------------ 获取特定日期 ------------------------------

    /** 获取日期当天的开始时间(零点) */
    public static Date getDayStart(Date date) {
        LocalDate localDate = LocalDate.from(toLocalDateTime(date));
        return toDate(localDate.atStartOfDay());
    }

    /** 获取日期当周的开始时间(周一零点) */
    public static Date getWeekStart(Date date) {
        LocalDate localDate = LocalDate.from(toLocalDateTime(date));
        return toDate(localDate.with(DayOfWeek.MONDAY).atStartOfDay());
    }

    /** 获取日期当月的开始时间 */
    public static Date getMonthStart(Date date) {
        LocalDate localDate = LocalDate.from(toLocalDateTime(date));
        return toDate(localDate.withDayOfMonth(1).atStartOfDay());
    }

    /** 获取日期当年的开始时间 */
    public static Date getYearStart(Date date) {
        LocalDate localDate = LocalDate.from(toLocalDateTime(date));
        return toDate(localDate.withDayOfYear(1).withDayOfMonth(1).atStartOfDay());
    }

// ------------------------------ 日期计算 ------------------------------

    /**
     * 获取日期时间, 根据指定日期时间加减
     *
     * @param date       原始日期时间
     * @param num        加减值(正数为未来时间, 负数为以往时间)
     * @param chronoUnit 加减值对应的时间单位
     * @return 计算后的日期时间
     */
    public static Date getDate(Date date, int num, ChronoUnit chronoUnit) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return toDate(localDateTime.plus(num, chronoUnit));
    }

    /** 判断是否同一天 */
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat(COMPACT_DATE_PATTERN);
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private DateUtils() {
    }
}