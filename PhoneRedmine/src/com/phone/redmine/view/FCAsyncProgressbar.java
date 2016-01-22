package com.phone.redmine.view;

import java.lang.ref.WeakReference;
import com.phone.redmine.entity.FCProcessInfo;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

/**
 * 进度条封装类，执行的内容会自动在子线程中调用
 * 需要实现DoInterface接口
 */
public class FCAsyncProgressbar extends AsyncTask<Integer,Integer,FCProcessInfo>
{

	private FCProgressbar	processBar;
	private DoInterface		doInter;
	private MYHandler		handler;

	public FCAsyncProgressbar (Context context)
	{
		processBar = new FCProgressbar(context);
		handler = new MYHandler(this);
	}

	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		processBar.showBar();
	}

	@Override
	protected FCProcessInfo doInBackground(Integer... params)
	{
		FCProcessInfo info = null;
		if (doInter != null)
		{
			info = doInter.onDoInback();
		}

		return info;
	}

	@Override
	protected void onPostExecute(FCProcessInfo info)
	{
		super.onPostExecute(info);
		processBar.hideBar();
		if (doInter != null)
			doInter.onPostExecute(info);
	}

	/**
	 * 添加任务回调接口
	 * 
	 * @param doInter		
	 */
	public void addDoInterface(DoInterface doInter)
	{
		this.doInter = doInter;
	}

	/**
	 * 设置信息显示，此方法需要在回调接口中调用
	 * 
	 * @param message		进度条显示信息
	 */
	public void setMessageInBack(String message)
	{
		Message msg = handler.obtainMessage();
		msg.arg1 = 0;
		msg.obj = message;
		handler.sendMessage(msg);
	}

	/**
	 * 进度条显示
	 */
	public void show()
	{
		this.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
	}

	/**
	 * 处理子线程任务和主线程之间的交互
	 */
	static class MYHandler extends Handler
	{

		FCAsyncProgressbar	proBar;

		public MYHandler (FCAsyncProgressbar proBar)
		{
			WeakReference<FCAsyncProgressbar> weakProBar = new WeakReference<FCAsyncProgressbar>(proBar);
			this.proBar = weakProBar.get();
		}

		@Override
		public void handleMessage(Message msg)
		{
			if (msg != null && msg.arg1 == 0 && msg.obj != null)
			{
				this.proBar.processBar.setMessage((String) msg.obj);
			}
		}
	}

	/**
	 * 任务回调接口
	 */
	public interface DoInterface
	{

		FCProcessInfo onDoInback();

		void onPostExecute(FCProcessInfo info);
	}

}
