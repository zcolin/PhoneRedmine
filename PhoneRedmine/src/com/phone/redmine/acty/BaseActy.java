package com.phone.redmine.acty;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import com.phone.redmine.util.ActyUtil;

/** 
 * 所有Acitivity的基类，实现公共操作
 */
public class BaseActy extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ActyUtil.ACTIVITY_MAP.put(getClass(), this);
		if (isImmerse())
		{
			//透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			//透明导航栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

	}

	protected boolean isImmerse()
	{
		return true && VERSION.SDK_INT >= VERSION_CODES.KITKAT;
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		if (ActyUtil.ACTIVITY_MAP.containsKey(getClass()))
		{
			ActyUtil.ACTIVITY_MAP.remove(getClass());
		}
	}
}
