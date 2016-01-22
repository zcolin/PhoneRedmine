package com.phone.redmine.biz;

import java.util.ArrayList;
import org.apache.http.Header;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.phone.redmine.acty.InitActy;
import com.phone.redmine.app.App;
import com.phone.redmine.biz.RMLoginWebAsyncTask.OnLoginComplete;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.RMIssue;
import com.phone.redmine.entity.RMPairBoolInfo;
import com.phone.redmine.entity.RMProject;
import com.phone.redmine.http.RMHttpResponse;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.http.entity.RMIssueStatusReply;
import com.phone.redmine.http.entity.RMMemberShipReply;
import com.phone.redmine.http.entity.RMPrioritiesReply;
import com.phone.redmine.http.entity.RMProjectsReply;
import com.phone.redmine.http.entity.RMVersionsReply;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.util.TimeUtil;

/**
 * RM 业务数据处理
 */
public class RMBiz
{

	/**
	 * 项目列表
	 */
	public static ArrayList<RMProject>	lstRMProjects	= new ArrayList<RMProject>();

	/**
	 * 问题状态_关闭
	 */
	public static RMPairBoolInfo		ISSUESTATUS_CLOSED;
	/**
	 * 问题状态_默认
	 */
	public static RMPairBoolInfo		ISSUESTATUS_DEFAULT;

	/**
	 * 加载数据 
	 * 
	 * @param complete
	 */
	public static void saveData(final OnLoadComplete complete)
	{
		saveProjects(0, new OnLoadComplete()
		{

			@Override
			public void onComplete(boolean isSuccess)
			{
				if (isSuccess)
				{
					RMDBLocal.getInstance().clearVersions();
					saveVersions(0, new OnLoadComplete()
					{

						@Override
						public void onComplete(boolean isSuccess)
						{
							if (isSuccess)
							{
								savePriorities(new OnLoadComplete()
								{

									@Override
									public void onComplete(boolean isSuccess)
									{
										if (isSuccess)
										{
											saveIssueStatus(new OnLoadComplete()
											{

												@Override
												public void onComplete(boolean isSuccess)
												{
													if (isSuccess)
													{
														saveStaffsFromWeb(new OnLoadComplete()
														{

															@Override
															public void onComplete(boolean isSuccess)
															{
																if (isSuccess)
																{
																	SharePrefUtil.getInstance().setString(RMConst.SHARE_DBSYNCTIME, TimeUtil.getDateTime());
																}
																if (complete != null)
																{
																	complete.onComplete(isSuccess);
																}
															}
														});
													} else
													{
														if (complete != null)
														{
															complete.onComplete(false);
														}
													}
												}
											});
										} else
										{
											if (complete != null)
											{
												complete.onComplete(false);
											}
										}
									}
								});
							} else
							{
								if (complete != null)
								{
									complete.onComplete(false);
								}
							}
						}
					});
				} else
				{
					if (complete != null)
					{
						complete.onComplete(false);
					}
				}
			}
		});
	}

	/**
	 * 从服务器获取项目数据，保存到数据库和内存
	 * 
	 * @param page				分页页码
	 * @param listener			所有加载完回调
	 */
	private static void saveProjects(final int page, final OnLoadComplete listener)
	{
		StringBuilder url = new StringBuilder(RMHttpUrl.URL_PROJECTS);
		url.append("?limit=").append(RMConst.PAGELIMIT);
		url.append("&offset=").append(page * RMConst.PAGELIMIT);
		url.append("&include=trackers,issue_categories");
		RMHttpUtil.addCommonSessionHeader();
		RMHttpUtil.client.get(App.APPCONTEXT, url.toString(), new RMHttpResponse(RMHttpResponse.SUCCESSCODE_200)
		{

			@Override
			public void success(Header[] headers, String response)
			{
				if (headers != null && response.length() > 0)
				{
					try
					{
						Gson gson = new Gson();
						RMProjectsReply ps = gson.fromJson(response, RMProjectsReply.class);
						if (ps != null && ps.projects != null && ps.projects.size() > 0)
						{
							lstRMProjects.clear();
							lstRMProjects.addAll(ps.projects);

							/*设置是否为parent*/
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

							/*第一次调用此方法时数据置空*/
							if (page == 0)
							{
								RMDBLocal.getInstance().clearProjects();
								RMDBLocal.getInstance().clearTrackers();
								RMDBLocal.getInstance().clearIssueCategory();
							}

							/*数据库插入数据*/
							RMDBLocal.getInstance().insertProjects(ps.projects);
							for (int i = 0; i < size; i++)
							{
								RMProject proi = ps.projects.get(i);
								RMDBLocal.getInstance().insertTrackers(proi.trackers, proi.id);
								RMDBLocal.getInstance().insertIssueCategorys(proi.issue_categories, proi.id, proi.name);
							}

							//如果还有下一页，递归
							if (ps.total_count >= ps.limit)
							{
								saveProjects(page + 1, listener);
							} else
							{
								if (listener != null)
								{
									listener.onComplete(true);
								}
							}
						}
					} catch (Exception e)
					{
						e.printStackTrace();
						if (listener != null)
						{
							listener.onComplete(false);
						}
					}
				}
			}

			@Override
			public void failed(String failedReason)
			{
				super.failed(failedReason);
				if (listener != null)
				{
					listener.onComplete(false);
				}
			}
		});
	}

