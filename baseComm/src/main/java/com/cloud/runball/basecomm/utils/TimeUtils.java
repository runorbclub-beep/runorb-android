package com.cloud.runball.basecomm.utils;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author ns467
 */
public class TimeUtils {
    public static String dateFormat_day = "HH:mm";
    public static String dateFormat_month = "MM-dd";
    public static String dateFormat_day2 = "MM/dd";

    /**
     * 时间转换成字符串,默认为"yyyy-MM-dd HH:mm:ss"
     *
     * @param time 时间
     */
    public static String dateToString(long time) {
        return dateToString(time, "yyyy.MM.dd HH:mm");
    }

    public static String dateToMMddString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
        return simpleDateFormat.format(date);
    }

    /**
     * 获得当前周的日期
     * @param date
     * @return
     */
    public static String getWeekBetweenDate(Date date) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一(国外是周日 Calendar.SUNDAY)
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if(dayWeek==1){
            dayWeek = 8;
        }

        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        Date mondayDate = cal.getTime();
        String weekBegin = sdf.format(mondayDate);
        //Logger.d("所在周星期一的日期：" + weekBegin);

        cal.add(Calendar.DATE, 4 +cal.getFirstDayOfWeek());
        Date sundayDate = cal.getTime();
        String weekEnd = sdf.format(sundayDate);
        //Logger.d("所在周星期日的日期：" + weekEnd);
        return weekBegin+"~"+weekEnd;
    }



    /**
     * 时间转换成字符串,指定格式
     *
     * @param time   时间
     * @param format 时间格式
     */
    public static String dateToString(long time, String format) {
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String formatDuration(long duration) {
        long minute = 0, hour = 0, secend = duration;
        minute = (secend % 3600) / 60;
        hour = secend / 3600;
        secend = secend % 60;

        //这里一直增加
        minute=minute+hour*60;

        return String.format("%02d:%02d",  minute, secend);
    }

    public static String formatDuration(int duration) {
        int minute = 0, hour = 0, secend = duration;
        minute = (secend % 3600) / 60;
        hour = secend / 3600;
        secend = secend % 60;

        //这里一直增加
        minute=minute+hour*60;

        return String.format("%02d:%02d",  minute, secend);
        //return String.format("%02d:%02d:%02d", hour, minute, secend);
    }


    public static String formatDuration2(int duration) {
        int minute = 0, hour = 0, secend = duration;
        minute = (secend % 3600) / 60;
        hour = secend / 3600;
        secend = secend % 60;
        return String.format("%02d:%02d",  minute, secend);
        //return String.format("%02d:%02d:%02d", hour, minute, secend);
    }

    public static String formatDuration3(int duration) {
        int minute = 0, hour = 0, secend = duration;
        minute = (secend % 3600) / 60;
        hour = secend / 3600;
        secend = secend % 60;
        return String.format("%02d:%02d:%02d", hour, minute, secend);
    }

    public static String formatDurationFull(int duration) {
        int minute = 0, hour = 0, secend = duration;
        minute = (secend % 3600) / 60;
        hour = secend / 3600;
        secend = secend % 60;
        return String.format("%02d:%02d:%02d", hour, minute, secend);
    }

    public static String formatDurationFull(double duration) {
        int minute = 0, hour = 0, secend = (int)duration;
        minute = (secend % 3600) / 60;
        hour = secend / 3600;
        secend = secend % 60;
        return String.format("%02d:%02d:%02d", hour, minute, secend);
    }


    public static String formatMatchDuration(int duration) {
        int minute=duration/60;
        float second=duration%60;
        if(second>0){
            return String.valueOf(duration/60.0f);
        }
        return String.format("%d",  minute);
    }
}
