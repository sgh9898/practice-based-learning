package com.sgh.demo.sharding.sharding.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

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
 * @version 2024/3/18
 */
public class ShardingDateUtils {

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

// ------------------------------ 获取 String ------------------------------

    /**
     * Date 转 String, 手动指定格式
     *
     * @param pattern 格式, 如 yyyy-MM-dd HH:mm:ss
     */
    @Nullable
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
     * @return 格式为 yyyy-MM-dd HH:mm:ss
     */
    @Nullable
    public static String toStrDateTime(Date date) {
        return toStr(date, DATETIME_PATTERN);
    }

    /**
     * Date 转 String, 只取日期
     *
     * @return 格式为 yyyy-MM-dd
     */
    @Nullable
    public static String toStrDate(Date date) {
        return toStr(date, DATE_PATTERN);
    }

    /**
     * Date 转 String, 只取时间
     *
     * @return 格式为 yyyy-MM-dd
     */
    @Nullable
    public static String toStrTime(Date date) {
        return toStr(date, TIME_PATTERN);
    }

    /**
     * Date 转 String, 取日期时间(无间隔符)
     *
     * @return 格式为 yyyyMMddHHmmss
     */
    @Nullable
    public static String toStrCompactDateTime(Date date) {
        return toStr(date, COMPACT_DATETIME_PATTERN);
    }

    /**
     * Date 转 String, 只取日期(无间隔符)
     *
     * @return 格式为 yyyyMMdd
     */
    @Nullable
    public static String toStrCompactDate(Date date) {
        return toStr(date, COMPACT_DATE_PATTERN);
    }

    /**
     * Date 转 String, 只取时间(无间隔符)
     *
     * @return 格式为 HHmmss
     */
    @Nullable
    public static String toStrCompactTime(Date date) {
        return toStr(date, COMPACT_TIME_PATTERN);
    }

// ------------------------------ 获取 Date ------------------------------

    /**
     * String 转 Date, 手动指定格式
     *
     * @param pattern 格式, 如: yyyyMMddHHmmss
     */
    @Nullable
    public static Date from(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(strDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("解析 " + pattern + " 格式日期失败, 当前内容: " + strDate, e);
        }
    }

    /**
     * String 转 Date, 取日期时间
     *
     * @param strDate 格式为 yyyy-MM-dd HH:mm:ss
     */
    @Nullable
    public static Date fromDateTime(String strDate) {
        return from(strDate, DATETIME_PATTERN);
    }

    /**
     * String 转 Date, 只取日期
     *
     * @param strDate 格式为 yyyy-MM-dd
     */
    @Nullable
    public static Date fromDate(String strDate) {
        return from(strDate, DATE_PATTERN);
    }

    /**
     * String 转 Date, 只取时间
     *
     * @param strDate 格式为 HH:mm:ss
     */
    @Nullable
    public static Date fromTime(String strDate) {
        return from(strDate, TIME_PATTERN);
    }

    /**
     * String 转 Date, 取日期时间(无间隔符)
     *
     * @param strDate 格式为 yyyyMMddHHmmss
     */
    @Nullable
    public static Date fromCompactDateTime(String strDate) {
        return from(strDate, COMPACT_DATETIME_PATTERN);
    }

    /**
     * String 转 Date, 只取日期(无间隔符)
     *
     * @param strDate 格式为 yyyyMMdd
     */
    @Nullable
    public static Date fromCompactDate(String strDate) {
        return from(strDate, COMPACT_DATE_PATTERN);
    }

    /**
     * String 转 Date, 只取时间(无间隔符)
     *
     * @param strDate 格式为 HHmmss
     */
    @Nullable
    public static Date fromCompactTime(String strDate) {
        return from(strDate, COMPACT_TIME_PATTERN);
    }

    /** Date 转 LocalDateTime */
    @NonNull
    public static LocalDateTime toLocalDateTime(@NonNull Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /** LocalDateTime 转 Date */
    @NonNull
    public static Date fromLocalDateTime(@NonNull LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /** 获取日期当天的开始时间(零点) */
    @NonNull
    public static Date getDayStart(Date date) {
        LocalDate localDate = LocalDate.from(toLocalDateTime(date));
        return fromLocalDateTime(localDate.atStartOfDay());
    }

// ------------------------------ 获取特定日期 ------------------------------

    /** 获取日期当周的开始时间(周一零点) */
    @NonNull
    public static Date getWeekStart(Date date) {
        LocalDate localDate = LocalDate.from(toLocalDateTime(date));
        return fromLocalDateTime(localDate.with(DayOfWeek.MONDAY).atStartOfDay());
    }

    /** 获取日期当月的开始时间 */
    @NonNull
    public static Date getMonthStart(Date date) {
        LocalDate localDate = LocalDate.from(toLocalDateTime(date));
        return fromLocalDateTime(localDate.withDayOfMonth(1).atStartOfDay());
    }

    /** 获取日期当年的开始时间 */
    @NonNull
    public static Date getYearStart(Date date) {
        LocalDate localDate = LocalDate.from(toLocalDateTime(date));
        return fromLocalDateTime(localDate.withDayOfYear(1).withDayOfMonth(1).atStartOfDay());
    }

    /**
     * 获取日期时间, 根据指定日期时间加减
     *
     * @param date       原始日期时间
     * @param num        加减值(正数为未来时间, 负数为过去时间)
     * @param chronoUnit 加减值对应的时间单位
     * @return 计算后的日期时间
     */
    @NonNull
    public static Date getDate(Date date, int num, ChronoUnit chronoUnit) {
        LocalDateTime localDateTime = toLocalDateTime(date);
        return fromLocalDateTime(localDateTime.plus(num, chronoUnit));
    }

// ------------------------------ 日期计算 ------------------------------

    /** 判断是否同一天 */
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat(COMPACT_DATE_PATTERN);
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private ShardingDateUtils() {
    }
}