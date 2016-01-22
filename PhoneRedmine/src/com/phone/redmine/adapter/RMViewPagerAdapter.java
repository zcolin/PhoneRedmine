package com.phone.redmine.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.phone.redmine.acty.RMMainActy;

/**
 * RM 主页面 ViewPager适配器
 */
public class RMViewPagerAdapter extends FragmentPagerAdapter
{

	private RMMainActy	rmActy;

	public RMViewPagerAdapter (RMMainActy rmActy, FragmentManager fm)
	{
		super(fm);
		this.rmActy = rmActy;
	}

	@Override
	public int getCount()
	{
		return RMMainActy.TAB_POSITION.length;
	}

	@Override
	public Fragment getItem(int arg0)
	{
		return rmActy.getFragByPosition(arg0);
	}
}
