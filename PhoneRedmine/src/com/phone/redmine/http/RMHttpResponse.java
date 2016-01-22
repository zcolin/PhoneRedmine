package com.phone.redmine.http;

import org.apache.http.Header;
import org.apache.http.conn.HttpHostConnectException;
import android.app.Activity;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;
import com.phone.redmine.app.App;
import com.phone.redmine.http.entity.RMErrorInfo;
import com.phone.redmine.util.LogUtil;
import com.phone.redmine.util.NetworkUtil;
import com.phone.redmine.util.StringUtil;
import com.phone.redmine.view.FCProgressbar;
import com.phone.redmine.view.crouton.FCCrouton;

/**
 * RM 自定义了ProgressBar进度条的显示 和失败提示
 */
public class RMHttpResponse extends TextHttpResponseHandler
{

	public static final int	SUCCESSCODE_201	= 201;
	public static final int	SUCCESSCODE_200	= 200;

	private FCProgressbar	proBar;					//请求过程中的进度条
	private int				successCode;			//成功标志码
	private Activity		toastActy;				//完成应答后显示Crouton的Acty对象

	/**
	 * @param successCode		成功标志码
	 */
	public RMHttpResponse (int successCode)
	{
		this(null, null, successCode);
	}

	/**
	 * @param barActy		进度条Atvicity实体
	 * @param toastActy		完成应答后的提示信息Ativity实体
	 * @param successCode	成功标志码
	 */
	public RMHttpResponse (Activity barActy, Activity toastActy, int successCode)
	{
		this.toastActy = toastActy;
		if (barActy != null)
		{
			proBar = new FCProgressbar(barActy);
		}
		this.successCode = successCode;
	}

	@Override
	public void onStart()
	{
		if (proBar != null)
		{
			proBar.showBar();
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, String response)
	{

		LogUtil.d("REDMINE HTTP RESPONSE:", response);
		if (statusCode == successCode)
		{
			success(headers, response);
		} else
		{
			failed(statusCode, getErrorMsg(response) + "(" + statusCode + ")");
		}
	}

	@Override
	public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e)
	{
		String str;
		if (StringUtil.isBlank(responseBody) && e != null)
		{
			if (e instanceof HttpHostConnectException || statusCode == 0)
			{
				if (!NetworkUtil.isNetworkAvailable(App.APPCONTEXT))
				{
					str = "当前无网络连接，请开启网络！";
				} else
				{
					str = "连接服务器失败(" + statusCode + "), 请检查网络或稍后重试";
				}
			} else
			{
				str = e.getMessage() + "(" + statusCode + ")";
			}
		} else
		{
			str = getErrorMsg(responseBody) + "(" + statusCode + ")";
		}
		failed(statusCode, str);
	}

	@Override
	public void onFinish()
	{
		if (proBar != null)
		{
			proBar.hideBar();
		}
		finish();
	}

	/**
	 * 将除了自定义的成功状态之外的失败汇聚到此函数 
	 * 
	 * @param failedReason		失败原因
	 */
	public void failed(String failedReason)
	{
		FCCrouton.error(toastActy, failedReason);
	}

	/**
	 * 将除了自定义的成功状态之外的失败汇聚到此函数
	 * 
	 * @param statusCode		状态码
	 * @param failedReason		失败原因
	 */
	public void failed(int statusCode, String failedReason)
	{
		failed(failedReason);
	}

	/**
	 * 应答成功回调函数
	 * 
	 * @param headers			headers
	 * @param response			应答信息
	 */
	public void success(Header[] headers, String response)
	{}

	/**
	 * 结束链接回调 
	 */
	public void finish()
	{}

	/*
	 * 获取自定义的Json错误信息 
	 * 
	 * @param strError		json信息
	 * 
	 * @return				从json中提取的错误数据
	 */
	private String getErrorMsg(String strError)
	{
		String strErr;
		try
		{
			Gson gson = new Gson();
			StringBuilder sb = new StringBuilder();
			RMErrorInfo errorInfo = gson.fromJson(strError, RMErrorInfo.class);
			if (errorInfo != null && errorInfo.errors != null && errorInfo.errors.size() > 0)
			{
				int size = errorInfo.errors.size();
				for (int i = 0; i < size; i++)
				{
					sb.append(errorInfo.errors.get(0));
					if (i != size - 1)
					{
						sb.append(".");
					}
				}
			}
			strErr = sb.toString();
		} catch (Exception e)
		{
			strErr = strError;
		}
		return strErr;
	}
}
