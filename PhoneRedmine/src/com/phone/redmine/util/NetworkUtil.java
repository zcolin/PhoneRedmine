package com.phone.redmine.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络连接工具类
 */
public class NetworkUtil
{

	/**
	 * 判断wifi是否连接
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static boolean isWifiConnect(Context context)
	{
		return getConnectedType(context) == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 判断移动数据是否连接
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static boolean isMobileConnect(Context context)
	{
		return getConnectedType(context) == ConnectivityManager.TYPE_MOBILE;
	}

	/**
	 * 判断网络是否可用 
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context)
	{
		return getConnectedType(context) != -1;
	}

	/**
	 * 获取当前网络连接的类型信息, -1：没有网络, 0：移动网络, 1：WIFI网络, 2：wap网络, 3：net网络
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static int getConnectedType(Context context)
	{
		if (context != null)
		{
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable())
			{
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}

	/**
	 * 获取本机Ip地址， 可以获取Gprs和wifi的
	 * 
	 * @return
	 */
	public String getLocalIp()
	{
		try
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress())
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex)
		{
		}
		return null;
	}
}
