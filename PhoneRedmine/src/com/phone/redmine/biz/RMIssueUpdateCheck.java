package com.phone.redmine.biz;

import java.util.ArrayList;
import java.util.Date;
import org.apache.http.Header;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.google.gson.Gson;
import com.phone.redmine.R;
import com.phone.redmine.acty.RMMainActy;
import com.phone.redmine.app.App;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.EventEntity;
import com.phone.redmine.entity.RMIssue;
import com.phone.redmine.entity.RMIssueDetail.Journal;
import com.phone.redmine.http.RMHttpResponse;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.http.entity.RMIssueReply;
import com.phone.redmine.http.entity.RMIssuesReply;
import com.phone.redmine.util.CalendarUtil;
import com.phone.redmine.util.NetworkUtil;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.util.StringUtil;
import de.greenrobot.event.EventBus;

/**
 * 问题更新检查
 */
public class RMIssueUpdateCheck
{

	/**
	 * 检查问题更新（根据上次刷新时间获取时间段内的新问题， 然后根据操作日志去除掉自己操作的问题）
	 */
	public void checkIssueUpdate()
	{
		//如果登录过redmine才进行后台刷新
		if (StringUtil.isNotBlank(RMHttpUtil.getToken()))
		{
			//如果没有查到上次刷新时间，本次不刷新，记录下本次的时间，下次刷新
			String lastTime = SharePrefUtil.getInstance().getString(RMConst.SHARE_UPDATEISSUE_TIME, null);
			if (lastTime == null)
			{
				SharePrefUtil.getInstance().setString(RMConst.SHARE_UPDATEISSUE_TIME, CalendarUtil.getDateTimeUTC(new Date(), CalendarUtil.DEF_UTC_FORMAT));
			} else
			{
				if (NetworkUtil.isNetworkAvailable(App.APPCONTEXT))
				{
					//刷新时间，先获取（防止出现时间差），如果刷新成功，计入本地
					final String time = CalendarUtil.getDateTimeUTC(new Date(), CalendarUtil.DEF_UTC_FORMAT);
//					final int userid = FCSharePrefUtil.getInstance().getInt(RMConst.SHARE_RMUSERID, 0);
					StringBuilder url = new StringBuilder(RMHttpUrl.URL_ISSUES);
					url.append("?limit=").append(30);
					url.append("&sort=").append("updated_on:desc");
					url.append("&assigned_to_id=me");
					url.append("&updated_on=%3E%3D" + lastTime);
					//url.append("&created_on=%3E%3D" + lastTime);
					RMHttpUtil.addCommonSessionHeader();
					RMHttpUtil.client.get(App.APPCONTEXT, url.toString(), new RMHttpResponse(
							RMHttpResponse.SUCCESSCODE_200)
					{

						@Override
						public void success(Header[] headers, String response)
						{
							if (headers != null && response.length() > 0)
							{
								try
								{
									Gson gson = new Gson();
									RMIssuesReply ps = gson.fromJson(response, RMIssuesReply.class);

									//有子项目的项目
									if (ps != null && ps.issues != null && ps.issues.size() > 0)
									{
										//去除已关闭和已解决的
										for (int i = 0; i < ps.issues.size(); i++)
										{
											RMIssue info = ps.issues.get(i);
											if (info.status.id != 1/*只保留新建*/)
											{
												ps.issues.remove(i);
												RMDBLocal.getInstance().deleteUpdatedIssueId(info.id);
											}
										}
										updateData(ps.issues, time);
										
										//showNotification(ps.issues);
										//										excludeSelfOperate(ps.issues, 0, time, userid);
									}
								} catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						}
					});
				}
			}
		}
	}

	/*
	 * 逐条查询，根据操作日志判断是不是自己，如果是自己，不算作更新提示范畴
	 * 
	 * @param lstIssues				问题更新列表
	 * @param index					问题列表的下标
	 * @param time					请求服务器的时间	
	 * @param userId				用户Id
	 */
	public void excludeSelfOperate(final ArrayList<RMIssue> lstIssues, final int index, final String time,
			final int userId)
	{
		if (index < lstIssues.size())
		{
			StringBuilder url = new StringBuilder(RMHttpUrl.URL_PRE_ISSUES);
			url.append(lstIssues.get(index).id).append(".json?include=journals");
			RMHttpUtil.addCommonSessionHeader();
			RMHttpUtil.client.get(App.APPCONTEXT, url.toString(), new RMHttpResponse(RMHttpResponse.SUCCESSCODE_200)
			{

				boolean	isRemove	= false;

				@Override
				public void success(Header[] headers, String response)
				{
					if (headers != null && response.length() > 0)
					{
						try
						{
							Gson gson = new Gson();
							RMIssueReply ps = gson.fromJson(response, RMIssueReply.class);
							if (ps != null && ps.issue != null)
							{
								ArrayList<Journal> lstJournals = ps.issue.journals;
								if (lstJournals != null && lstJournals.size() > 0)
								{
									Journal journal = ps.issue.journals.get(lstJournals.size() - 1);
									if (journal.user != null && journal.user.id == userId)
									{
										lstIssues.remove(index);
										isRemove = true;
									}
								}
							}
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}

				@Override
				public void onFinish()
				{
					super.onFinish();
					if (!isRemove)
					{
						excludeSelfOperate(lstIssues, index + 1, time, userId);
					} else
					{
						excludeSelfOperate(lstIssues, index, time, userId);
					}
				}
			});
		}
		else
		{
			updateData(lstIssues, time);
		}
	}

	/**
	 * 创建系统通知
	 * 
	 * @param lstIssue
	 */
	public void showNotification(ArrayList<RMIssue> lstIssue)
	{
		NotificationManager manager = (NotificationManager) App.APPCONTEXT.getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(App.APPCONTEXT);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setAutoCancel(true);
		builder.setContentTitle("有 " + lstIssue.size() + " 条指派给我的问题更新");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < lstIssue.size(); i++)
		{
			sb.append((i + 1)).append(". ").append(lstIssue.get(i).subject);
			if (i != lstIssue.size() - 1)
			{
				sb.append("\n");
			}
		}
		Intent resultIntent = new Intent(App.APPCONTEXT, RMMainActy.class);
		resultIntent.setAction(Intent.ACTION_MAIN);
		resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent pendingIntent = PendingIntent.getActivity(App.APPCONTEXT, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentText(sb.toString());
		builder.setContentIntent(pendingIntent);
		Notification noti = builder.build();
		manager.notify(0, noti);
	}

	/**
	 * 更新数据
	 * 
	 * @param lstIssues		从服务器查询到的数据列表
	 * @param time			更新时间
	 */
	private void updateData(ArrayList<RMIssue> lstIssues, String time)
	{
		//插入到已更新數據庫
		RMDBLocal.getInstance().insertUpdatedIssueId(lstIssues);

		//刷新成功，记录下刷新时间
		SharePrefUtil.getInstance().setString(RMConst.SHARE_UPDATEISSUE_TIME, time);

		//发送刷新页面广播
		EventBus.getDefault().post(new EventEntity.OnComplectUpdateIssueEvent());
	}
}
