package com.demo.util;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 格式转换工具
 *
 * @author Song gh on 2022/8/16.
 */
public class ConversionUtil {

    /** 判断是否同一天 */
    public static Boolean isTheSameDay(Date date1, Date date2) {
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
        // 只保留日期
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(dateOnly);
        calendarDate.set(Calendar.HOUR_OF_DAY, 0);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);

        Calendar calendarTime = Calendar.getInstance();
        calendarTime.setTime(timeOnly);

        // 拼接
        calendarDate.add(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY));
        calendarDate.add(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE));
        return calendarDate;
    }

    /**
     * 拼接日期和时间, 返回 date
     *
     * @param dateOnly 取用日期部分
     * @param timeOnly 取用时间部分
     */
    public static Date addUpDayAndTimeDate(Date dateOnly, Date timeOnly) {
        return addUpDayAndTimeCalendar(dateOnly, timeOnly).getTime();
    }

    /** date 转 string, 取日期时间 */
    public static String dateToStrDateTime(Date srcDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(srcDate);
    }

    /** date 转 string, 只取日期 */
    public static String dateToStrDate(Date srcDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(srcDate);
    }

    /** date 转 string, 只取时间 */
    public static String dateToStrTime(Date srcDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(srcDate);
    }

    /**
     * 将sql查询结果转为map
     *
     * @param sqlMapList sql查询结果
     * @param keyName    key 字段名
     * @param valueName  value 字段名
     */
    public static Map<?, ?> sqlMapListToMap(List<Map<String, Object>> sqlMapList, String keyName, String valueName) {
        Map<Object, Object> resultMap = new HashMap<>();
        for (Map<String, Object> currMap : sqlMapList) {
            resultMap.put(currMap.get(keyName), currMap.get(valueName));
        }
        return resultMap;
    }
}
