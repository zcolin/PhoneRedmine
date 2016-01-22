package com.phone.redmine.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Calendar操作工具类
 */
public class CalendarUtil
{

	/** 
	 * 缺省的日期显示格式： yyyy-MM-dd 
	 */
	public static final String	DEF_DATE_FORMAT		= "yyyy-MM-dd";

	/** 
	 * 缺省的日期时间显示格式：yyyy-MM-dd HH:mm:ss 
	 */
	public static final String	DEF_DATETIME_FORMAT	= "yyyy-MM-dd HH:mm:ss";

	/** 
	 * 只有时间的输出格式：HHmmss 
	 */
	public static final String	DEF_ONLYTIME_FORMAT	= "HHmmss";

	/** 
	 * UTC格式
	 */
	public static final String	DEF_UTC_FORMAT		= "yyyy-MM-dd'T'HH:mm:ss'Z'";

	/** 
	 * 私有构造方法，禁止对该类进行实例化 
	 */
	private CalendarUtil ()
	{}

	/** 
	 * 得到系统当前日期时间 
	 * 
	 * @return 		当前日期时间 
	 */
	public static Date getNow()
	{
		return Calendar.getInstance().getTime();
	}

	/** 
	 * 得到用缺省方式格式化的当前日期 
	 * 
	 * @return 		当前日期 
	 */
	public static String getDate()
	{
		return getDateTime(DEF_DATE_FORMAT);
	}

	/** 
	 * 得到用缺省方式格式化的当前日期及时间 
	 * 
	 * @return 		当前日期及时间 
	 */
	public static String getDateTime()
	{
		return getDateTime(DEF_DATETIME_FORMAT);
	}

	/** 
	 * 得到用缺省方式格式化的当前时间 
	 * 
	 * @return 		当前时间 
	 */
	public static String getOnlyTime()
	{
		return getDateTime(DEF_ONLYTIME_FORMAT);
	}

	/** 
	 * 得到系统当前日期及时间，并用指定的方式格式化 
	 * 
	 * @param pattern 		显示格式 
	 * 
	 * @return 				当前日期及时间 
	 */
	public static String getDateTime(String pattern)
	{
		Date datetime = Calendar.getInstance().getTime();
		return getDateTime(datetime, pattern);
	}

