package com.phone.redmine.util;

/**
 * 字节处理工具类
 */
public class ByteUtil
{

	/**
	 * 字符序列转换为10进制字符串 , 转换大小端
	 * 
	 * @param b
	 * 
	 * @return
	 */
	public static String toIntStingConvertByteOrder(byte[] b)
	{
		if (b.length < 4)
		{
			return "转换十进制失败，十六进制为:" + toHexStringConvertByteOrder(b);
		}

		int s = 0;
		int s0 = b[0] & 0xff;// 最低位
		int s1 = b[1] & 0xff;
		int s2 = b[2] & 0xff;
		int s3 = b[3] & 0xff;
		s3 <<= 24;
		s2 <<= 16;
		s1 <<= 8;
		s = s0 | s1 | s2 | s3;
		return String.valueOf(s);
	}

	/**
	 * 字符序列转换为16进制字符串 	, 转换大小端
	 * 
	 * @param src
	 * 
	 * @return
	 */
	public static StringBuilder toHexStringConvertByteOrder(byte[] src)
	{
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0)
		{
			return stringBuilder;
		}

		for (int i = src.length - 1; i >= 0; i--)
		{
			String hv = Integer.toHexString(src[i] & 0xFF);
			if (hv.length() == 1)
			{
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder;
	}

	/**
	 * 字符序列转换为固定长度的16进制字符串，位数不足补0 	, 转换大小端
	 * 
	 * @param src		
	 * @param fixLength				固定长度
	 * 
	 * @return
	 */
	public static String toFixedLengthHexStringConvertByteOrder(byte[] src, int fixLength)
	{
		StringBuilder sb = toHexStringConvertByteOrder(src);
		while (sb.length() < fixLength)
		{
			sb.insert(0, "0");
		}
		return sb.toString();
	}

	/**
	 * 字符序列转换为固定长度的16进制字符串，位数不足补0
	 * 
	 * @param src		
	 * @param fixLength				固定长度
	 * 
	 * @return
	 */
	public static String toFixedLengthHexString(byte[] src, int fixLength)
	{
		StringBuilder sb = toHexString(src);
		while (sb.length() < fixLength)
		{
			sb.insert(0, "0");
		}
		return sb.toString();
	}

	/**
	 * 字符序列转换为16进制字符串 	
	 * 
	 * @param src
	 * 
	 * @return
	 */
	public static StringBuilder toHexString(byte[] src)
	{
		if (src == null || src.length <= 0)
		{
			return null;
		}

		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = 0; i < src.length; i++)
		{
			String hv = Integer.toHexString(src[i] & 0xFF);
			if (hv.length() == 1)
			{
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder;
	}
}
