package com.phone.redmine.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity 相关操作 工具类
 */
public class ActyUtil
{

	public static HashMap<Class<?>,Activity>	ACTIVITY_MAP	= new HashMap<Class<?>,Activity>();

	/**
	 * 结束所有Activity
	 */
	public static void finishAllActivity()
	{
		Iterator<Entry<Class<?>,Activity>> iter = ACTIVITY_MAP.entrySet().iterator();
		while (iter.hasNext())
		{
			Map.Entry<Class<?>,Activity> entry = (Map.Entry<Class<?>,Activity>) iter.next();
			Activity acty = entry.getValue();
			if (acty != null && !acty.isFinishing())
			{
				acty.finish();
			}
		}

		clearActivityMap();
	}

	/**
	 * 结束某个Activity
	 * 
	 * @param classObj	Activity的class对象
	 */
	public static void finishActivity(Class<?>... classObj)
	{
		if (classObj != null && classObj.length > 0)
		{
			for (Class<?> obj : classObj)
			{
				if (obj != null)
				{
					Activity acty = ACTIVITY_MAP.get(obj);
					if (acty != null && !acty.isFinishing())
					{
						acty.finish();
					}
				}
			}
		}
	}

	/**
	 * 清空map列表
	 */
	public static void clearActivityMap()
	{
		ACTIVITY_MAP.clear();
	}

	/**
	 *  开启新的Activity 
	 * 
	 * @param context
	 * @param cls			
	 */
	public static void startActivity(Context context, Class<?> cls)
	{
		Intent intent = new Intent();
		intent.setClass(context, cls);
		context.startActivity(intent);
	}

	/**
	 * 开启新的Activity 
	 * 
	 * @param context
	 * @param cls
	 * @param bundle
	 */
	public static void startActivity(Context context, Class<?> cls, Bundle bundle)
	{
		Intent intent = new Intent();
		intent.setClass(context, cls);
		intent.putExtra("data", bundle);
		context.startActivity(intent);
	}

	/**
	 * 开启新的Activity 
	 * 
	 * @param context
	 * @param cls
	 * @param requestCode
	 */
	public static void startActivityForResult(Activity context, Class<?> cls, int requestCode)
	{
		Intent intent = new Intent();
		intent.setClass(context, cls);
		context.startActivityForResult(intent, requestCode);
	}
}
