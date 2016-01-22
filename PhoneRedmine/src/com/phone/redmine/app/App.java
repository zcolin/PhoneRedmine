package com.phone.redmine.app;

import android.app.Application;
import android.content.Context;

/**
 * 程序入口
 */
public class App extends Application
{
	/**
	 * 全局Context
	 */
	public static Context		APPCONTEXT;

	@Override
	public void onCreate()
	{
		APPCONTEXT = this;
		super.onCreate();
	}
}
