package com.phone.redmine.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.entity.RMIssueFilter;

/**
 * RM 自定义过滤器 列表适配器 
 */
public class RMSelfIssueFilterAdapter extends BaseAdapter
{

	private ArrayList<RMIssueFilter>	lstSelfFilter;
	private Context						context;
	private int							textPadding;

	public RMSelfIssueFilterAdapter (Context context, ArrayList<RMIssueFilter> lstSelfFilter)
	{
		this.context = context;
		this.lstSelfFilter = lstSelfFilter;
		textPadding = (int) context.getResources().getDimension(R.dimen.dimens_mid);
	}

	@Override
	public int getCount()
	{
		return lstSelfFilter.size();
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
		TextView tv;
		if (convertView != null)
		{
			tv = (TextView) convertView;
		} else
		{
			tv = new AppCompatTextView(context);
			tv.setTextSize(20);
			tv.setPadding(textPadding, textPadding, textPadding, textPadding);
		}
		tv.setText(lstSelfFilter.get(position).name);
		return tv;
	}
}
