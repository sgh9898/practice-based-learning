package com.demo.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具
 *
 * @author Song gh on 2023/1/12.
 */
public class DateUtils {

    /** 获取日期, 时间设定为 00:00:00 */
    public static Date getDateZeroTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /** 获取日期, 根据当前日期加减 */
    public static Date getDate(Date date, int addDate) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, addDate);
        return calendar.getTime();
    }

    /** 判断是否同一天 */
    public static Boolean isTheSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR))
                && (calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * 拼接日期和时间, 返回 calendar
     *
     * @param dateOnly 取用日期部分
     * @param timeOnly 取用时间部分
     */
    public static Calendar addUpDayAndTimeCalendar(Date dateOnly, Date timeOnly) {
        if (dateOnly == null) {
            return null;
        }
        // 只保留日期
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(dateOnly);
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);

        // 拼接
        if (timeOnly != null) {
            Calendar calendarTime = Calendar.getInstance();
            calendarTime.setTime(timeOnly);
            calendarDate.add(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
            calendarDate.add(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
        }
        return calendarDate;
    }

    /**
     * 拼接日期和时间, 返回 date
     *
     * @param dateOnly 取用日期部分
     * @param timeOnly 取用时间部分
     */
    public static Date addUpDayAndTimeDate(Date dateOnly, Date timeOnly) {
        if (dateOnly == null) {
            return null;
        }
        return addUpDayAndTimeCalendar(dateOnly, timeOnly).getTime();
    }

    /** date 转 string, 取日期时间 */
    public static String dateToStrDateTime(Date srcDate) {
        if (srcDate == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(srcDate);
    }

    /** date 转 string, 只取日期 */
    public static String dateToStrDate(Date srcDate) {
        if (srcDate == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(srcDate);
    }

    /** date 转 string, 只取时间 */
    public static String dateToStrTime(Date srcDate) {
        if (srcDate == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(srcDate);
    }

    /** string 转 date, 取日期时间 */
    public static Date strToDateTime(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /** string 转 date, 只取日期 */
    public static Date strToDateOnly(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /** string 转 date, 只取时间 */
    public static Date strToTimeOnly(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        try {
            return new SimpleDateFormat("HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}