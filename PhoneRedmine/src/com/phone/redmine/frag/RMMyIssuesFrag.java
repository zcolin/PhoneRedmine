package com.phone.redmine.frag;

import java.util.ArrayList;
import org.apache.http.Header;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.google.gson.Gson;
import com.phone.redmine.R;
import com.phone.redmine.acty.RMIssueViewActy;
import com.phone.redmine.adapter.RMIssuesAdapter;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.entity.RMIssue;
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
 * RM 我的任务 页面
 */
public class RMMyIssuesFrag extends FragSupportLazyLoadBase implements OnClickListener, OnItemClickListener
{

	/**
	 * 进入 问题查看  页面的标识
	 */
	public static final int		RESULT_RMISSUEVIEWACTY	= 12;

	private ArrayList<RMIssue>	lstIssueInfo			= new ArrayList<RMIssue>();
	private RMIssuesAdapter		adapter;
	private FCZrcListView		lstView;
	private TextView			tvHeaderSwitch;
	private int					curPageType;										//0指派给我的问题，1我发布的问题
	private int					curPage;											//当前的页面 ， 从0开始
	private TextView			emptyTv;

	public static RMMyIssuesFrag newInstance()
	{
		final RMMyIssuesFrag issuesFrag = new RMMyIssuesFrag();
		return issuesFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void lazyLoad()
	{
		lstView = (FCZrcListView) rootView.findViewById(R.id.rmfragmyissue_listview);
		emptyTv = (TextView) rootView.findViewById(R.id.rmfragmyissue_emptytext);
		tvHeaderSwitch = (TextView) rootView.findViewById(R.id.rmfragmyissue_btntopswitch);
		emptyTv.setGravity(Gravity.CENTER);
		lstView.setEmptyView(emptyTv);
		tvHeaderSwitch.setOnClickListener(this);
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

		refresh();
		
		View progressBar = rootView.findViewById(R.id.rmfragmyissue_progressbar);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	protected int getRootViewLayId()
	{
		return R.layout.rmfrag_myissue;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == Activity.RESULT_OK)
		{
			refresh();
		}
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
				intent.setClass(getActivity(), RMIssueViewActy.class);
				startActivityForResult(intent, RESULT_RMISSUEVIEWACTY);
			}
		}
	}

	@Override
	public void onClick(View v)
	{
		if (v.getId() == R.id.rmfragmyissue_btntopswitch)
		{
			curPageType = curPageType == 0 ? 1 : 0;
			refresh();
		}
	}

	/**
	 * 刷新列表
	 */
	public void refresh()
	{
		if (curPageType == 0)
		{
			tvHeaderSwitch.setText("指派给我的问题");
		} else
		{
			tvHeaderSwitch.setText("我发布的问题");
		}

		lstView.refresh();
	}

	/*
	 * 下拉刷新结束
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
	 * 分页加载 已加载全部
	 */
	private void onLoadAll()
	{
		lstView.stopLoadMore();
	}

	/*
	 * 开始分页加载 
	 */
	private void startLoad()
	{
		lstView.startLoadMore();
	}

	/*
	 * 分页加载完一页
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
	 * 刷新列表
	 */
	private void notifyAdapter()
	{
		if (adapter == null)
		{
			if (getActivity() != null && !getActivity().isFinishing())
			{
				adapter = new RMIssuesAdapter(getActivity(), lstIssueInfo, true);
				lstView.setAdapter(adapter);
			}
		} else
		{
			adapter.notifyDataSetChanged();
		}
	}

	/*
	 * 加载分页数据
	 * 
	 * @param page		要加载的页码
	 */
	private void loadPage(final int page)
	{
		String strStatus = SharePrefUtil.getInstance().getString(RMConst.SHARE_ISSUEFILTER_MY_STATUS, RMConst.ISSUEFILTER_SORT_STATUS_NOTCLOSED);
		String strCloumn = SharePrefUtil.getInstance().getString(RMConst.SHARE_ISSUEFILTER_MY_STATUS, RMConst.ISSUEFILTER_SORT_CLOUMN_ISSUEID);
		String strSort = SharePrefUtil.getInstance().getString(RMConst.SHARE_ISSUEFILTER_MY_STATUS, RMConst.ISSUEFILTER_SORT_DESC);

		StringBuilder url = new StringBuilder(RMHttpUrl.URL_ISSUES);
		url.append("?limit=").append(RMConst.PAGELIMIT);
		url.append("&offset=").append(page * RMConst.PAGELIMIT);
		url.append("&status_id").append(strStatus);
		url.append("&sort=").append(strCloumn).append(strSort);
		if (curPageType == 0)
		{
			url.append("&assigned_to_id=me");
		} else
		{
			url.append("&author_id=me");
		}

		RMHttpUtil.addCommonSessionHeader();
		RMHttpUtil.client.get(getActivity(), url.toString(), new RMHttpResponse(RMHttpResponse.SUCCESSCODE_200)
		{

			boolean	isSuccess;

			@Override
			public void success(Header[] headers, String response)
			{
				if (headers != null && response.length() > 0)
				{
					isSuccess = true;
					try
					{
						//此为下拉刷新
						if (page == 0)
						{
							lstIssueInfo.clear();
						}
						Gson gson = new Gson();
						RMIssuesReply ps = gson.fromJson(response, RMIssuesReply.class);

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
}
