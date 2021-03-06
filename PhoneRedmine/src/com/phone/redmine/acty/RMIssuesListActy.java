package com.phone.redmine.acty;

import java.util.ArrayList;
import org.apache.http.Header;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.phone.redmine.R;
import com.phone.redmine.adapter.RMIssuesAdapter;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.entity.RMIssue;
import com.phone.redmine.entity.RMIssueDetailTransferData;
import com.phone.redmine.entity.RMIssueFilter;
import com.phone.redmine.http.RMHttpResponse;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.http.entity.RMIssuesReply;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.view.zrclistview.FCZrcListView;
import com.phone.redmine.view.zrclistview.ZrcListView;
import com.phone.redmine.view.zrclistview.ZrcListView.OnItemClickListener;
import com.phone.redmine.view.zrclistview.ZrcListView.OnStartListener;

/**
 * RM 问题列表 页面
 */
public class RMIssuesListActy extends BaseSecLevelActy implements OnItemClickListener
{

	/**
	 * 进入 问题列表过滤器  页面的标识
	 */
	public static final int		RESULT_RMISSUEFILTERACTY	= 10;

	/**
	 * 进入 问题添加  页面的标识
	 */
	public static final int		RESULT_RMISSUEADDACTY		= 11;

	/**
	 * 进入 问题查看  页面的标识
	 */
	public static final int		RESULT_RMISSUEVIEWACTY		= 12;