	/**
	 * 从服务器获取人员信息保存到数据库（从web抓取数据）
	 * 
	 * @param listener		加载完回调
	 */
	private static void saveStaffsFromWeb(final OnLoadComplete listener)
	{
		InitActy.loginRedmineWeb(null, null, new OnLoginComplete()
		{

			@Override
			public void loginComplete(String faiReason)
			{
				if (faiReason == null)
				{
					new RMStaffWebAsyncTask(listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 0);
				} else
				{
					listener.onComplete(false);
				}
			}
		});
	}

	/**
	 * 使用接口获取人员数据
	 * @deprecated
	 * 
	 * @param index			项目的下标
	 * @param page			单个项目下获取数据的分页页码
	 * 
	 * @param listener		加载完回调接口	
	 */
	public static void saveStaffs(final int index, final int page, final OnLoadComplete listener)
	{
		if (index < lstRMProjects.size())
		{
			if (lstRMProjects.get(index).isParent)
			{
				saveStaffs(index + 1, 0, listener);
			} else
			{
				final int projectId = lstRMProjects.get(index).id;
				StringBuilder url = new StringBuilder(RMHttpUrl.URL_PRE_PROJECTS);
				url.append(projectId).append("/memberships.json");
				url.append("?limit=").append(RMConst.PAGELIMIT);
				url.append("&offset=").append(page * RMConst.PAGELIMIT);
				RMHttpUtil.addCommonSessionHeader();
				RMHttpUtil.client.get(App.APPCONTEXT, url.toString(), new RMHttpResponse(
						RMHttpResponse.SUCCESSCODE_200)
				{

					@Override
					public void success(Header[] headers, String response)
					{
						super.success(headers, response);
						Gson gson = new Gson();
						RMMemberShipReply reply = gson.fromJson(response, RMMemberShipReply.class);
						if (reply != null && reply.memberships != null)
						{
							RMDBLocal.getInstance().insertStaffs(reply.memberships);

							//如果还有下一页，递归
							if (reply.total_count >= reply.limit)
							{
								saveStaffs(index, page + 1, listener);
							} else
							{
								saveStaffs(index + 1, 0, listener);
							}
						}
					}

					@Override
					public void failed(int statusCode, String failedReason)
					{
						super.failed(statusCode, failedReason);
						if (listener != null)
						{
							listener.onComplete(false);
						}
					}
				});
			}
		} else
		{
			if (listener != null)
			{
				listener.onComplete(true);
			}
		}
	}

	/**
	 * 从服务器获取版本信息保存到数据库 
	 * 
	 * @param index			项目的下标
	 * 
	 * @param listener		记载完回调接口
	 */
	private static void saveVersions(final int index, final OnLoadComplete listener)
	{
		if (index < lstRMProjects.size())
		{
			if (lstRMProjects.get(index).isParent)
			{
				saveVersions(index + 1, listener);
			} else
			{
				final String identifier = lstRMProjects.get(index).identifier;
				StringBuilder url = new StringBuilder(RMHttpUrl.URL_PRE_PROJECTS);
				url.append(identifier).append("/versions.json");
				RMHttpUtil.addCommonSessionHeader();
				RMHttpUtil.client.get(App.APPCONTEXT, url.toString(), new RMHttpResponse(
						RMHttpResponse.SUCCESSCODE_200)
				{

					@Override
					public void success(Header[] headers, String response)
					{
						super.success(headers, response);
						Gson gson = new Gson();
						RMVersionsReply reply = gson.fromJson(response, RMVersionsReply.class);
						if (reply != null && reply.versions != null)
						{
							RMDBLocal.getInstance().insertVersions(reply.versions);
						}
						saveVersions(index + 1, listener);
					}

					@Override
					public void failed(int statusCode, String failedReason)
					{
						super.failed(statusCode, failedReason);
						if (listener != null)
						{
							listener.onComplete(false);
						}
					}
				});
			}
		} else
		{
			if (listener != null)
			{
				listener.onComplete(true);
			}
		}
	}

