package com.ldb.vocabulary2.android.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lsp on 2016/9/20.
 */
public class DateUtil {
    public static Date getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static Date addMilliSecond(Date originalDate, int amount ){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originalDate);
        calendar.add(Calendar.MILLISECOND, amount);
        Date date = calendar.getTime();
        return date;
    }

    public static Date addSecond(Date originalDate, int amount ){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originalDate);
        calendar.add(Calendar.SECOND, amount);
        Date date = calendar.getTime();
        return date;
    }

    public static Date addMinute(Date originalDate, int amount ){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originalDate);
        calendar.add(Calendar.MINUTE, amount);
        Date date = calendar.getTime();
        return date;
    }

    public static Date addHour(Date originalDate, int amount ){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originalDate);
        calendar.add(Calendar.HOUR_OF_DAY, amount);
        Date date = calendar.getTime();
        return date;
    }

    public static Date addDay(Date originalDate, int amount ){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(originalDate);
        calendar.add(Calendar.DAY_OF_MONTH, amount);
        Date date = calendar.getTime();
        return date;
    }

    public static String format(Timestamp originalDate, String format){
        DateFormat df = new SimpleDateFormat(format);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(originalDate.getTime());
        return df.format(calendar.getTime());
    }

    public static String format(Timestamp originalDate){
        return format(originalDate, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Date originalDate, String format){
        return format(new Timestamp(originalDate.getTime()), format);
    }

    public static String format(Date originalDate){
        return format(new Timestamp(originalDate.getTime()));
    }



    public static Date parseDate(String dateStr, String format) throws ParseException{
        DateFormat df = new SimpleDateFormat(format);
        return df.parse(dateStr);
    }

    public static Date parseDateTime(String dateStr) throws ParseException {
        return parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
    }
}