	private RMIssuesAdapter		adapter;
	private FCZrcListView		lstView;
	private ArrayList<RMIssue>	lstIssueInfo				= new ArrayList<RMIssue>();
	private TextView			emptyTv;
	private int					projectId;
	private int					curPage;
	private RMIssueFilter		issueFilter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_zrclistview);
		setActionbarTitle("问题列表");
		if (savedInstanceState != null)
		{
			issueFilter = savedInstanceState.getParcelable("issueFilter");
			projectId = savedInstanceState.getInt("projectId");
		}
		if (issueFilter == null)
		{
			issueFilter = getIntent().getParcelableExtra("issueFilter");
			projectId = getIntent().getIntExtra("projectId", 0);
		}

		if (issueFilter == null)
		{
			setActionbarExtra2Background(R.drawable.actionbar_filter_selector);
			setActionbarExtraBackground(R.drawable.actionbar_add_selector);
		}

		initRes();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		notifyAdapter();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2)
	{
		if (arg1 == RESULT_OK)
		{
			lstView.refresh();
		}
	}

	@Override
	protected void onActionbarExtra2Click()
	{
		Intent intent = new Intent();
		intent.setClass(this, RMFastIssueFilterActy.class);
		intent.putExtra("type", 2);
		startActivityForResult(intent, RESULT_RMISSUEFILTERACTY);
	}

	@Override
	protected void onActionbarExtraClick()
	{
		RMIssueDetailTransferData data = new RMIssueDetailTransferData();
		data.projectId = projectId;

		Intent intent = new Intent();
		intent.setClass(this, RMIssueAddEditActy.class);
		intent.putExtra("issueDetailTransferData", data);
		startActivityForResult(intent, RESULT_RMISSUEFILTERACTY);
	}

	@Override
	public void onItemClick(ZrcListView parent, View view, int position, long id)
	{
		if (position >= 0 && position < lstIssueInfo.size())
		{
			RMIssue itemInfo = lstIssueInfo.get(position);
			if (itemInfo != null)
			{
				Intent intent = new Intent();
				intent.putExtra("issueId", itemInfo.id);
				intent.setClass(this, RMIssueViewActy.class);
				startActivityForResult(intent, RESULT_RMISSUEVIEWACTY);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (issueFilter != null)
		{
			outState.putParcelable("issueFilter", issueFilter);
			outState.putInt("projectId", projectId);
		}
	}

	private void initRes()
	{
		lstView = (FCZrcListView) findViewById(R.id.commonzrclistview_listview);
		emptyTv = (TextView) findViewById(R.id.commonzrclistview_emptytext);
		emptyTv.setGravity(Gravity.CENTER);
		lstView.setEmptyView(emptyTv);
		lstView.setOnItemClickListener(this);

		/* 下拉刷新事件回调*/
		lstView.setOnRefreshStartListener(new OnStartListener()
		{

			@Override
			public void onStart()
			{
				emptyTv.setText("正在加载……");
				curPage = 0;
				loadPage(curPage);
			}
		});

		/* 加载更多事件回调*/
		lstView.setOnLoadMoreStartListener(new OnStartListener()
		{

			@Override
			public void onStart()
			{
				loadPage(curPage);
			}
		});
		lstView.refresh();
	}

	/*
	 * 下拉刷新完成
	 * 
	 * @param success			刷新是否成功
	 */
	private void onRefreshComplete(boolean success)
	{
		if (lstIssueInfo.size() == 0)
		{
			emptyTv.setText("无内容");
		} else
		{
			emptyTv.setText("");
		}
		if (success)
		{
			lstView.setRefreshSuccess("刷新成功");
		} else
		{
			lstView.setRefreshFail("刷新失败");
		}
		notifyAdapter();
	}

	/*
	 * 分页加载已加载全部
	 */
	private void onLoadAll()
	{
		lstView.stopLoadMore();
	}

	/*
	 * 分页加载开始加载
	 */
	private void startLoad()
	{
		lstView.startLoadMore();
	}

	/*
	 * 分页加载 加载一页完成
	 */
	private void onLoadComplete()
	{
		if (lstIssueInfo.size() == 0)
		{
			emptyTv.setText("没有相关记录");
		} else
		{
			emptyTv.setText("");
		}
		lstView.setLoadMoreSuccess();
		notifyAdapter();
	}

	/*
	 * 刷新列表显示
	 */
	private void notifyAdapter()
	{
		if (adapter == null)
		{
			adapter = new RMIssuesAdapter(this, lstIssueInfo, false);
			lstView.setAdapter(adapter);
		} else
		{
			adapter.notifyDataSetChanged();
		}
	}

	/*
	 * 加载分页数据
	 * 
	 * @param page		页码
	 */
	private void loadPage(final int page)
	{
		StringBuilder url = new StringBuilder(RMHttpUrl.URL_ISSUES);
		url.append("?limit=").append(RMConst.PAGELIMIT);
		url.append("&offset=").append(page * RMConst.PAGELIMIT);
		if (projectId > 0)
		{
			url.append("&project_id=").append(projectId);
		}
		checkIssueFilter(url);

		RMHttpUtil.addCommonSessionHeader();
		RMHttpUtil.client.get(this, url.toString(), new RMHttpResponse(null, this, RMHttpResponse.SUCCESSCODE_200)
		{

			boolean	isSuccess;

			@Override
			public void success(Header[] headers, String response)
			{
				if (headers != null && response.length() > 0)
				{
					isSuccess = true;
					//将数据添加到列表
					try
					{
						//此为下拉刷新
						if (page == 0)
						{
							lstIssueInfo.clear();
						}
						Gson gson = new Gson();
						RMIssuesReply ps = gson.fromJson(response, RMIssuesReply.class);

						//有子项目的项目
						if (ps != null && ps.issues != null && ps.issues.size() > 0)
						{
							lstIssueInfo.addAll(ps.issues);

							//如果為下拉刷新，如果第一页滿，申请第二页，否则不再申请第二页数据
							if (page == 0 && ps.total_count >= ps.limit)
							{
								startLoad();
							}
						} else
						{
							onLoadAll();
						}
					} catch (Exception e)
					{
						e.printStackTrace();
						onLoadAll();
					}
					curPage++;
				}
			}

			@Override
			public void failed(String failedReason)
			{
				super.failed(failedReason);
				isSuccess = false;
				onLoadAll();
			}

			@Override
			public void finish()
			{
				if (page == 0)
				{
					onRefreshComplete(isSuccess);
				} else
				{
					onLoadComplete();
				}
			}
		});
	}

	/*
	 * 根据过滤器拼接URL
	 * 
	 * @param builder
	 */
	private void checkIssueFilter(StringBuilder builder)
	{
		//如果高级过滤为空，默认使用项目的低级过滤器
		if (issueFilter == null)
		{
			String strStatus = SharePrefUtil.getInstance().getString(RMConst.SHARE_ISSUEFILTER_PROJECT_STATUS, RMConst.ISSUEFILTER_SORT_STATUS_NOTCLOSED);
			String strCloumn = SharePrefUtil.getInstance().getString(RMConst.SHARE_ISSUEFILTER_PROJECT_CLOUMN, RMConst.ISSUEFILTER_SORT_CLOUMN_ISSUEID);
			String strSort = SharePrefUtil.getInstance().getString(RMConst.SHARE_ISSUEFILTER_PROJECT_SORT, RMConst.ISSUEFILTER_SORT_DESC);
			builder.append("&status_id").append(strStatus);
			builder.append("&sort=").append(strCloumn).append(strSort);
		} else
		{
			if (issueFilter.project_id > 0)
			{
				builder.append("&project_id=").append(issueFilter.project_id);
			}
			if (issueFilter.assigned_to_id > 0)
			{
				builder.append("&assigned_to_id=").append(issueFilter.assigned_to_id);
			}
			if (issueFilter.status_id > 0)
			{
				builder.append("&status_id=").append(issueFilter.status_id);
			} else if (issueFilter.status_id == 0)
			{
				builder.append("&status_id=*");
			}
			if (issueFilter.category_id > 0)
			{
				builder.append("&category_id=").append(issueFilter.category_id);
			}
			if (issueFilter.tracker_id > 0)
			{
				builder.append("&tracker_id=").append(issueFilter.tracker_id);
			}
			if (issueFilter.priority_id > 0)
			{
				builder.append("&priority_id=").append(issueFilter.priority_id);
			}
			if (issueFilter.sort != null && issueFilter.cloumn != null)
			{
				builder.append("&sort=").append(issueFilter.cloumn).append(issueFilter.sort);
			}
		}
	}
}
