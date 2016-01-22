package com.phone.redmine.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.biz.RMBiz;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.RMIssue;
import com.phone.redmine.util.CalendarUtil;
import com.phone.redmine.view.BadgeView;
import com.phone.redmine.view.FCProgressView;

/**
 * RM 问题列表适配器
 */
public class RMIssuesAdapter extends BaseAdapter
{

	//数据库存放的后台检索服务器获取的更新未查看的问题
	private ArrayList<Integer>	lstUpdatedIssue	= new ArrayList<Integer>();

	//要显示的问题列表
	private ArrayList<RMIssue>	lstIssueInfo;

	private ViewHolder			viewHolder;
	private LayoutInflater		mInflater;
	private boolean				isShowBottom;
	private Context				context;
	private Resources			res;

	public RMIssuesAdapter (Context context, ArrayList<RMIssue> lstIssueInfo, boolean isShowBottom)
	{
		this.context = context;
		this.res = context.getResources();
		this.lstIssueInfo = lstIssueInfo;
		this.isShowBottom = isShowBottom;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		lstUpdatedIssue.clear();
		RMDBLocal.getInstance().getAllUpdatedIssueId(lstUpdatedIssue);
	}

	@Override
	public void notifyDataSetChanged()
	{
		lstUpdatedIssue.clear();
		RMDBLocal.getInstance().getAllUpdatedIssueId(lstUpdatedIssue);

		super.notifyDataSetChanged();

	}

	@Override
	public int getCount()
	{
		return lstIssueInfo.size();
	}

	@Override
	public Object getItem(int position)
	{
		return position;
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if (convertView != null)
		{
			viewHolder = (ViewHolder) convertView.getTag();
		} else
		{
			convertView = mInflater.inflate(R.layout.rmlistitem_issue, null);
			viewHolder = new ViewHolder();
			viewHolder.tvSubject = (TextView) convertView.findViewById(R.id.rmlistitemissue_subject);
			viewHolder.tvAssignedTo = (TextView) convertView.findViewById(R.id.rmlistitemissue_assignedto);
			viewHolder.tvDueDate = (TextView) convertView.findViewById(R.id.rmlistitemissue_duedate);
			viewHolder.tvId = (TextView) convertView.findViewById(R.id.rmlistitemissue_id);
			viewHolder.tvPriority = (TextView) convertView.findViewById(R.id.rmlistitemissue_priority);
			viewHolder.tvStatus = (TextView) convertView.findViewById(R.id.rmlistitemissue_status);
			viewHolder.tvTracker = (TextView) convertView.findViewById(R.id.rmlistitemissue_tracker);
			viewHolder.rlBottom = (RelativeLayout) convertView.findViewById(R.id.rmlistitemissue_bottom_ll);
			viewHolder.tvProjectName = (TextView) convertView.findViewById(R.id.rmlistitemissue_project);
			viewHolder.tvVersion = (TextView) convertView.findViewById(R.id.rmlistitemissue_version);
			viewHolder.progress = (FCProgressView) convertView.findViewById(R.id.rmlistitemissue_progress);
			viewHolder.badgeTask = new BadgeView(context, viewHolder.tvSubject);
			convertView.setTag(viewHolder);
		}

		RMIssue info = lstIssueInfo.get(position);
		viewHolder.tvSubject.setText(info.subject);
		viewHolder.tvId.setText("#" + info.id);
		viewHolder.tvStatus.setText(info.status == null ? "" : info.status.name);
		viewHolder.tvAssignedTo.setText(info.assigned_to == null ? "" : info.assigned_to.name);
		viewHolder.tvTracker.setText(info.tracker == null ? "" : info.tracker.name);
		viewHolder.tvPriority.setText(info.priority == null ? "" : info.priority.name);

		/*完成時間如果超过当天显示红色*/
		if (info.due_date != null)
		{
			Calendar calDue = Calendar.getInstance();
			calDue.setTime(CalendarUtil.parseUTC(info.due_date, CalendarUtil.DEF_DATE_FORMAT));
			Calendar calNow = Calendar.getInstance();
			if (calDue.before(calNow) && !RMBiz.isClosed(info))
			{
				viewHolder.tvDueDate.setTextColor(res.getColor(R.color.red_light));
			} else
			{
				viewHolder.tvDueDate.setTextColor(res.getColor(R.color.gray_mid));
			}
		}
		viewHolder.tvDueDate.setText(info.due_date);

		/*追踪状态设置背景*/
		if (info.tracker != null && info.tracker.id >= 0 && info.tracker.id < 10)
		{
			viewHolder.tvTracker.setBackgroundColor(RMConst.TRACKER_DEFAULT_COLORS[info.tracker.id]);
		}

		/*关闭的问题背景显示为深灰色*/
		if (RMBiz.isClosed(info))
		{
			convertView.setBackgroundColor(res.getColor(R.color.gray_light));
		} else
		{
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}

		if (isShowBottom)
		{
			viewHolder.rlBottom.setVisibility(View.VISIBLE);
			viewHolder.tvProjectName.setText(info.project == null ? "" : info.project.name);
			viewHolder.tvVersion.setText(info.fixed_version == null ? "" : info.fixed_version.name);
			viewHolder.progress.setProgress(info.done_ratio);
		} else
		{
			viewHolder.rlBottom.setVisibility(View.GONE);
		}

		if (isNewUpdateIssue(info.id))
		{
			viewHolder.badgeTask.show();
		} else
		{
			viewHolder.badgeTask.hide();
		}

		return convertView;
	}

	/*
	 * 问题是否是新的问题
	 * 
	 * @param id		问题id
	 * 
	 * @return 			是否是新问题
	 */
	private boolean isNewUpdateIssue(int id)
	{
		int size = lstUpdatedIssue.size();
		for (int i = 0; i < size; i++)
		{
			if (lstUpdatedIssue.get(i).intValue() == id)
			{
				return true;
			}
		}
		return false;
	}

	private class ViewHolder
	{

		BadgeView		badgeTask;		//红点
		TextView		tvSubject;		//问题主题
		TextView		tvId;			//问题ID
		TextView		tvStatus;		//问题状态
		TextView		tvPriority;		//问题优先级
		TextView		tvAssignedTo;	//指派给
		TextView		tvTracker;		//问题跟踪
		TextView		tvDueDate;		//完成日期
		RelativeLayout	rlBottom;		//底部控件的容器布局
		TextView		tvVersion;		//版本
		TextView		tvProjectName;	//项目名
		FCProgressView	progress;		//完百分比
	}

}
