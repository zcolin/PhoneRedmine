package com.phone.redmine.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.acty.RMSearchActy.SearchResult;

/**
 * RM 问题搜索 列表适配器 
 */
public class RMSearchAdapter extends BaseAdapter
{

	private ArrayList<SearchResult>	lstSearchResult;
	private ViewHolder				viewHolder;
	private LayoutInflater			mInflater;
	private Context					context;

	public RMSearchAdapter (Context context, ArrayList<SearchResult> lstSearchResult)
	{
		this.context = context;
		this.lstSearchResult = lstSearchResult;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount()
	{
		return lstSearchResult.size();
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
			convertView = mInflater.inflate(R.layout.rmlistitem_search, null);
			viewHolder = new ViewHolder();
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.rmlistitemsearch_title);
			viewHolder.tvProject = (TextView) convertView.findViewById(R.id.rmlistitemsearch_project);
			viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.rmlistitemsearch_description);
			convertView.setTag(viewHolder);
		}

		SearchResult info = lstSearchResult.get(position);
		viewHolder.tvTitle.setText(info.strTitle);
		viewHolder.tvProject.setText(info.project + "     " + info.time);
		viewHolder.tvDescription.setText(info.description);

		Drawable able = getDrawByType(info.type);
		viewHolder.tvTitle.setCompoundDrawablesWithIntrinsicBounds(able, null, null, null);

		return convertView;
	}

	private Drawable getDrawByType(int type)
	{
		Drawable able = null;
		if (type == 0)
		{
			able = context.getResources().getDrawable(R.drawable.searchitem_issue);
		} else if (type == 1)
		{
			able = context.getResources().getDrawable(R.drawable.searchitem_news);
		} else if (type == 2)
		{
			able = context.getResources().getDrawable(R.drawable.searchitem_document);
		}
		return able;
	}

	private class ViewHolder
	{

		TextView	tvTitle;
		TextView	tvProject;
		TextView	tvDescription;
	}
}
