package kr.co.cntt.scc.util;

import java.sql.Time;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * Date Util
 *
 * Created by jslivane on 2016. 6. 14..
 *
 * https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
 *
 */
public class DateUtil {

    public static final String MIN_TIME = "00:00:00";
    public static final String MAX_TIME = "23:59:59";

    public static Date getCurrentDate() {
        return new Date();
    }

    public static Time getCurrentTime() {
        return new Time(new Date().getTime());
    }

    public static String getCurrentDateStringAppAdminParse(String paramDate) {
        LocalDate date = LocalDate.parse(paramDate);
        // yyyy-MM-dd
        return date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

    }
    
    public static String getDateStringAppAdmin(String type) {
    	LocalDate date = null;
    	if (type.equals("now")) {
    		date = LocalDate.now();
    	}
    	else if (type.equals("yesterday")) {
    		date = LocalDate.now().minusDays(1);
    	}
        // yyyy-MM-dd
        return date.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

    }

    public static String getCurrentDateString() {
        LocalDate currentDate = LocalDate.now();
        
        // yyyy-MM-dd
        return currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

    }

    public static String getCurrentDateTimeString() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // yyyy-MM-dd HH:mm:ss
        return currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    }

    public static String getCurrentTimeString() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // HH:mm:ss
        return currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

    }

    public static String getCurrentTimeShortString() {
        LocalDateTime currentDateTime = LocalDateTime.now();

        // HH:mm
        return currentDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

    }

    public static String getCurrentDatePlusDaysString(Integer day) {
        LocalDate currentDate = LocalDate.now().plusDays(day);
        
        // yyyy-MM-dd
        return currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE);

    }
    
    public static Date getNextDate() {
        return asDate(LocalDate.now().plusDays(1));
    }

    public static Time getEndTime() {
        return Time.valueOf("03:00:00"); // FIXME
    }

    public static Time getStartTime() {
        return Time.valueOf("08:00:00"); // FIXME
    }

    public static Date asDate(LocalDate localDate) {
      return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
      return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
      return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
      return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
}
