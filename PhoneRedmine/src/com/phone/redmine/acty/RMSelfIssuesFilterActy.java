package com.phone.redmine.acty;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.adapter.RMSelfIssueFilterAdapter;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.RMIssueFilter;

/**
 * 
 * RM 自定义查询页面
 */
public class RMSelfIssuesFilterActy extends BaseSecLevelActy implements OnItemClickListener, OnItemLongClickListener
{

	/**
	 * 进入 高级问题列表过滤器  页面的标识
	 */
	public static final int				RESULT_ADVANCEDISSUEFILTER	= 10;

	private RMSelfIssueFilterAdapter	adapter;
	private ListView					lstView;
	private TextView					emptyTv;
	private ArrayList<RMIssueFilter>	lstSelfFilter				= new ArrayList<RMIssueFilter>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_listview);
		setActionbarTitle("自定义查询列表");
		setActionbarExtraBackground(R.drawable.actionbar_add_selector);
		initRes();
		initData();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2)
	{
		if (arg1 == RESULT_OK)
		{
			initData();
		}
	}

	@Override
	protected void onActionbarExtraClick()
	{
		Intent intent = new Intent();
		intent.putExtra("type", 1);
		intent.setClass(this, RMAdvancedIssueFilterActy.class);
		startActivityForResult(intent, RESULT_ADVANCEDISSUEFILTER);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		Intent intent = new Intent();
		intent.putExtra("issueFilter", lstSelfFilter.get(position));
		intent.setClass(this, RMIssuesListActy.class);
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
	{
		final RMIssueFilter filter = lstSelfFilter.get(position);
		String[] arrItems = new String[]{"编辑", "删除"};
		new AlertDialog.Builder(this)
				.setItems(arrItems, new DialogInterface.OnClickListener()
				{

					public void onClick(DialogInterface dialog, int which)
					{
						switch (which)
						{
							case 0:
								Intent intent = new Intent();
								intent.putExtra("type", 1);
								intent.putExtra("filter", filter);
								intent.setClass(RMSelfIssuesFilterActy.this, RMAdvancedIssueFilterActy.class);
								startActivityForResult(intent, RESULT_ADVANCEDISSUEFILTER);
								break;
							case 1:
								RMDBLocal.getInstance().deleteIssueSelfFilter(filter.id);
								initData();
								break;
							default:
								break;
						}
					}
				}).show();
		return true;
	}

	private void initRes()
	{
		lstView = (ListView) findViewById(R.id.commonlistview_listview);
		emptyTv = (TextView) findViewById(R.id.commonlistview_emptytext);
		emptyTv.setGravity(Gravity.CENTER);
		lstView.setEmptyView(emptyTv);
		lstView.setOnItemClickListener(this);
		lstView.setOnItemLongClickListener(this);
	}

	private void initData()
	{
		lstSelfFilter.clear();
		RMDBLocal.getInstance().getAllIssueSelfFilter(lstSelfFilter);
		if (lstSelfFilter.size() == 0)
		{
			emptyTv.setText("暂无自定义过滤器");
		}
		notifyAdapter();
	}

	private void notifyAdapter()
	{
		if (adapter == null)
		{
			adapter = new RMSelfIssueFilterAdapter(this, lstSelfFilter);
			lstView.setAdapter(adapter);
		} else
		{
			adapter.notifyDataSetChanged();
		}
	}

}
