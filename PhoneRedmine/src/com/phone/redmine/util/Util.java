package com.phone.redmine.util;

import android.annotation.SuppressLint;

/**
 * 工具类 
 */
public class Util
{

	/**
	 * 获取float格式化的字符串
	 * 
	 * @param fData			需要格式的数据
	 * @param precision		保留的位数
	 * 
	 * @return				格式化后的字符串
	 */
	@SuppressLint("DefaultLocale")
	public static String formatStringFloat_precision(float fData, int precision)
	{
		return String.format("%." + precision + "f", fData);
	}
}
