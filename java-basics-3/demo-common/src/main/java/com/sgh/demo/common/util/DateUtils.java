package com.sgh.demo.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期工具
 *
 * @author Song gh
 * @version 2024/5/1
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

// ------------------------------ 常量 ------------------------------
    /** [时间格式] 年-月-日 时:分:秒 */
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** [时间格式] 年-月-日 */
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    /** [时间格式] 时:分:秒 */
    public static final String TIME_PATTERN = "HH:mm:ss";

    /** [时间格式] 年月日时分秒(无间隔符) */
    public static final String COMPACT_DATETIME_PATTERN = "yyyyMMddHHmmss";
    /** [时间格式] 年月日(无间隔符) */
    public static final String COMPACT_DATE_PATTERN = "yyyyMMdd";
    /** [时间格式] 时分秒(无间隔符) */
    public static final String COMPACT_TIME_PATTERN = "HHmmss";

// ------------------------------ 获取 String ------------------------------

    /**
     * Date 转为 String, 手动指定格式
     *
     * @param pattern 日期格式, 如 yyyy-MM-dd HH:mm:ss
     * @return 指定格式的 string, 日期为 null 则返回 ""(空白 string)
     */
    @NonNull
    public static String dateToStr(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * Date 转为 String, 取日期 + 时间
     *
     * @return 格式为 yyyy-MM-dd HH:mm:ss 的 string, 日期为 null 则返回 ""(空白 string)
     */
    @NonNull
    public static String dateToStrDateTime(Date date) {
        return dateToStr(date, DATETIME_PATTERN);
    }

    /**
     * Date 转为 String, 只取日期
     *
     * @return 格式为 yyyy-MM-dd 的 string, 日期为 null 则返回 ""(空白 string)
     */
    @NonNull
    public static String dateToStrDateOnly(Date date) {
        return dateToStr(date, DATE_PATTERN);
    }

    /**
     * Date 转为 String, 只取时间
     *
     * @return 格式为 HH:mm:ss 的 string, 日期为 null 则返回 ""(空白 string)
     */
    @NonNull
    public static String dateToStrTimeOnly(Date date) {
        return dateToStr(date, TIME_PATTERN);
    }

    /**
     * Date 转为 String, 取日期 + 时间(无间隔符)
     *
     * @return 格式为 yyyyMMddHHmmss 的 string, 日期为 null 则返回 ""(空白 string)
     */
    @NonNull
    public static String dateToStrCompactDateTime(Date date) {
        return dateToStr(date, COMPACT_DATETIME_PATTERN);
    }

    /**
     * Date 转为 String, 只取日期(无间隔符)
     *
     * @return 格式为 yyyyMMdd 的 string, 日期为 null 则返回 ""(空白 string)
     */
    @NonNull
    public static String dateToStrCompactDateOnly(Date date) {
        return dateToStr(date, COMPACT_DATE_PATTERN);
    }

    /**
     * Date 转为 String, 只取时间(无间隔符)
     *
     * @return 格式为 HHmmss 的 string, 日期为 null 则返回 ""(空白 string)
     */
    @NonNull
    public static String dateToStrCompactTimeOnly(Date date) {
        return dateToStr(date, COMPACT_TIME_PATTERN);
    }

// ------------------------------ 获取 Date ------------------------------

    /**
     * String 转为 Date, 手动指定格式
     *
     * @param pattern 日期格式, 如 yyyy-MM-dd HH:mm:ss
     * @return 解析得到的日期, strDate 为空时会返回 null
     */
    @Nullable
    public static Date strToDate(String strDate, String pattern) {
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
     * String 转为 Date, 取日期 + 时间
     *
     * @param strDate 格式为 yyyy-MM-dd HH:mm:ss
     */
    @Nullable
    public static Date strToDateTime(String strDate) {
        return strToDate(strDate, DATETIME_PATTERN);
    }

    /**
     * String 转为 Date, 只取日期
     *
     * @param strDate 格式为 yyyy-MM-dd
     */
    @Nullable
    public static Date strToDateOnly(String strDate) {
        return strToDate(strDate, DATE_PATTERN);
    }

    /**
     * String 转为 Date, 只取时间
     *
     * @param strDate 格式为 HH:mm:ss
     */
    @Nullable
    public static Date strToTimeOnly(String strDate) {
        return strToDate(strDate, TIME_PATTERN);
    }

    /**
     * String 转为 Date, 取日期 + 时间(无间隔符)
     *
     * @param strDate 格式为 yyyyMMddHHmmss
     */
    @Nullable
    public static Date strToCompactDateTime(String strDate) {
        return strToDate(strDate, COMPACT_DATETIME_PATTERN);
    }

    /**
     * String 转为 Date, 只取日期(无间隔符)
     *
     * @param strDate 格式为 yyyyMMdd
     */
    @Nullable
    public static Date strToCompactDateOnly(String strDate) {
        return strToDate(strDate, COMPACT_DATE_PATTERN);
    }

    /**
     * String 转为 Date, 只取时间(无间隔符)
     *
     * @param strDate 格式为 HHmmss
     */
    @Nullable
    public static Date strToCompactTimeOnly(String strDate) {
        return strToDate(strDate, COMPACT_TIME_PATTERN);
    }

// ------------------------------ LocalDateTime 转换 ------------------------------

    /** Date 转 LocalDateTime */
    @NonNull
    public static LocalDateTime dateToLocalDateTime(@NonNull Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /** LocalDateTime 转 Date */
    @NonNull
    public static Date localDateTimeToDate(@NonNull LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

// ------------------------------ 获取特定日期 ------------------------------

    /**
     * 获取当前时间
     *
     * @return 格式为 yyyy-MM-dd HH:mm:ss 的 string
     */
    @NonNull
    public static String getNowStr() {
        return dateToStrDateTime(new Date());
    }

    /**
     * 获取当前时间(无间隔符)
     *
     * @return 格式为 yyyyMMddHHmmss 的 string
     */
    @NonNull
    public static String getNowCompactStr() {
        return dateToStrDateTime(new Date());
    }

    /** 获取本日的开始时间(零点) */
    @NonNull
    public static Date getDayStart() {
        LocalDate localDate = LocalDate.from(dateToLocalDateTime(new Date()));
        return localDateTimeToDate(localDate.atStartOfDay());
    }

    /** 获取日期当天的开始时间(零点) */
    @NonNull
    public static Date getDayStart(Date date) {
        LocalDate localDate = LocalDate.from(dateToLocalDateTime(date));
        return localDateTimeToDate(localDate.atStartOfDay());
    }

    /** 获取本周的开始时间(周一零点) */
    @NonNull
    public static Date getWeekStart() {
        LocalDate localDate = LocalDate.from(dateToLocalDateTime(new Date()));
        return localDateTimeToDate(localDate.with(DayOfWeek.MONDAY).atStartOfDay());
    }

    /** 获取日期当周的开始时间(周一零点) */
    @NonNull
    public static Date getWeekStart(Date date) {
        LocalDate localDate = LocalDate.from(dateToLocalDateTime(date));
        return localDateTimeToDate(localDate.with(DayOfWeek.MONDAY).atStartOfDay());
    }

    /** 获取本月的开始时间 */
    @NonNull
    public static Date getMonthStart() {
        LocalDate localDate = LocalDate.from(dateToLocalDateTime(new Date()));
        return localDateTimeToDate(localDate.withDayOfMonth(1).atStartOfDay());
    }

    /** 获取日期当月的开始时间 */
    @NonNull
    public static Date getMonthStart(Date date) {
        LocalDate localDate = LocalDate.from(dateToLocalDateTime(date));
        return localDateTimeToDate(localDate.withDayOfMonth(1).atStartOfDay());
    }

    /** 获取本年度的开始时间 */
    @NonNull
    public static Date getYearStart() {
        LocalDate localDate = LocalDate.from(dateToLocalDateTime(new Date()));
        return localDateTimeToDate(localDate.withDayOfYear(1).withDayOfMonth(1).atStartOfDay());
    }

    /** 获取日期当年的开始时间 */
    @NonNull
    public static Date getYearStart(Date date) {
        LocalDate localDate = LocalDate.from(dateToLocalDateTime(date));
        return localDateTimeToDate(localDate.withDayOfYear(1).withDayOfMonth(1).atStartOfDay());
    }

// ------------------------------ 日期计算 ------------------------------

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
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(localDateTime.plus(num, chronoUnit));
    }

    /**
     * 获取日期时间, 根据指定日期时间加减天数
     *
     * @param date 原始日期时间
     * @param num  加减天数(正数为未来时间, 负数为过去时间)
     * @return 计算后的日期时间
     */
    @NonNull
    public static Date getDay(Date date, int num) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(localDateTime.plusDays(num));
    }

    /** 判断是否同一天 */
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat(COMPACT_DATE_PATTERN);
        return fmt.format(date1).equals(fmt.format(date2));
    }

    /**
     * 计算时间间隔
     *
     * @param startTimestamp 开始时间(时间戳)
     * @param endTimestamp   结束时间(时间戳)
     */
    public static Duration getDuration(long startTimestamp, long endTimestamp) {
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTimestamp), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTimestamp), ZoneId.systemDefault());
        return Duration.between(start, end);
    }
}
