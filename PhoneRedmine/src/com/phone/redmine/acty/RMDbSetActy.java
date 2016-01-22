package com.phone.redmine.acty;

import java.util.ArrayList;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.biz.RMBiz;
import com.phone.redmine.biz.RMBiz.OnLoadComplete;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.RMPairBoolInfo;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.view.FCProgressbar;
import com.phone.redmine.view.FCToast;

/**
 * RM 数据库设置 页面
 */
public class RMDbSetActy extends BaseSecLevelActy implements OnClickListener
{

	private TextView	tvSyncTime;
	private TextView	tvSync;
	private TextView	tvTracker;
	private TextView	tvStatus;
	private TextView	tvUsers;
	private TextView	tvPriority;
	private TextView	tvVersion;
	private TextView	tvCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmacty_dbset);
		setActionbarTitle("数据库设置");
		initRes();
		initData();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rmactydbset_btnsync:
				syncDB();
				break;
			default:
				break;
		}
	}

	private void initRes()
	{
		tvSyncTime = (TextView) findViewById(R.id.rmactydbset_synctime);
		tvSync = (TextView) findViewById(R.id.rmactydbset_btnsync);
		tvTracker = (TextView) findViewById(R.id.rmactydbset_tvtracker);
		tvStatus = (TextView) findViewById(R.id.rmactydbset_tvstatus);
		tvUsers = (TextView) findViewById(R.id.rmactydbset_tvusers);
		tvPriority = (TextView) findViewById(R.id.rmactydbset_tvpriority);
		tvVersion = (TextView) findViewById(R.id.rmactydbset_tvversion);
		tvCategory = (TextView) findViewById(R.id.rmactydbset_tvcategory);

		tvSync.setOnClickListener(this);
	}

	private void initData()
	{
		RMDBLocal db = RMDBLocal.getInstance();
		ArrayList<RMPairBoolInfo> lstInfos = new ArrayList<RMPairBoolInfo>();
		StringBuilder sbInfos = new StringBuilder();
		
		/*显示问题状态*/
		db.getAllIssueStatus(lstInfos);
		int size = lstInfos.size();
		for (int i = 0; i < size; i++)
		{
			sbInfos.append(lstInfos.get(i).name);
			if (i != size - 1)
			{
				sbInfos.append("\n");
			}
		}
		tvStatus.setText(sbInfos);

		/*显示优先级*/
		sbInfos.setLength(0);
		lstInfos.clear();
		db.getAllPriorities(lstInfos);
		size = lstInfos.size();
		for (int i = 0; i < size; i++)
		{
			sbInfos.append(lstInfos.get(i).name);
			if (i != size - 1)
			{
				sbInfos.append("\n");
			}
		}
		tvPriority.setText(sbInfos);

		/*显示人员*/
		sbInfos.setLength(0);
		lstInfos.clear();
		db.getAllStaffs(lstInfos);
		size = lstInfos.size();
		for (int i = 0; i < size; i++)
		{
			sbInfos.append(lstInfos.get(i).name);
			if (i != size - 1)
			{
				sbInfos.append("\n");
			}
		}
		tvUsers.setText(sbInfos);

		/*显示跟踪状态*/
		sbInfos.setLength(0);
		lstInfos.clear();
		db.getAllTrackers(lstInfos);
		size = lstInfos.size();
		for (int i = 0; i < size; i++)
		{
			sbInfos.append(lstInfos.get(i).name);
			if (i != size - 1)
			{
				sbInfos.append("\n");
			}
		}
		tvTracker.setText(sbInfos);

		/*显示版本*/
		sbInfos.setLength(0);
		lstInfos.clear();
		db.getAllVersions(lstInfos);
		size = lstInfos.size();
		for (int i = 0; i < size; i++)
		{
			sbInfos.append(lstInfos.get(i).name);
			if (i != size - 1)
			{
				sbInfos.append("\n");
			}
		}
		tvVersion.setText(sbInfos);

		/*显示问题种类*/
		sbInfos.setLength(0);
		lstInfos.clear();
		db.getAllIssueCategorys(lstInfos);
		size = lstInfos.size();
		for (int i = 0; i < size; i++)
		{
			sbInfos.append(lstInfos.get(i).name);
			if (i != size - 1)
			{
				sbInfos.append("\n");
			}
		}
		tvCategory.setText(sbInfos);

		tvSyncTime.setText("上次同步：" + SharePrefUtil.getInstance().getString(RMConst.SHARE_DBSYNCTIME, null));
	}

	/*
	 * 同步主数据库
	 */
	private void syncDB()
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
					FCToast.ToastShow("同步数据库出错，请重试！");
				} else
				{
					initData();
				}
			}
		});
	}

}
