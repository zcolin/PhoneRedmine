package com.phone.redmine.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 设备显示相关处理 工具类
 */
public class DisplayUtil
{

	private static float	DENSITY;		// 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
	private static int		DENSITYDPI;	// 屏幕密度（每寸像素：120/160/240/320） 
	private static int		WINDOW_WIDTH;	// 屏幕宽度
	private static int		WINDOW_HEIGHT;	// 屏幕高度

	/**
	 * 获取屏幕Density
	 * 
	 * @param context
	 * 
	 * @return			Density
	 */
	public static float getDensity(Context context)
	{
		if (DENSITY == 0)
		{
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			DENSITY = dm.density;
		}
		return DENSITY;
	}

	/**
	 * 获取屏幕DensityDpi
	 * 
	 * @param context
	 * 
	 * @return				DensityDpi
	 */
	public static int getDensityDpi(Context context)
	{
		if (DENSITYDPI == 0)
		{
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			DENSITYDPI = dm.densityDpi;
		}
		return DENSITYDPI;
	}

	/**
	 * 获取屏幕宽（尺寸小的）
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenWidth(Context context)
	{
		if (WINDOW_WIDTH == 0)
		{
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			WINDOW_WIDTH = dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels;
		}
		return WINDOW_WIDTH;
	}

	/**
	 * 获取屏幕高（尺寸大的）
	 * 
	 * @param context
	 * @return
	 */
	public static int getScreenHeight(Context context)
	{
		if (WINDOW_HEIGHT == 0)
		{
			DisplayMetrics dm = context.getResources().getDisplayMetrics();
			WINDOW_HEIGHT = dm.heightPixels > dm.widthPixels ? dm.heightPixels : dm.widthPixels;
		}
		return WINDOW_HEIGHT;
	}

	/**
	 * 将px值转换为dp值
	 * 
	 * @param context
	 * @param pxValue		px像素值
	 * 
	 * @return				dp
	 */
	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将dip或dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dipValue
	 * @param scale
	 *            （DisplayMetrics类中属性density）
	 * @return
	 */
	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue)
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue)
	{
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * @Title: snapShotWithStatusBar  
	 * @Description: 获取当前屏幕截图，包含状态栏 
	 * @param @param activity
	 * @param @return 
	 * @return Bitmap 
	 * @throws
	 */
	public static Bitmap snapShotWithStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
		view.destroyDrawingCache();
		return bp;
	}

	/**
	 * @Title: snapShotWithoutStatusBar  
	 * @Description: 获取当前屏幕截图，不包含状态栏 
	 * @param @param activity
	 * @param @return 
	 * @return Bitmap 
	 * @throws
	 */
	public static Bitmap snapShotWithoutStatusBar(Activity activity)
	{
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmp = view.getDrawingCache();
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int width = getScreenWidth(activity);
		int height = getScreenHeight(activity);
		Bitmap bp = null;
		bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
		view.destroyDrawingCache();
		return bp;
	}

	/**
	 * 	精确获取屏幕尺寸（例如：3.5、4.0、5.0寸屏幕） 
	 * 
	 * @param 		activity
	 * 
	 * @return		设备尺寸		
	 */
	public static double getScreenPhysicalSize(Activity activity)
	{
		DisplayMetrics dm = activity.getResources().getDisplayMetrics();
		double diagonalPixels = Math.sqrt(Math.pow(dm.widthPixels, 2) + Math.pow(dm.heightPixels, 2));
		return diagonalPixels / (dm.densityDpi);
	}

	/**
	 * 判断是否是平板（官方用法）
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static boolean isTablet(Context context)
	{
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}

	/**
	 * 用于获取状态栏的高度。
	 * 
	 * @return 返回状态栏高度的像素值。
	 */
	public static int getStatusBarHeight(Activity context)
	{
		int statusBarHeight = 0;
		
		try
		{
			Resources res = context.getResources();
			int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
			if (resourceId > 0)
			{
				statusBarHeight = res.getDimensionPixelSize(resourceId);
            }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if (statusBarHeight == 0)
		{
			Rect frame = new Rect();
			context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
			statusBarHeight = frame.top;
		}
		return statusBarHeight;
	}
}
