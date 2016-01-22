package com.phone.redmine.view;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.phone.redmine.R;
import com.phone.redmine.app.App;

/**
 * Toast封装提示
 */
public class FCToast
{

	private static Toast	toast;	// 提示框，静态全局

	/** 
	 * 普通提示
	 * 
	 * @param strInfo 		显示信息
	 * @param nKeepTime 	显示时间
	 */
	public static void ToastShow(int strInfo, int nKeepTime)
	{
		if (null == App.APPCONTEXT)
		{
			return;
		}
		ToastShow(App.APPCONTEXT.getString(strInfo), nKeepTime);
	}

	/** 
	 * 普通提示，   Toast.LENGTH_SHORT
	 * 
	 * @param strInfo 		显示信息
	 */
	public static void ToastShow(String strInfo)
	{
//		Drawable able = App.APPCONTEXT.getResources().getDrawable(R.drawable.logo_with_text_w);
		ToastShow(strInfo, Toast.LENGTH_SHORT, null);
	}

	/** 
	 * 普通提示
	 * 
	 * @param strInfo 		显示信息
	 * @param nKeepTime 	显示时间
	 */
	public static void ToastShow(String strInfo, int nKeepTime)
	{
//		Drawable able = App.APPCONTEXT.getResources().getDrawable(R.drawable.logo_with_text_w);
		ToastShow(strInfo, nKeepTime, null);
	}

	/** 
	 * 错误提示
	 * 
	 * @param strInfo 		显示信息
	 * @param nKeepTime 	显示时间
	 */
	public static void ToastShowError(int strInfo, int nKeepTime)
	{
		if (null == App.APPCONTEXT)
		{
			return;
		}
		ToastShowError(App.APPCONTEXT.getString(strInfo), nKeepTime);
	}

	/** 
	 * 错误提示
	 * 
	 * @param strInfo 		显示信息
	 * @param nKeepTime 	显示时间
	 */
	public static void ToastShowError(String strInfo, int nKeepTime)
	{
//		Drawable able = App.APPCONTEXT.getResources().getDrawable(R.drawable.toast_error);
		ToastShow(strInfo, nKeepTime, null);
	}

	/** 
	 * 警告提示
	 * 
	 * @param strInfo 		显示信息
	 * @param nKeepTime 	显示时间
	 */
	public static void ToastShowWarning(int strInfo, int nKeepTime)
	{
		if (null == App.APPCONTEXT)
		{
			return;
		}
		ToastShowWarning(App.APPCONTEXT.getString(strInfo), nKeepTime);
	}

	/** 
	 * 警告提示
	 * 
	 * @param strInfo 		显示信息
	 * @param nKeepTime 	显示时间
	 */
	public static void ToastShowWarning(String strInfo, int nKeepTime)
	{
//		Drawable able = App.APPCONTEXT.getResources().getDrawable(R.drawable.toast_warning);
		ToastShow(strInfo, nKeepTime, null);
	}

	/**
	 * 设置信息显示
	 * 
	 * @param message		显示信息
	 * @param nKeepTime		显示时间
	 * @param able			显示的图片
	 */
	public static void ToastShow(String message, int nKeepTime, Drawable able)
	{
		ToastInfo toastInfo = new ToastInfo();
		toastInfo.msg = message;
		toastInfo.nKeepTime = nKeepTime;
		toastInfo.able = able;
		MYHandler handler = new MYHandler(Looper.getMainLooper());
		Message msg = handler.obtainMessage();
		msg.arg1 = 0;
		msg.obj = toastInfo;
		handler.sendMessage(msg);
	}

	/*
	 * 显示Toast，必须主线程调用
	 * 
	 * @param strInfo		显示信息
	 * @param nKeepTime		显示时间
	 * @param able			显示的图片
	 */
	private static void ToastShowWithDrawble(String strInfo, int nKeepTime, Drawable able)
	{
		try
		{
			if (null == App.APPCONTEXT)
			{
				return;
			} else if (nKeepTime < 0)
			{
				nKeepTime = Toast.LENGTH_SHORT;
			}
			
			if (toast != null)
			{
				// toast.cancel(); //4.0中会出bug
				toast.setText(strInfo);
			} else
			{
				toast = Toast.makeText(App.APPCONTEXT, strInfo, nKeepTime);
				LinearLayout view = (LinearLayout) toast.getView();
				view.setBackgroundColor(Color.TRANSPARENT);
				TextView messageView = (TextView) view.getChildAt(0);
				messageView.setGravity(Gravity.CENTER_VERTICAL);
				messageView.setBackgroundColor(Color.parseColor("#aa363f44"));
				messageView.setPadding(2, 5, 5, 5);
				// view.setBackgroundColor(Color.rgb(30, 144, 255));
				// view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
				if (able != null)
				{
					able.setBounds(0, 0, able.getMinimumWidth(), able.getMinimumHeight());
					messageView.setCompoundDrawables(able, null, null, null);
				}
				messageView.setTextSize(16);
				messageView.setTextColor(Color.parseColor("#ffffff"));
				messageView.setShadowLayer(0, 0, 0, 0);
				toast.setView(view);
				//toast.setGravity(Gravity.TOP, 0, 53);
			}
			toast.show();
		} catch (Exception e)
		{
		}
	}

	/*
	 * 子线程与主线程的通讯
	 * 使子线程也可以通过通知主线程调用Toast
	 */
	static class MYHandler extends Handler
	{

		public MYHandler (Looper mainLooper)
		{
			super(mainLooper);
		}

		@Override
		public void handleMessage(Message msg)
		{
			if (msg != null && msg.arg1 == 0 && msg.obj != null)
			{
				ToastInfo info = (ToastInfo) msg.obj;
				ToastShowWithDrawble(info.msg, info.nKeepTime, info.able);
			}
		}
	}

	/*
	 * Toast信息携带类
	 */
	private static class ToastInfo
	{

		String		msg;
		int			nKeepTime;
		Drawable	able;
	}
}
