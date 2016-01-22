package com.phone.redmine.acty;

import java.util.ArrayList;
import org.apache.http.Header;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.google.gson.Gson;
import com.phone.redmine.R;
import com.phone.redmine.biz.RMBiz;
import com.phone.redmine.biz.RMBiz.OnLoadComplete;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.RMPairBoolInfo;
import com.phone.redmine.http.RMHttpResponse;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.http.entity.RMLoginReply;
import com.phone.redmine.util.ActyUtil;
import com.phone.redmine.util.DESUtil;
import com.phone.redmine.util.DeviceUtil;
import com.phone.redmine.util.LogUtil;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.util.StringUtil;
import com.phone.redmine.view.FCDlg.FCParamSubmitInterface;
import com.phone.redmine.view.FCDlgEditOne;
import com.phone.redmine.view.FCProgressbar;
import com.phone.redmine.view.FCToast;
import com.phone.redmine.view.crouton.FCCrouton;

/**
 * RM 登录页面  
 */
public class RMLoginActy extends BaseActy implements OnClickListener
{

	private EditText	etName;
	private EditText	etPwd;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmacty_login);
		initRes();
		initData();
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.rmactylogin_submit)
		{
			login(etName.getText().toString(), etPwd.getText().toString());
		} else if (v.getId() == R.id.rmactylogin_seturl)
		{
			setUrl();
		}
	}

	private void initRes()
	{
		findViewById(R.id.rmactylogin_submit).setOnClickListener(this);
		findViewById(R.id.rmactylogin_seturl).setOnClickListener(this);
		etName = (EditText) findViewById(R.id.rmactylogin_name);
		etPwd = (EditText) findViewById(R.id.rmactylogin_pwd);
	}

	private void initData()
	{
		String str = SharePrefUtil.getInstance().getString(RMConst.SHARE_RMUSERNAME, null);
		if (StringUtil.isNotBlank(str))
		{
			etName.setText(str);
			etPwd.requestFocus();
			DeviceUtil.showSoftKeyBoardDelay(this, etPwd);
		} else
		{
			DeviceUtil.showSoftKeyBoardDelay(this, etName);
		}
	}

	/*
	 * 登陆前检查
	 * 
	 * @param strUserName		用户名
	 * @param strUserPwd		密码
	 * @return
	 */
	private boolean checkLogin(String strUserName, String strUserPwd)
	{
		boolean flag = false;
		if (StringUtil.isEmpty(SharePrefUtil.getInstance().getString("url", null)))
		{
			setUrl();
		} else if (StringUtil.isBlank(strUserName))
		{
			FCCrouton.alert(this, "用户名不能为空!");
		} else if (StringUtil.isBlank(strUserPwd))
		{
			FCCrouton.alert(this, "密码不能为空!");
		} else
		{
			flag = true;
		}
		return flag;
	}

	/*
	 * 登录
	 * 
	 * @param name		用户名
	 * @param pwd		密码
	 */
	private void login(final String name, final String pwd)
	{
		if (!checkLogin(name, pwd))
		{
			return;
		}

		RMHttpUtil.addCommonSessionHeader(name, pwd);
		RMHttpUtil.client.get(this, RMHttpUrl.URL_LOGIN, new RMHttpResponse(this, this, RMHttpResponse.SUCCESSCODE_200)
		{

			@Override
			public void success(Header[] headers, String response)
			{
				loginSuccess(response, name, pwd);
			}

			@Override
			public void failed(int statusCode, String failedReason)
			{
				FCCrouton.error(RMLoginActy.this, "用户名或密码错误！");
			}
		});
	}

	/*
	 * 登录成功，存储验证码以及用户名，全局用户变量设定
	 * 
	 * @param response		登录成功服务器返回的数据
	 * @param name			用户名
	 * @param pwd			密码
	 */
	private void loginSuccess(String response, String name, String pwd)
	{
		try
		{
			Gson gson = new Gson();
			RMLoginReply userInfo = gson.fromJson(response, RMLoginReply.class);
			if (userInfo != null && userInfo.user != null)
			{
				RMHttpUtil.setToken(userInfo.user.api_key);
				SharePrefUtil.getInstance().setInt(RMConst.SHARE_RMUSERID, userInfo.user.id);
				SharePrefUtil.getInstance().setString(RMConst.SHARE_RMUSERNAME, name);
				SharePrefUtil.getInstance().setString(RMConst.SHARE_RMPASSWORD, DESUtil.encrypt(pwd, RMConst.USER_PRIVATE_KEY));

				syncDbData();
			}
		} catch (Exception e)
		{
			LogUtil.w("解析登录报文失败", LogUtil.ExceptionToString(e));
		}
	}

	/**
	 * 设置链接的URL
	 */
	private void setUrl()
	{
		String savedUrl = SharePrefUtil.getInstance().getString("url", null);
		FCDlgEditOne dlg = new FCDlgEditOne(this, "设置URL", savedUrl);
		dlg.addSubmitListener(new FCParamSubmitInterface<String>()
		{

			@Override
			public boolean submit(String t)
			{
				if (StringUtil.isNotEmpty(t))
				{
					SharePrefUtil.getInstance().setString("url", t);
					RMHttpUrl.loadUrl();
					return true;
				}
				return false;
			}
		});
		dlg.show();
		DeviceUtil.showSoftKeyBoardDelay(this, dlg.getEditText());
	}

	/*
	 * 同步主数据库
	 */
	private void syncDbData()
	{
		ArrayList<RMPairBoolInfo> lstStaffs = new ArrayList<RMPairBoolInfo>();
		RMDBLocal.getInstance().getAllStaffs(lstStaffs);
		if (lstStaffs.size() == 0)
		{
			final FCProgressbar bar = new FCProgressbar(this);
			bar.showBar();
			bar.setMessage("正在同步数据库……");
			RMBiz.saveData(new OnLoadComplete()
			{

				@Override
				public void onComplete(boolean isSuccess)
				{
					bar.hideBar();
					if (!isSuccess)
					{
						FCToast.ToastShow("同步数据库出错，请进入后重新同步！");
					}
					RMLoginActy.this.finish();
					ActyUtil.startActivity(RMLoginActy.this, RMMainActy.class);
				}
			});
		} else
		{
			RMLoginActy.this.finish();
			ActyUtil.startActivity(RMLoginActy.this, RMMainActy.class);
		}
	}

}
