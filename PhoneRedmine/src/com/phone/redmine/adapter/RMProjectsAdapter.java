package com.phone.redmine.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.entity.RMProject;

/**
 * RM 项目列表适配器 
 */
public class RMProjectsAdapter extends BaseAdapter
{

	private ArrayList<RMProject>	lstProjectInfo;
	private ViewHolder				viewHolder;
	private LayoutInflater			mInflater;

	public RMProjectsAdapter (Context context, ArrayList<RMProject> lstProjectInfo)
	{
		this.lstProjectInfo = lstProjectInfo;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		return lstProjectInfo.size();
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
			convertView = mInflater.inflate(R.layout.rmlistitem_project, null);
			viewHolder = new ViewHolder();
			viewHolder.tvProject = (TextView) convertView.findViewById(R.id.rmlistitemproject_name);
			convertView.setTag(viewHolder);
		}

		RMProject info = lstProjectInfo.get(position);
		String paddingText = "";
		if (info.parent != null)
		{
			paddingText = "      ";
		}
		if (info.isParent)
		{
			viewHolder.tvProject.setBackgroundColor(Color.GRAY);
		} else
		{
			viewHolder.tvProject.setBackgroundColor(Color.TRANSPARENT);
		}
		viewHolder.tvProject.setText(paddingText + info.name);
		return convertView;
	}

	private class ViewHolder
	{

		TextView	tvProject;
	}
}
