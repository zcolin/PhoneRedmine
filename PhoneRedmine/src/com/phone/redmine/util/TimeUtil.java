package com.phone.redmine.util;

import android.os.SystemClock;
import android.text.format.Time;

/**
 * Time操作工具类
 */
public class TimeUtil
{

	/** 
	 * 缺省的日期显示格式： yyyy-MM-dd 
	 */
	public static final String	DEF_DATE_FORMAT		= "%Y-%m-%d";

	/** 
	 * 缺省的日期时间显示格式：yyyy-MM-dd HH:mm:ss 
	 */
	public static final String	DEF_DATETIME_FORMAT	= "%Y-%m-%d %H:%M:%S";

	/** 
	 * 只有时间的输出格式：HHmmss 
	 */
	public static final String	DEF_ONLYTIME_FORMAT	= "%H%M%S";

	/** 
	 * 没有分隔符的时间格式：yyyyMMddHHmmss 
	 */
	public static final String	DEF_DATETIME_SEC	= "%Y%m%d%H%M%S";

	private static final Time	time				= new Time();

	/** 
	 * 私有构造方法，禁止对该类进行实例化 
	 */
	private TimeUtil ()
	{}

	/**
	 * 如果忽略线程同步，可以使用此公共time
	 * 
	 * @param seconds				要设置的时间， 单位second
	 * @return
	 */
	public static Time getTime(long seconds)
	{
		time.set(seconds * 1000);
		return time;
	}

	/**
	 * 如果忽略线程同步，可以使用此公共time
	 * 
	 * @return
	 */
	public static Time getTimeNow()
	{
		time.setToNow();
		return time;
	}

	/** 
	 * 得到用缺省方式格式化的当前日期 
	 * 
	 * @return 						当前日期 
	 */
	public static String getDate()
	{
		return getDateTime(DEF_DATE_FORMAT);
	}

	/** 
	 * 得到用缺省方式格式化的指定日期
	 * 
	 * @param seconds  				要设置的时间， 单位second
	 * 
	 * @return 						指定日期 
	 */
	public static String getDate(long seconds)
	{
		Time time = new Time();
		time.set(seconds * 1000);
		return getDateTime(DEF_DATE_FORMAT);
	}

	/** 
	 * 得到用缺省方式格式化的当前日期及时间 
	 * 
	 * @return 						当前日期及时间 
	 */
	public static String getDateTime()
	{
		return getDateTime(DEF_DATETIME_FORMAT);
	}

	/** 
	 * 得到用缺省方式格式化的当前日期及时间 
	 * 
	 * @param seconds  				要设置的时间， 单位second
	 * 
	 * @return 						指定日期及时间 
	 */
	public static String getDateTime(long seconds)
	{
		Time time = new Time();
		time.set(seconds * 1000);
		return getDateTime(time, DEF_DATETIME_FORMAT);
	}

	/** 
	 * 得到用缺省方式格式化的当前时间 
	 * 
	 * @return 						当前时间 
	 */
	public static String getOnlyTime()
	{
		return getDateTime(DEF_ONLYTIME_FORMAT);
	}

	/** 
	 * 得到用缺省方式格式化的指定时间 
	 * 
	 * @param seconds  				要设置的时间， 单位second
	 * 
	 * @return 						指定时间 
	 */
	public static String getOnlyTime(long seconds)
	{
		Time time = new Time();
		time.set(seconds * 1000);
		return getDateTime(time, DEF_ONLYTIME_FORMAT);
	}

	/** 
	 * 得到系统当前日期及时间，并用指定的方式格式化 
	 * 
	 * @param pattern 				显示格式 
	 * 
	 * @return 						当前日期及时间 
	 */
	public static String getDateTime(String pattern)
	{
		Time time = new Time();
		time.setToNow();
		return getDateTime(time, pattern);
	}

	/** 
	 * 得到用指定方式格式化的时间
	 * 
	 * @param time 					需要进行格式化的日期 
	 * @param pattern 				显示格式 
	 * 
	 * @return 						日期时间字符串 
	 */
	public static String getDateTime(Time time, String pattern)
	{
		if (null == pattern || "".equals(pattern))
		{
			pattern = DEF_DATETIME_FORMAT;
		}
		return time.format(pattern);
	}

	/** 
	 * 得到当前年份 
	 * 
	 * @return 						当前年份 
	 */
	public static int getCurrentYear()
	{
		Time time = new Time();
		time.setToNow();
		return time.year;
	}

