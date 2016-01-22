package com.phone.redmine.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import com.phone.redmine.app.App;

/**
 * 配置文件读写封装 
 */
public class SharePrefUtil
{

	private static SharePrefUtil		fcSharePrefUtil;
	private SharedPreferences			sp;
	private SharedPreferences.Editor	editor;

	public static SharePrefUtil getInstance()
	{
		if (fcSharePrefUtil == null)
		{
			fcSharePrefUtil = new SharePrefUtil();
		}
		return fcSharePrefUtil;
	}

	public SharePrefUtil ()
	{
		this(null);
	}

	/**
	 * 根据包名获取指定程序的SETINFO
	 *
	 * @param pckName
	 */
	public SharePrefUtil (String pckName)
	{
		Context context = App.APPCONTEXT;
		if (pckName != null && pckName.length() > 0)
		{
			try
			{
				context = App.APPCONTEXT.createPackageContext(pckName, Context.CONTEXT_IGNORE_SECURITY);
			} catch (NameNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		sp = context.getSharedPreferences("setinfo", Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public void setString(String key, String value)
	{
		editor.putString(key, value);
		editor.commit();
	}

	public String getString(String key, String value)
	{
		return sp.getString(key, value);
	}

	public void removeString(String key)
	{
		editor.remove(key);
		editor.commit();
	}

	public void setInt(String key, int value)
	{
		editor.putInt(key, value);
		editor.commit();
	}

	public int getInt(String key, int value)
	{
		return sp.getInt(key, value);
	}

	public void setBoolean(String key, boolean value)
	{
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key, Boolean value)
	{
		return sp.getBoolean(key, value);
	}

	public void setLong(String key, long value)
	{
		editor.putLong(key, value);
		editor.commit();
	}

	public long getLong(String key)
	{
		return sp.getLong(key, 0L);
	}

	public void setFloat(String key, float value)
	{
		editor.putFloat(key, value);
		editor.commit();
	}

	public float getFloat(String key)
	{
		return sp.getFloat(key, 0f);
	}
}
