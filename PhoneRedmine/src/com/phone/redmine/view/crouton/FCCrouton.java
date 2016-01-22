package com.phone.redmine.view.crouton;

import com.phone.redmine.view.crouton.Style.Builder;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/** @Description:Crouton封装提示。
 * @author wanglin
 * @date 2013-12-23 上午11:14:26 Histroy: */
public class FCCrouton
{
	public static final int			RedLight				= 0xccfc6e51;
	public static final int			GreenLight				= 0xcc8cc152;
	public static final int			YelloLight				= 0xccf0bd3e;
	private static final Style		ERROR					= new Builder()
																	.setBackgroundColorValue(RedLight)
																	.setTextSize(17)
																	.setPaddingInDP(10)
																	.build();
	private static final Style		ALERT					= new Builder()
																	.setBackgroundColorValue(YelloLight)
																	.setTextSize(17)
																	.setPaddingInDP(10)
																	.build();
	private static final Style		INFO					= new Builder()
																	.setBackgroundColorValue(GreenLight)
																	.setTextSize(17)
																	.setPaddingInDP(10)
																	.build();
	private static Configuration	CONFIGURATION_CROUTON	= new Configuration.Builder()
																	.setDuration(Configuration.DURATION_SHORT)
																	.build();
	private static Crouton			crouton;									// 提示框，静态全局

	public static void info(Activity acty, String strInfo)
	{
		info(acty, strInfo, Configuration.DURATION_SHORT);
	}

	public static void info(Activity acty, String strInfo, int duration)
	{
		info(acty, strInfo, 0, duration);
	}

	public static void info(Activity acty, String strInfo, int viewGroupResId, int duration)
	{
		croutonShow(acty, strInfo, INFO, viewGroupResId, duration);
	}

	public static void alert(Activity acty, String strInfo)
	{
		alert(acty, strInfo, Configuration.DURATION_SHORT);
	}

	public static void alert(Activity acty, String strInfo, int duration)
	{
		alert(acty, strInfo, 0, duration);
	}

	public static void alert(Activity acty, String strInfo, int viewGroupResId, int duration)
	{
		croutonShow(acty, strInfo, ALERT, viewGroupResId, duration);
	}

	public static void error(Activity acty, String strInfo)
	{
		error(acty, strInfo, Configuration.DURATION_SHORT);
	}

	public static void error(Activity acty, String strInfo, int duration)
	{
		error(acty, strInfo, 0, duration);
	}

	public static void error(Activity acty, String strInfo, int viewGroupResId, int duration)
	{
		croutonShow(acty, strInfo, ERROR, viewGroupResId, duration);
	}
	
	public static void cancel()
	{
		if (crouton != null)
		{
			crouton.cancel();
		}
	}

	/** 设置信息显示 **/
	public static void croutonShow(Activity acty, String strInfo, Style style, int viewGroupResId, int duration)
	{
		CroutonInfo CroutonInfo = new CroutonInfo();
		CroutonInfo.acty = acty;
		CroutonInfo.strInfo = strInfo;
		CroutonInfo.style = style;
		CroutonInfo.viewGroupResId = viewGroupResId;
		CroutonInfo.duration = duration;
		MYHandler handler = new MYHandler(Looper.getMainLooper());
		Message msg = handler.obtainMessage();
		msg.arg1 = 0;
		msg.obj = CroutonInfo;
		handler.sendMessage(msg);
	}

	static class MYHandler extends Handler
	{
		public MYHandler(Looper mainLooper)
		{
			super(mainLooper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			if (msg != null && msg.arg1 == 0 && msg.obj != null)
			{
				CroutonInfo info = (CroutonInfo) msg.obj;
				CroutonShowWithDrawble(info.acty, info.strInfo, info.style, info.viewGroupResId, info.duration);
			}
		}
	}

	//显示Crouton
	private static void CroutonShowWithDrawble(Activity acty, String strInfo, Style style, int viewGroupResId, int duration)
	{
		if (acty == null || strInfo == null)
		{
			return;
		}
		try
		{
			if (CONFIGURATION_CROUTON.getDuration() != duration)
			{
				CONFIGURATION_CROUTON = new Configuration.Builder()
						.setDuration(duration)
						.build();
			}
			if (crouton != null)
			{
				crouton.cancel();
			}
			if (viewGroupResId == 0)
			{
				crouton = Crouton.makeText(acty, strInfo, style);
			} else
			{
				crouton = Crouton.makeText(acty, strInfo, style, viewGroupResId);
			}
			crouton.setConfiguration(CONFIGURATION_CROUTON);
			crouton.show();
		} catch (Exception e)
		{
		}
	}

	static class CroutonInfo
	{
		Activity	acty;
		Style		style;
		String		strInfo;
		int			duration;
		int			viewGroupResId;
	}
}
