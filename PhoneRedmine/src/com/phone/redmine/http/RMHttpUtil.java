package com.phone.redmine.http;

import java.io.UnsupportedEncodingException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.util.StringUtil;

/**
 * RM Http工具类
 */
public class RMHttpUtil
{

	public static int				FAILURE_TIMEOUT		= 7000;
	public static final String		CONTENTTYPE_JSON	= "application/json";
	public static final String		HEAD_SESSION		= "X-Redmine-API-Key";
	public static AsyncHttpClient	client				= new AsyncHttpClient();	//实例话对象

	public static String			TOKEN;

	static
	{
		client.setTimeout(FAILURE_TIMEOUT);   //设置链接超时，如果不设置，默认为10s
	}

	/**
	 * 获取登录Token
	 * 
	 * @return		登录Token
	 */
	public static String getToken()
	{
		if (StringUtil.isEmpty(TOKEN))
		{
			TOKEN = SharePrefUtil.getInstance().getString(RMConst.SHARE_APIKEY, "");
		}
		return TOKEN;
	}

	/**
	 * 设置登录Token
	 * 
	 * @param token		登录Token
	 */
	public static void setToken(String token)
	{
		if (token == null)
		{
			SharePrefUtil.getInstance().removeString(RMConst.SHARE_APIKEY);
		} else
		{
			SharePrefUtil.getInstance().setString(RMConst.SHARE_APIKEY, token);
		}
		TOKEN = token;
	}

	/**
	 * 添加用户名密码Header(BasicAuth)
	 * 
	 * @param userName		用户名
	 * @param userPwd		密码
	 */
	public static void addCommonSessionHeader(String userName, String userPwd)
	{
		client.removeHeader(HEAD_SESSION);
		client.setBasicAuth(userName, userPwd);
	}

	/**
	 * 添加验证Token的Header
	 */
	public static void addCommonSessionHeader()
	{
		client.clearBasicAuth();
		client.addHeader(HEAD_SESSION, getToken());
	}

	/**
	 * 获取协议通用报文头 
	 * 
	 * @return			Header[]
	 */
	public static Header[] getCommonHeader()
	{
		Header signHead = new BasicHeader(HEAD_SESSION, SharePrefUtil.getInstance().getString(RMConst.SHARE_APIKEY, ""));
		return new Header[]{signHead};
	}

	/**
	 * 获取协议内容封装好的的Entity 
	 * 
	 * @param obj			可以被Gson解析的对象
	 * @return
	 */
	public static HttpEntity getStringEntity(Object obj)
	{
		Gson gson = new Gson();
		String strJson = gson.toJson(obj);
		return getStringEntity(strJson);
	}

	/**
	 * 获取协议内容封装好的的Entity 
	 * 
	 * @param str			Json数据
	 * @return
	 */
	public static HttpEntity getStringEntity(String str)
	{
		HttpEntity entity = null;
		try
		{
			entity = new StringEntity(str, "UTF-8");
		} catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		return entity;
	}
}
