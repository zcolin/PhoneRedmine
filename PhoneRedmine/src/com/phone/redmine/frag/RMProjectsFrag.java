package com.phone.redmine.frag;

import java.util.ArrayList;
import org.apache.http.Header;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.phone.redmine.R;
import com.phone.redmine.acty.RMIssuesListActy;
import com.phone.redmine.acty.RMLoginActy;
import com.phone.redmine.adapter.RMProjectsAdapter;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.entity.RMProject;
import com.phone.redmine.http.RMHttpResponse;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.http.entity.RMProjectsReply;
import com.phone.redmine.util.ActyUtil;
import com.phone.redmine.view.FCToast;
import com.phone.redmine.view.zrclistview.FCZrcListView;
import com.phone.redmine.view.zrclistview.ZrcListView;
import com.phone.redmine.view.zrclistview.ZrcListView.OnItemClickListener;
import com.phone.redmine.view.zrclistview.ZrcListView.OnStartListener;

/**
 * RM 项目列表页 
 */
public class RMProjectsFrag extends FragSupportLazyLoadBase implements OnItemClickListener
{

	private ArrayList<RMProject>	lstProjects	= new ArrayList<RMProject>();
	private RMProjectsAdapter		adapter;
	private FCZrcListView			lstView;
	private TextView				tvEmpty;
	private int						curPage;

	public static RMProjectsFrag newInstance()
	{
		final RMProjectsFrag shopNewsFrag = new RMProjectsFrag();
		return shopNewsFrag;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void lazyLoad()
	{
		lstView = (FCZrcListView) rootView.findViewById(R.id.commonzrclistview_listview);
		tvEmpty = (TextView) rootView.findViewById(R.id.commonzrclistview_emptytext);
		tvEmpty.setGravity(Gravity.CENTER);
		lstView.setEmptyView(tvEmpty);
		lstView.setOnItemClickListener(this);

		// 下拉刷新事件回调
		lstView.setOnRefreshStartListener(new OnStartListener()
		{

			@Override
			public void onStart()
			{
				tvEmpty.setText("正在加载……");
				curPage = 0;
				loadPage(curPage);
			}
		});

		// 加载更多事件回调
		lstView.setOnLoadMoreStartListener(new OnStartListener()
		{

			@Override
			public void onStart()
			{
				loadPage(curPage);
			}
		});

		lstView.refresh();
		
		View progressBar = rootView.findViewById(R.id.commonzrclistview_progressbar);
		progressBar.setVisibility(View.GONE);
	}

	@Override
	protected int getRootViewLayId()
	{
		return R.layout.common_zrclistview_haveprocessbar;
	}
	@Override
	public void onItemClick(ZrcListView parent, View view, int position, long id)
	{
		if (position >= 0 && position < lstProjects.size())
		{
			RMProject itemInfo = lstProjects.get(position);
			if (itemInfo != null)
			{
				Intent intent = new Intent();
				intent.putExtra("projectId", itemInfo.id);
				intent.setClass(getActivity(), RMIssuesListActy.class);
				startActivity(intent);
			}
		}
	}

	/*
	 * 下拉刷新结束
	 * 
	 * @param success			刷新是否成功
	 */
	private void onRefreshComplete(boolean success)
	{
		if (lstProjects.size() == 0)
		{
			tvEmpty.setText("无内容");
		} else
		{
			tvEmpty.setText("");
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
		if (lstProjects.size() == 0)
		{
			tvEmpty.setText("没有相关记录");
		} else
		{
			tvEmpty.setText("");
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
				adapter = new RMProjectsAdapter(getActivity(), lstProjects);
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
		StringBuilder url = new StringBuilder(RMHttpUrl.URL_PROJECTS);
		url.append("?limit=").append(RMConst.PAGELIMIT);
		url.append("&offset=").append(page * RMConst.PAGELIMIT);
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
							lstProjects.clear();
							RMProject info = new RMProject();
							info.id = 0;
							info.name = "所有项目";
							lstProjects.add(info);
						}
						Gson gson = new Gson();
						RMProjectsReply ps = gson.fromJson(response, RMProjectsReply.class);
						if (ps != null && ps.projects != null && ps.projects.size() > 0)
						{
							lstProjects.addAll(ps.projects);
							int size = ps.projects.size();
							for (int i = 0; i < size; i++)
							{
								RMProject projI = ps.projects.get(i);
								for (int j = 0; j < size; j++)
								{
									RMProject projJ = ps.projects.get(j);
									if (projJ.parent != null && projJ.parent.id == projI.id)
									{
										projI.isParent = true;
										break;
									}
								}
							}
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
			public void failed(int statusCode, String failedReason)
			{
				isSuccess = false;
				onLoadAll();

				//token已失效
				if (statusCode == 401)
				{
					RMHttpUtil.setToken(null);
					ActyUtil.startActivity(getActivity(), RMLoginActy.class);
					getActivity().finish();
					FCToast.ToastShow("登录Token已失效，请重新登录");
				}
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
