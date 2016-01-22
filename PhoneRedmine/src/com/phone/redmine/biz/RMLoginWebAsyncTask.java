package com.phone.redmine.biz;

import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import android.app.Activity;
import android.os.AsyncTask;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.phone.redmine.app.App;
import com.phone.redmine.entity.RMPairStringInfo;
import com.phone.redmine.http.RMHttpResponse;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.util.SharePrefUtil;

/**
 * 异步登录Redmine Web端
 */
public class RMLoginWebAsyncTask extends AsyncTask<Integer,Integer,String[]>
{

	private Activity			barCtx;
	private Activity			infoCtx;
	private OnLoginComplete		listener;
	private RMPairStringInfo	userInfo;

	public RMLoginWebAsyncTask (Activity barCtx, Activity infoCtx, RMPairStringInfo userInfo, OnLoginComplete listener)
	{
		this.barCtx = barCtx;
		this.infoCtx = infoCtx;
		this.listener = listener;
		this.userInfo = userInfo;
	}

	@Override
	protected void onPostExecute(String[] result)
	{
		if (result != null)
		{
			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(RMHttpUtil.FAILURE_TIMEOUT);
			RequestParams request = new RequestParams();
			request.put("username", userInfo.str1);
			request.put("password", userInfo.str2);
			request.put("autologin", "0");
			request.put("login", "");
			request.put("back_url", "");
			request.put("authenticity_token", result[0]);
			client.addHeader("Cookie", result[1]);

			final PersistentCookieStore myCookieStore = new PersistentCookieStore(App.APPCONTEXT);
			client.setCookieStore(myCookieStore);
			client.post(RMHttpUrl.URLHEAD_REDMINE + "login", request, new RMHttpResponse(barCtx, infoCtx,
					RMHttpResponse.SUCCESSCODE_200)
			{

				@Override
				public void success(Header[] headers, String response)
				{
					boolean flag = false;

					/*将服务器返回的Cookie存到本地*/
					List<Cookie> lstCookie = myCookieStore.getCookies();
					for (int i = 0; i < lstCookie.size(); i++)
					{
						if ("_redmine_session".equals(lstCookie.get(i).getName()))
						{
							SharePrefUtil.getInstance().setString(RMConst.SHARE_REDMINECOOKIE, "_redmine_session=" + lstCookie.get(i).getValue());
							flag = true;
							break;
						}
					}

					if (listener != null)
					{
						if (flag)
						{
							listener.loginComplete(null);
						} else
						{
							listener.loginComplete("设置登录session失败");
						}
					}
				}

				@Override
				public void failed(int statusCode, String failedReason)
				{
					super.failed(statusCode, failedReason);
					if (listener != null)
					{
						listener.loginComplete("请求再登录失败:" + failedReason);
					}
				}
			});
		} else
		{
			if (listener != null)
			{
				listener.loginComplete("获取登录cookie和token失败！");
			}
		}
	}

	@Override
	protected String[] doInBackground(Integer... params)
	{
		String[] strResult = null;
		try
		{
			/*首先访问Redmine登录页，将Session取出，用于登录*/
			Response res = Jsoup.connect(RMHttpUrl.URLHEAD_REDMINE + "login").timeout(30000).execute();
			Map<String,String> cookies = res.cookies();
			Document doc = res.parse();
			Element et = doc.select("input[name=authenticity_token]").first();
			String ss = et.attr("value");
			strResult = new String[]{ss, "_redmine_session=" + cookies.get("_redmine_session")};
		} catch (Exception e1)
		{
			e1.printStackTrace();
		}
		return strResult;
	}

	/**
	 * @ClassName: OnLoginComplete  
	 * @Description: 登录执行完成 
	 * @author: WagnLin
	 * @date:2015年1月31日 下午12:43:40
	 */
	public interface OnLoginComplete
	{

		void loginComplete(String faiReason);
	}
}
