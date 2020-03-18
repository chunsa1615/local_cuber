package kr.co.cntt.scc.alimTalk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtils {

  /**
   * DateTimeFormatter : yyyyMMddHHmmssSSS
   */
  public static final DateTimeFormatter DATE_TIME_MILIE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
  /**
   * DateTimeFormatter : yyyy-MM-dd HH:mm
   */
  public static final DateTimeFormatter DATE_TIME_MINUTE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
  /**
   * DateTimeFormatter : yyyy-MM-dd HH:mm:ss
   */
  public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  /**
   * DateTimeFormatter : yyyyMMdd
   */
  public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
  /**
   * DateTimeFormatter : HHmmss
   */
  public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HHmmss");
  /**
   * DateTimeFormatter : yyyyMMddHHmmss
   */
  public static final DateTimeFormatter yyyyMMddHHmmss_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");



  /**
   * 
   * @param String date : "yyyy-MM-dd HH:mm:ss"
   * @param String format : 지정형식
   * @return
   */
  public static Date getStringTransDateFormat(String date, String format) {
    Date result = null;
    SimpleDateFormat transFormat = new SimpleDateFormat(format);

    try {
      result = transFormat.parse(date);
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return result;
  }

  /**
   * convert DateTime String format yyyyMMddHHmmssSSS to Date
   * @param yyyyMMddHHmmssSSS String
   * @return Date
   */
  public static Date dateParseDateTimeMilisec(String s) {
    return getStringTransDateFormat(s, "yyyyMMddHHmmssSSS");
  }
  
  /**
   * @return yyyyMMddHHmmssSSS
   */
  public static String getCurrentTime_DATE_TIME_MILIE_FORMAT() {
    ZonedDateTime now = ZonedDateTime.now();
    return now.format(DATE_TIME_MILIE_FORMAT);
  }

  /**
   * @return yyyy-MM-dd HH:mm
   */
  public static String getCurrentTime_DATE_TIME_MINUTE() {
    ZonedDateTime now = ZonedDateTime.now();
    return now.format(DATE_TIME_MINUTE_FORMAT);
  }

  /**
   * @return yyyy-MM-dd HH:mm:ss
   */
  public static String getCurrentTime_DATE_TIME_FORMAT() {
    ZonedDateTime now = ZonedDateTime.now();
    return now.format(DATE_TIME_FORMAT);
  }

  /**
   * 
   * @param addMinute
   * @return yyyy-MM-dd HH:mm:ss
   */
  public static String getCurrentTime_DATE_TIME_FORMAT(int addMinute) {
    ZonedDateTime now = ZonedDateTime.now();

    if (addMinute > 0) {
      now = now.plusMinutes(addMinute);
    } else if (addMinute < 0) {
      now = now.minusMinutes(addMinute);
    }

    return now.format(DATE_TIME_FORMAT);
  }

  /**
   * @return yyyyMMdd
   */
  public static String getCurrentTime_DATE_FORMAT() {
    ZonedDateTime now = ZonedDateTime.now();
    return now.format(DATE_FORMAT);
  }

  /**
   * @return HHmmss
   */
  public static String getCurrentTime_TIME_FORMAT() {
    ZonedDateTime now = ZonedDateTime.now();
    return now.format(TIME_FORMAT);
  }

  public static long getCurrentUnixTimestamp_SECOND() {
    Instant timestamp = Instant.now();
    return timestamp.getEpochSecond();
  }

  public static long getCurrentUnixTime() {
    Instant timestamp = Instant.now();
    return timestamp.toEpochMilli();
  }

  /**
   * convert Unix Epoch Time(long) to yyyyMMddHHmmss(string)
   * 
   * @param epochTime
   * @return yyyyMMddHHmmss
   */
  public static String getyyyyMMddHHmmss(long epochTime) {
    return Instant.ofEpochSecond(epochTime).atZone(ZoneId.systemDefault()).format(yyyyMMddHHmmss_FORMAT);
  }

  /**
   * (밀리초단위 변환) convert Unix Epoch Time(long) to yyyyMMddHHmmss(string)
   * @param epochTimeMilliseconds
   * @return
   */
  public static String getyyyyMMddHHmmssOfMs(long epochTimeMilliseconds) {
    return getyyyyMMddHHmmss(epochTimeMilliseconds / 1000);
  }

  public static LocalDateTime getLocalDate(String date, String format) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
    return LocalDateTime.parse(date, formatter);
  }
  
  public static String getCustomFormatDate(String datetime, String parseFormat, String returnFormat){
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(returnFormat);
    LocalDateTime localDate = getLocalDate(datetime, parseFormat);
    return localDate.format(formatter);
  }
  
}
