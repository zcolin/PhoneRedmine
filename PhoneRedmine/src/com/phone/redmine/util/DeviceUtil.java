package com.phone.redmine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.phone.redmine.app.App;

/**
 * 设备工具类，操作设备工具，如关机，点亮屏幕等
 */
public class DeviceUtil
{

	/**
	 * 延时弹出软键盘（软键盘即时弹出有时会出现bug）
	 * 
	 * @param context
	 * @param view			The currently focused view, which would like to receive soft keyboard input	
	 */
	public static void showSoftKeyBoardDelay(final Context context, final View view)
	{
		new Handler().postDelayed(new Runnable()
		{

			@Override
			public void run()
			{
				alertKeyBoard(context, view);
			}
		}, 500);
	}

	/** 
	 * 弹出软键盘 
	 * 
	 * @param context
	 * @param view			The currently focused view, which would like to receive soft keyboard input
	 */
	public static void alertKeyBoard(Context context, View view)
	{
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, 0);
	}

	/** 
	 * 隐藏软键盘 
	 * 
	 * @param context
	 * @param view			The currently focused view, which would like to receive soft keyboard input
	 */
	public static void hideKeyBoard(Context context, View view)
	{
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 切换弹出关闭软键盘
	 * 
	 * @param context
	 * @param view			The currently focused view, which would like to receive soft keyboard input
	 */
	public static void toggleKeyBoard(Context context, View view)
	{
		InputMethodManager m = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		m.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
	}

	/**
	 * 设置屏保时间
	 * 
	 * @param context
	 * @param time				屏保时间秒
	 */
	public static void setScreenSaverTime(Context context, int time)
	{
		Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, time);
	}

	/**
	 *  获取系统屏保时间 
	 * 
	 * @param context
	 * 
	 * @return					屏保时间秒
	 */
	public static int getScreenSaverTime(Context context)
	{
		int result = 0;
		try
		{
			result = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 点亮屏幕
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public static void acquireScreenOn(Context context)
	{
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
		mWakeLock.acquire();
		mWakeLock.release();
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	/** 
	 *  解锁屏幕
	 *  
	 *	标记值						CPU					屏幕				键盘
	 *	PARTIAL_WAKE_LOCK			开启					关闭				关闭
	 *	SCREEN_DIM_WAKE_LOCK		开启					调暗（Dim）		关闭
	 *	SCREEN_BRIGHT_WAKE_LOCK		开启					调亮（Bright）	关闭
	 *	FULL_WAKE_LOCK				开启					调亮（Bright）	调亮（Bright）
	 *
	 *
	 * @param context 
	 */
	@SuppressWarnings("deprecation")
	public static void acquireWakeLock(Context context)
	{
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (!pm.isScreenOn())
		{
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
					PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "SimpleTimer");
			if (!wl.isHeld())
			{
				wl.acquire();
				wl.release();
			}
		}
	}

	/**
	 * 关掉屏幕
	 * 
	 * @param context
	 */
	public static void acquireUNWakeLock(Context context)
	{
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		if (pm.isScreenOn())
		{
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WhatEver");
			wl.acquire();
			wl.release();
		}
	}

	/** 
	 * 禁用键盘锁 
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public static void disableKeylock(Context context)
	{
		KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		android.app.KeyguardManager.KeyguardLock mKeyguardLock = mKeyguardManager.newKeyguardLock("");
		mKeyguardLock.disableKeyguard();
	}

	/**
	 * 更换系统设置语言
	 * 
	 * @param context
	 * @param lan		如:Locale.US.getLanguage()
	 */
	public static void setLanguage(Context context, String lan)
	{
		String stg = Locale.SIMPLIFIED_CHINESE.getLanguage();
		Configuration config = context.getResources().getConfiguration();
		if (lan.equals(stg))
		{
			config.locale = Locale.SIMPLIFIED_CHINESE;
		}
		else
		{
			config.locale = Locale.US;
		}
		try
		{
			context.getResources().updateConfiguration(config, null);
		} catch (Exception e)
		{
			LogUtil.w("FCUtil--setLanguage", "updateConfiguration failed!");
		}
	}

	/**
	 * 设置字体缩放级别
	 * 
	 * @param ctx
	 * @param scale				缩放倍数
	 */
	public static void settingSysFontScale(Context ctx, float scale)
	{
		try
		{
			Resources res = ctx.getResources();
			Configuration cfg = res.getConfiguration();
			cfg.fontScale = scale;
			res.updateConfiguration(cfg, null);
		} catch (Exception re)
		{
			LogUtil.w("FCUtil--settingSysFontScale", "updateConfiguration failed!");
		}
	}

	/**
	 * 获取设备唯一码
	 * 
	 * @return		设备码
	 */
	public static String getDeviceId()
	{
		if (App.APPCONTEXT != null)
		{
			String deviceId = ((TelephonyManager) App.APPCONTEXT.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
			return deviceId;
		}
		return "";
	}

	/**
	 * @Title: isCameraCanUse  
	 * @Description: 判断后置摄像头是否可用 
	 * @param @return 
	 * @return boolean 
	 * @throws
	 */
	public static boolean isBackCameraCanUse()
	{
		boolean canUse = true;
		Camera mCamera = null;
		try
		{
			mCamera = Camera.open();
			if (mCamera == null)
			{
				canUse = false;
			}
		} catch (Exception e)
		{
			canUse = false;
		}
		if (canUse)
		{
			mCamera.release();
			mCamera = null;
		}
		return canUse;
	}

	/**
	 * @Title: isCameraCanUse  
	 * @Description: 判断前置摄像头是否可用 
	 * @param @return 
	 * @return boolean 
	 * @throws
	 */
	public static boolean isFrontCameraCanUse()
	{
		boolean canUse = false;
		Camera mCamera = null;
		try
		{
			int count = Camera.getNumberOfCameras();
			for (int i = 0; i < count; i++)
			{
				CameraInfo info = new CameraInfo();
				Camera.getCameraInfo(i, info);
				if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
				{
					mCamera = Camera.open(i);
					if (mCamera != null)
					{
						canUse = true;
						break;
					}
				}
			}
		} catch (Exception e)
		{
		}
		if (canUse)
		{
			mCamera.release();
			mCamera = null;
		}
		return canUse;
	}

	/**
	 * 获得系统可用内存信息
	 * 
	 * @param context
	 * 
	 * @return			返回KB
	 */
	public static long getAvailMemorySize(Context context)
	{
		ActivityManager actMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 获得MemoryInfo对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 获得系统可用内存，保存在MemoryInfo对象上
		actMgr.getMemoryInfo(memoryInfo);
		long memSize = memoryInfo.availMem;
		return memSize / 1024;
	}

	/**
	 * 获得系统可用内存信息
	 * 
	 * @param context
	 * 
	 * @return			可用内存大小的字符串表示
	 */
	public static String getAvailMemorySizeStr(Context context)
	{
		long size = getAvailMemorySize(context);
		String availMemStr = Formatter.formatFileSize(context, size * 1024);
		return availMemStr;
	}

	/**
	 * 获取总设备内存
	 * 
	 * @param context
	 * 
	 * @return			返回Byte
	 */
	public static long getTotalMemory(Context context)
	{
		// 系统内存信息文件
		String str1 = "/proc/meminfo";
		String str2;
		String[] arrayOfString = null;
		long initial_memory = 0;
		BufferedReader localBufferedReader = null;
		try
		{
			FileReader localFileReader = new FileReader(str1);
			localBufferedReader = new BufferedReader(localFileReader, 8192);
			// 读取meminfo第一行，系统总内存大小
			str2 = localBufferedReader.readLine();
			if (str2 != null)
				arrayOfString = str2.split("\\s+");
			// 获得系统总内存，单位是KB
			if (arrayOfString != null)
			{
				initial_memory = Long.valueOf(arrayOfString[1]).longValue();
			}
		} catch (IOException e)
		{
			LogUtil.d("FCDeviceLog--getTotalMemory", LogUtil.ExceptionToString(e));
		} finally
		{
			if (localBufferedReader != null)
			{
				try
				{
					localBufferedReader.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return initial_memory;
	}

	/**
	 * 获得系统总内存信息
	 * 
	 * @param context
	 * 
	 * @return			可用内存大小的字符串表示
	 */
	public static String getTotalMemoryStr(Context context)
	{
		long iMemory = getTotalMemory(context);
		return Formatter.formatFileSize(context, iMemory * 1024);
	}

	/**
	 * 获取CPU信息， /proc/cpuinfo文件中第一行是CPU的型号，第二行是CPU的频率
	 * 
	 * @return
	 */
	public static String[] getCUPinfo()
	{
		String str1 = "/proc/cpuinfo";
		String[] cpuInfo = {"", ""};
		String str2 = null;
		String[] arrayOfString = null;
		BufferedReader localBufferedReader = null;
		try
		{
			FileReader fr = new FileReader(str1);
			localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			if (str2 != null)
				arrayOfString = str2.split("\\s+");
			if (arrayOfString != null)
			{
				for (int i = 2; i < arrayOfString.length; i++)
				{
					cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
				}
			}
			str2 = localBufferedReader.readLine();
			if (str2 != null)
				arrayOfString = str2.split("\\s+");
			if (arrayOfString != null)
				cpuInfo[1] += arrayOfString[2];
		} catch (Exception e)
		{
			LogUtil.d("FCDeviceLog--getCUPinfo", LogUtil.ExceptionToString(e));
		} finally
		{
			if (localBufferedReader != null)
			{
				try
				{
					localBufferedReader.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		return cpuInfo;
	}

	/**
	 * 获取SDCard大小
	 * 
	 * @return			sdCardInfo[0] 总大小 , sdCardInfo[1] 可用大小
	 */
	@SuppressWarnings({"deprecation"})
	public long[] getSDCardMemory()
	{
		long[] sdCardInfo = new long[2];
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long bSize = sf.getBlockSize();
			long bCount = sf.getBlockCount();
			long availBlocks = sf.getAvailableBlocks();
			sdCardInfo[0] = bSize * bCount;//总大小  
			sdCardInfo[1] = bSize * availBlocks;//可用大小  
		}
		return sdCardInfo;
	}

	/**
	 * 获取SDCard格式化字符串后的size
	 * 
	 * @return			sdCardInfo[0] 总大小 , sdCardInfo[1] 可用大小
	 */
	public String[] getSDCardMemoryStr(Context context)
	{
		long[] sdCardInfo = getSDCardMemory();
		String[] arrStr = new String[2];
		arrStr[0] = Formatter.formatFileSize(context, sdCardInfo[0] * 1024);//总大小  
		arrStr[1] = Formatter.formatFileSize(context, sdCardInfo[1] * 1024);//可用大小  
		return arrStr;
	}

	static long	total	= 0;
	static long	idle	= 0;

	/**
	 * 获取Cup占用大小
	 * 
	 * @return
	 */
	public static double getCupAllUsed()
	{
		boolean isFrist = false;
		if (total == 0 && idle == 0)
		{
			isFrist = true;
		}
		double usage = 0d;
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/stat")), 1000);
			String load = reader.readLine();
			reader.close();
			String[] toks = load.split(" ");
			long currTotal = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4]);
			long currIdle = Long.parseLong(toks[5]);
			usage = (currTotal - total) * 100.0f / (currTotal - total + currIdle - idle);
			total = currTotal;
			idle = currIdle;
			if (isFrist)
			{
				return getCupAllUsed();
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		return usage;
	}

	/**
	 *  获取已开机时长
	 * 
	 * @param mContext
	 * @return				秒
	 */
	public static long getOpenTimes(Context mContext)
	{
		return SystemClock.elapsedRealtime() / 1000;
	}

	/**
	 * 获取格式化后的已开机时长
	 * 
	 * @param mContext
	 * 
	 * @return				格式化的字符串
	 */
	public static String getOpenTimeStr(Context mContext)
	{
		long ut = getOpenTimes(mContext);
		if (ut == 0)
		{
			ut = 1;
		}
		int m = (int) ((ut / 60) % 60);
		int h = (int) ((ut / 3600));
		return h + "时" + m + "分";
	}
}