	/** 
	 * 得到当前月份 
	 * 
	 * @return 				当前月份 
	 */
	public static int getCurrentMonth()
	{
		Time time = new Time();
		time.setToNow();
		return time.month + 1;
	}

	/** 
	 * 得到当前日 
	 * 
	 * @return 				当前日 
	 */
	public static int getCurrentDay()
	{
		Time time = new Time();
		time.setToNow();
		return time.monthDay;
	}

	/**
	 * 获取当前周天数 
	 * 
	 * @return				当前周的天数
	 */
	public static int getCurrentDayOfWeek()
	{
		Time time = new Time();
		time.setToNow();
		return time.weekDay;
	}

	/**
	 * 获取当前时
	 * 
	 * @return				当前时
	 */
	public static int getCurrentHour()
	{
		Time time = new Time();
		time.setToNow();
		return time.hour;
	}

	/**
	 * 获取当前分
	 * 
	 * @return				当前分
	 */
	public static int getCurrentMinute()
	{
		Time time = new Time();
		time.setToNow();
		return time.minute;
	}

	/**
	 *  获取当前秒
	 * 
	 * @return				当前秒
	 */
	public static int getCurrentSecond()
	{
		Time time = new Time();
		time.setToNow();
		return time.second;
	}

	/**
	 * 计算日期和现在相差数。
	 * 
	 * @param milliSecond		指定时间的毫秒数
	 * 
	 * @return
	 */
	public static String diffNow(long milliSecond)
	{
		Time timeNow = new Time();
		timeNow.setToNow();
		Time time = new Time();
		time.set(milliSecond);
		return diff(timeNow, time);
	}

	/**
	 * 计算日期和现在相差数。
	 * 
	 * @param time				指定时间
	 * 
	 * @return
	 */
	public static String diffNow(Time time)
	{
		Time timeNow = new Time();
		timeNow.setToNow();
		return diff(timeNow, time);
	}

	/** 
	 * 计算两个日期相差。 
	 * 
	 * @param one 		第一个日期数，作为基准 
	 * @param two 		第二个日期数，作为比较 
	 * 
	 * @return 			两个日期相差时间 
	 */
	public static String diff(Time one, Time two)
	{
		long diffTime = diffDays(one, two);
		if (diffTime > 365)
		{
			return diffTime / 365 + "年";
		}

		if (diffTime > 31)
		{
			return diffTime / 31 + "月";
		}

		if (diffTime != 0)
		{
			return diffTime + "天";
		}

		diffTime = diffHours(one, two);
		if (diffTime != 0)
		{
			return diffTime + "小时";
		}

		return diffMinutes(one, two) + "分钟";
	}

	/** 
	 * 计算两个日期相差分钟数。 
	 * 
	 * @param one 			第一个日期数，作为基准 
	 * @param two 			第二个日期数，作为比较 
	 * 
	 * @return 				相差分钟数
	 */
	public static long diffMinutes(Time one, Time two)
	{
		return (one.toMillis(true) - two.toMillis(true)) / (60 * 1000);
	}

	/** 
	 * 计算两个日期相差小时数。 
	 * @param one 			第一个日期数，作为基准 
	 * @param two 			第二个日期数，作为比较 
	 * 
	 * @return 				相差小时数
	 */
	public static long diffHours(Time one, Time two)
	{
		return (one.toMillis(true) - two.toMillis(true)) / (60 * 60 * 1000);
	}

	/** 
	 * 计算两个日期相差天数。 
	 * 
	 * @param one 			第一个日期数，作为基准 
	 * @param two			 第二个日期数，作为比较 
	 * 
	 * @return 				两个日期相差天数 
	 */
	public static long diffDays(Time one, Time two)
	{
		return (one.toMillis(true) - two.toMillis(true)) / (24 * 60 * 60 * 1000);
	}

	/**
	 * 登陆时服务器给的时间
	 */
	public static long	beforeSeconds;
	/**
	 * 登陆时系统的流逝时间
	 */
	public static long	beforeElapse;

	/**
	 * 根据流逝时间获取真实时间
	 * 
	 * @return
	 */
	public static long getCurTimeSecondsByElapse()
	{
		return beforeSeconds + (SystemClock.elapsedRealtime() / 1000 - beforeElapse);
	}

}
