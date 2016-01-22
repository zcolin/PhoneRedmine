package com.phone.redmine.frag;

import java.io.File;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.phone.redmine.R;
import com.phone.redmine.acty.RMAdvancedIssueFilterActy;
import com.phone.redmine.acty.RMDbSetActy;
import com.phone.redmine.acty.RMSearchActy;
import com.phone.redmine.acty.RMSelfIssuesFilterActy;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.util.ActyUtil;
import com.phone.redmine.util.FileUtil;
import com.phone.redmine.view.FCDlg.FCSubmitInterface;
import com.phone.redmine.view.FCDlgComm;
import com.phone.redmine.view.crouton.FCCrouton;

/**
 * RM 功能页面
 */
public class RMFunctionFrag extends FragSupportLazyLoadBase implements OnClickListener
{
	public static RMFunctionFrag newInstance()
	{
		final RMFunctionFrag funcFrag = new RMFunctionFrag();
		return funcFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void lazyLoad()
	{
		rootView.findViewById(R.id.rmfragfunction_searchkeyword).setOnClickListener(this);
		rootView.findViewById(R.id.rmfragfunction_filterresult).setOnClickListener(this);
		rootView.findViewById(R.id.rmfragfunction_selfsearch).setOnClickListener(this);
		rootView.findViewById(R.id.rmfragfunction_dbset).setOnClickListener(this);
		rootView.findViewById(R.id.rmfragfunction_clearattachment).setOnClickListener(this);
		rootView.findViewById(R.id.rmfragfunction_logout).setOnClickListener(this);
	}

	@Override
	protected int getRootViewLayId()
	{
		return R.layout.rmfrag_function;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rmfragfunction_searchkeyword:
				ActyUtil.startActivity(getActivity(), RMSearchActy.class);
				break;
			case R.id.rmfragfunction_filterresult:
				ActyUtil.startActivity(getActivity(), RMAdvancedIssueFilterActy.class);
				break;
			case R.id.rmfragfunction_selfsearch:
				ActyUtil.startActivity(getActivity(), RMSelfIssuesFilterActy.class);
				break;
			case R.id.rmfragfunction_dbset:
				ActyUtil.startActivity(getActivity(), RMDbSetActy.class);
				break;
			case R.id.rmfragfunction_clearattachment:
				File file = new File(RMConst.PATH_REDMINEATTACHMENT + "/");
				if (file.exists())
				{
					FileUtil.delete(file);
					FCCrouton.info(getActivity(), "清除成功");
				} else
				{
					FCCrouton.info(getActivity(), "没有附件文件");
				}
				break;
			case R.id.rmfragfunction_logout:
				FCDlgComm dlg = new FCDlgComm(getActivity(), "提示", "确定要注销当前账号么？");
				dlg.addSubmitListener(new FCSubmitInterface()
				{

					@Override
					public boolean submit()
					{
						RMHttpUtil.setToken(null);
						getActivity().finish();
						return true;
					}
				});
				dlg.show();
				break;
			default:
				break;
		}
	}

}