	/**
	 * 从服务器获取优先级信息保存到数据库
	 * 
	 * @param listener
	 */
	private static void savePriorities(final OnLoadComplete listener)
	{
		RMHttpUtil.addCommonSessionHeader();
		RMHttpUtil.client.get(App.APPCONTEXT, RMHttpUrl.URL_ISSUE_PRIORITIES, new RMHttpResponse(
				RMHttpResponse.SUCCESSCODE_200)
		{

			boolean	isSuccess;

			@Override
			public void success(Header[] headers, String response)
			{
				super.success(headers, response);
				Gson gson = new Gson();
				RMPrioritiesReply reply = gson.fromJson(response, RMPrioritiesReply.class);
				if (reply != null && reply.issue_priorities != null)
				{
					RMDBLocal.getInstance().clearPriorities();
					RMDBLocal.getInstance().insertPriorities(reply.issue_priorities);
				}
				isSuccess = true;
			}

			@Override
			public void onFinish()
			{
				super.onFinish();
				if (listener != null)
				{
					listener.onComplete(isSuccess);
				}
			}
		});
	}

	/**
	 * 从服务器获取问题状态信息保存到数据库 
	 * 
	 * @param listener
	 */
	private static void saveIssueStatus(final OnLoadComplete listener)
	{
		RMHttpUtil.addCommonSessionHeader();
		RMHttpUtil.client.get(App.APPCONTEXT, RMHttpUrl.URL_ISSUES_STATUSES, new RMHttpResponse(
				RMHttpResponse.SUCCESSCODE_200)
		{

			boolean	isSuccess;

			@Override
			public void success(Header[] headers, String response)
			{
				super.success(headers, response);
				Gson gson = new Gson();
				RMIssueStatusReply reply = gson.fromJson(response, RMIssueStatusReply.class);
				if (reply != null && reply.issue_statuses != null)
				{
					RMDBLocal.getInstance().clearIssueStatus();
					RMDBLocal.getInstance().insertIssueStatus(reply.issue_statuses);
				}
				isSuccess = true;
			}

			@Override
			public void onFinish()
			{
				super.onFinish();
				if (listener != null)
				{
					listener.onComplete(isSuccess);
				}
			}
		});
	}

	/**
	 * 判断某个问题是不是关闭的属性
	 * 
	 * @param issue			问题信息
	 * 
	 * @return				是否是 关闭属性
	 */
	public static boolean isClosed(RMIssue issue)
	{
		if (issue != null && issue.status != null && ISSUESTATUS_CLOSED != null)
		{
			if (issue.status.id == ISSUESTATUS_CLOSED.id)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 设置问题的 新建，已关闭 的默认值 
	 */
	public static void setFixStatus()
	{
		ArrayList<RMPairBoolInfo> lstIssueStatus = new ArrayList<RMPairBoolInfo>();
		RMDBLocal.getInstance().getAllIssueStatus(lstIssueStatus);
		int size = lstIssueStatus.size();
		for (int i = 0; i < size; i++)
		{
			RMPairBoolInfo info = lstIssueStatus.get(i);
			if (info.bool2)
			{
				RMBiz.ISSUESTATUS_CLOSED = info;
			}
			if (info.bool1)
			{
				RMBiz.ISSUESTATUS_DEFAULT = info;
			}
		}
	}

	/**
	 * 根据ID获取元信息 
	 * 
	 * @param lstPairInfo	信息列表
	 * @param id			信息Id
	 * 
	 * @return				信息
	 */
	public static RMPairBoolInfo getPairInfoById(ArrayList<RMPairBoolInfo> lstPairInfo, int id)
	{
		int size = lstPairInfo.size();
		for (int i = 0; i < size; i++)
		{
			if (lstPairInfo.get(i).id == id)
			{
				return lstPairInfo.get(i);
			}
		}
		return null;
	}

	/**
	 * 加载完成 
	 */
	public interface OnLoadComplete
	{

		public void onComplete(boolean isSuccess);
	}
}
