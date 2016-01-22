package com.phone.redmine.adapter;

import java.util.ArrayList;
import java.util.Date;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.entity.RMNews;
import com.phone.redmine.util.CalendarUtil;

/**
 *	RM	新闻列表适配器 
 */
public class RMNewsAdapter extends BaseAdapter
{

	private ViewHolder			viewHolder;
	private LayoutInflater		mInflater;
	private ArrayList<RMNews>	lstNews;

	public RMNewsAdapter (Context context, ArrayList<RMNews> lstNews)
	{
		this.lstNews = lstNews;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		return lstNews.size();
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
			convertView = mInflater.inflate(R.layout.rmlistitem_news, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.rmlistitemnews_title);
			viewHolder.tvSummary = (TextView) convertView.findViewById(R.id.rmlistitemnews_summary);
			viewHolder.tvCreatedOn = (TextView) convertView.findViewById(R.id.rmlistitemnews_createdon);
			convertView.setTag(viewHolder);
		}

		RMNews info = lstNews.get(position);
		viewHolder.tvTitle.setText((position + 1) + "  " + info.title);
		viewHolder.tvSummary.setText(info.summary);

		String strAuthor = info.author == null ? "" : info.author.name;
		Date date = CalendarUtil.parseUTC(info.created_on, CalendarUtil.DEF_UTC_FORMAT);
		String diff = CalendarUtil.diffNow(date);
		viewHolder.tvCreatedOn.setText(strAuthor + " " + diff + "前 添加");
		return convertView;
	}

	private class ViewHolder
	{

		TextView	tvTitle;
		TextView	tvSummary;
		TextView	tvCreatedOn;
	}
}