	/** 
	 * 得到用指定方式格式化的日期 
	 * 
	 * @param date 			Date对象
	 * @param pattern 		显示格式 
	 * 
	 * @return 				日期时间字符串 
	 */
	public static String getDateTime(Date date, String pattern)
	{
		if (date == null)
		{
			return "";
		}

		if (null == pattern || "".equals(pattern))
		{
			pattern = DEF_DATETIME_FORMAT;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
		return dateFormat.format(date);
	}

	/** 
	 * 得到用指定方式格式化的日期  UTC时间
	 * 
	 * @param date 			Date对象
	 * @param pattern 		显示格式 
	 * 
	 * @return 				日期时间字符串 
	 */
	public static String getDateTimeUTC(Date date, String pattern)
	{
		if (date == null)
		{
			return "";
		}

		if (null == pattern || "".equals(pattern))
		{
			pattern = DEF_DATETIME_FORMAT;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}

	/** 
	 * 得到当前年份 
	 * 
	 * @return 			当前年份 
	 */
	public static int getCurrentYear()
	{
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/** 
	 * 得到当前月份 
	 * 
	 * @return 			当前月份 
	 */
	public static int getCurrentMonth()
	{
		//用get得到的月份数比实际的小1，需要加上  
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/** 
	 * 得到当前日 
	 * 
	 * @return 			当前日 
	 */
	public static int getCurrentDay()
	{
		return Calendar.getInstance().get(Calendar.DATE);
	}

	/**
	 * 获取周天数    Calendar中1-星期天，2-星期一，3-星期二，4-星期三，5-星期四，6-星期五，7-星期六
	 * 
	 * @param dayofweek			使用Calendar获取的dayOfWeek
	 * 
	 * @return					转换后的dayOfWeek
	 */
	public static int getDayOfWeek(int dayofweek)
	{
		int day = 1;
		if (dayofweek > 0 && dayofweek < 8)
		{
			int[] wee = {0, 7, 1, 2, 3, 4, 5, 6};
			day = wee[dayofweek];
		}
		return day;
	}

	/** 
	 * 取得当前日期以后若干天的日期。如果要得到以前的日期，参数用负数。 
	 * 例如要得到上星期同一天的日期，参数则为-7 
	 * 
	 * @param days 				增加的日期数 
	 * 
	 * @return 					增加以后的日期 
	 */
	public static Date addDays(int days)
	{
		return add(getNow(), days, Calendar.DATE);
	}

	/** 
	 * 取得指定日期以后若干天的日期。如果要得到以前的日期，参数用负数。
	 *
	 * @param date 				基准日期 
	 * @param days 				增加的日期数 
	 * 
	 * @return 					增加以后的日期 
	 */
	public static Date addDays(Date date, int days)
	{
		return add(date, days, Calendar.DATE);
	}

	/** 
	 * 取得当前日期以后某月的日期。如果要得到以前月份的日期，参数用负数。 
	 * 
	 * @param months 			增加的月份数 
	 * 
	 * @return 					增加以后的日期 
	 */
	public static Date addMonths(int months)
	{
		return add(getNow(), months, Calendar.MONTH);
	}

	/** 
	 * 取得指定日期以后某月的日期。如果要得到以前月份的日期，参数用负数。 
	 * 注意，可能不是同一日子，例如2003-1-31加上一个月是2003-2-28 、
	 * 
	 * @param date 				基准日期 
	 * @param months 			增加的月份数 
	
	 * @return 					增加以后的日期 
	 */
	public static Date addMonths(Date date, int months)
	{
		return add(date, months, Calendar.MONTH);
	}

	/**
	 * 为指定日期增加相应的天数或月数 
	 * 
	 * @param date 				基准日期 
	 * @param amount 			增加的数量 
	 * @param field 			增加的单位，年，月或者日 
	 * 
	 * @return 					增加以后的日期 
	 */
	public static Date add(Date date, int amount, int field)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, amount);
		return calendar.getTime();
	}

	/** 
	 * 计算日期和现在相差时间 
	 * 
	 * @param date			需要计算的Date对象
	 * 
	 * @return				相差的时间
	 */
	public static String diffNow(Date date)
	{
		return diff(CalendarUtil.getNow(), date);
	}

	/** 
	 * 计算两个日期相差时间 。 
	 * 
	 * @param one 			第一个日期数，作为基准 
	 * @param two 			第二个日期数，作为比较 
	 * 
	 * @return 				相差时间  
	 */
	public static String diff(Date one, Date two)
	{
		long diffTime = diffDays(one, two);
		if (Math.abs(diffTime) > 0)
		{
			return diffTime + "天";
		}

		diffTime = diffHours(one, two);
		if (Math.abs(diffTime) > 0)
		{
			return diffTime + "小时";
		}

		return diffMinutes(one, two) + "分钟";
	}

	/** 
	 * 计算两个日期相差分钟数。 
	 * 用第一个日期减去第二个。如果前一个日期小于后一个日期，则返回负数 
	 * 
	 * @param one 			第一个日期数，作为基准 
	 * @param two 			第二个日期数，作为比较 
	 * 
	 * @return 				两个日期相差分钟数 
	 */
	public static long diffMinutes(Date one, Date two)
	{
		return (one.getTime() - two.getTime()) / (60 * 1000);
	}

	/** 
	 * 计算两个日期相差小时数。 
	 * 用第一个日期减去第二个。如果前一个日期小于后一个日期，则返回负数 
	 * 
	 * @param one 			第一个日期数，作为基准 
	 * @param two 			第二个日期数，作为比较 
	 * 
	 * @return 				两个日期相差小时数 
	 */
	public static long diffHours(Date one, Date two)
	{
		return (one.getTime() - two.getTime()) / (60 * 60 * 1000);
	}

	/** 
	 * 计算两个日期相差天数。 
	 * 用第一个日期减去第二个。如果前一个日期小于后一个日期，则返回负数 
	 * 
	 * @param one 			第一个日期数，作为基准 
	 * @param two 			第二个日期数，作为比较 
	 * @return 				两个日期相差天数 
	 */
	public static long diffDays(Date one, Date two)
	{
		return (one.getTime() - two.getTime()) / (24 * 60 * 60 * 1000);
	}

	/** 
	 * 将一个字符串用给定的格式转换为日期类型。
	 * 
	 * @param datestr 				需要解析的日期字符串 
	 * @param pattern 				日期字符串的格式，默认为“yyyy-MM-dd”的形式 
	 * 
	 * @return 						解析后的日期 ， 如果返回null，则表示解析失败 
	 */
	public static Date parse(String datestr, String pattern)
	{
		Date date = null;
		if (null == pattern || "".equals(pattern))
		{
			pattern = DEF_DATE_FORMAT;
		}
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
			date = dateFormat.parse(datestr);
		} catch (ParseException e)
		{
		}
		return date;
	}

	/** 
	 * 将一个UTC时间字符串用给定的格式转换为日期类型。
	 * 
	 * @param datestr 				需要解析的日期字符串 
	 * @param pattern 				日期字符串的格式，默认为“yyyy-MM-dd”的形式 
	 * 
	 * @return 						解析后的日期 ， 如果返回null，则表示解析失败 
	 */
	public static Date parseUTC(String datestr, String pattern)
	{
		Date date = null;
		if (null == pattern || "".equals(pattern))
		{
			pattern = DEF_DATE_FORMAT;
		}
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			date = dateFormat.parse(datestr);
		} catch (ParseException e)
		{
		}
		return date;
	}

	/** 
	 * 返回本月的最后一天 
	 * 
	 * @return 			本月最后一天的日期 
	 */
	public static Date getMonthLastDay()
	{
		return getMonthLastDay(getNow());
	}

	/** 
	 * 返回给定日期中的月份中的最后一天 
	 * 
	 * @param date 			基准日期 
	 * 
	 * @return 				该月最后一天的日期 
	 */
	public static Date getMonthLastDay(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		//将日期设置为下一月第一天  
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1);
		//减去1天，得到的即本月的最后一天  
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}
}
