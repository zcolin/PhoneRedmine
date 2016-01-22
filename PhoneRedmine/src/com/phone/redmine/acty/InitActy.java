package com.phone.redmine.acty;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.RelativeLayout;
import com.phone.redmine.R;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.biz.RMLoginWebAsyncTask;
import com.phone.redmine.biz.RMLoginWebAsyncTask.OnLoginComplete;
import com.phone.redmine.entity.RMPairStringInfo;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.util.DESUtil;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.util.StringUtil;

/**
 * 初始化页面 , 程序的启动Activity
 */
public class InitActy extends Activity
{

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acty_init);

		resetData();
		setInitBg();

		init();
	}

	/**
	 * 登录web REDMINE的帐号 
	 * 
	 * @param acty1				ProgressBar的Activity对象
	 * @param acty2				请求完数据通知的Crouton对象
	 * @param listener			登录结束回调
	 */
	public static void loginRedmineWeb(Activity acty1, Activity acty2, OnLoginComplete listener)
	{
		if (StringUtil.isNotBlank(RMHttpUtil.getToken()))
		{
			RMPairStringInfo redmine = new RMPairStringInfo();
			redmine.str1 = SharePrefUtil.getInstance().getString(RMConst.SHARE_RMUSERNAME, null);
			redmine.str2 = SharePrefUtil.getInstance().getString(RMConst.SHARE_RMPASSWORD, null);
			if (redmine.str1 != null && redmine.str2 != null)
			{
				try
				{
					redmine.str2 = DESUtil.decrypt(redmine.str2, RMConst.USER_PRIVATE_KEY);
					new RMLoginWebAsyncTask(acty1, acty2, redmine, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);;	//登录RedmineWeb
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/*
	 * 重置数据
	 */
	private void resetData()
	{
		RMHttpUtil.TOKEN = null;
	}

	/*
	 * 设置背景图片
	 */
	private void setInitBg()
	{
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.actyinit_main_rl);
		rl.setBackgroundColor(getResources().getColor(R.color.red_light));
	}

	/*
	 * 加载完成回调
	 */
	private void init()
	{
		loginRedmineWeb(null, null, null);

		Intent intent = new Intent();
		intent.setClass(this, RMMainActy.class);
		startActivity(intent);
		this.finish();
	}
}
