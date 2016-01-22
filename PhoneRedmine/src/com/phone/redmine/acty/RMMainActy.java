package com.phone.redmine.acty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import com.phone.redmine.R;
import com.phone.redmine.adapter.RMViewPagerAdapter;
import com.phone.redmine.biz.RMBiz;
import com.phone.redmine.entity.EventEntity;
import com.phone.redmine.frag.RMFunctionFrag;
import com.phone.redmine.frag.RMMyIssuesFrag;
import com.phone.redmine.frag.RMNewsFrag;
import com.phone.redmine.frag.RMProjectsFrag;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.util.ActyUtil;
import com.phone.redmine.util.StringUtil;
import com.phone.redmine.view.FCTabView;
import com.phone.redmine.view.FCTabView.FCTab;
import de.greenrobot.event.EventBus;

/**
 * RM 主页面
 */
public class RMMainActy extends BaseToolBarActy
{
	/**
	 * 进入 问题列表过滤器  页面的标识
	 */
	public static final int		RESULT_RMISSUEFILTERACTY	= 10;
	public static final int[]	TAB_POSITION				= new int[]{0, 1, 2, 3};
	private Fragment[]			TAB_FRAG					= new Fragment[TAB_POSITION.length];
	private ViewPager			mViewPager;
	private FCTabView			tabView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmacty_main);
		setActionbarTitle("RedMine");

		/*如果本地没有token，直接转到登录页面*/
		if (StringUtil.isEmpty(RMHttpUtil.getToken()))
		{
			ActyUtil.startActivity(this, RMLoginActy.class);
			this.finish();
			return;
		}

		ininAppData();
		initRes();
		setupTab();
		initData();

		EventBus.getDefault().register(this);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	@Override
	protected void onActionbarExtraClick()
	{
		Intent intent = new Intent();
		intent.setClass(this, RMFastIssueFilterActy.class);
		intent.putExtra("type", 1);
		startActivityForResult(intent, RESULT_RMISSUEFILTERACTY);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2)
	{
		if (arg1 == RESULT_OK)
		{
			((RMMyIssuesFrag) TAB_FRAG[1]).refresh();
		}
	}

	/**
	 * EventBus 回调
	 * 
	 * @param event
	 */
	public void onEventMainThread(EventEntity.OnComplectUpdateIssueEvent event)
	{
		((RMMyIssuesFrag) TAB_FRAG[1]).refresh();
	}

	/**
	 * 根据位置获取Frag
	 * 
	 * @param pos	frag在viewpager中的位置
	 * @return
	 */
	public Fragment getFragByPosition(int pos)
	{
		if (TAB_FRAG[pos] == null)
		{
			TAB_FRAG[pos] = getNewFragByPos(pos);
		}
		return TAB_FRAG[pos];
	}

	/*
	 * 设置数据
	 */
	private void ininAppData()
	{
		RMBiz.setFixStatus();
	}

	private void initRes()
	{
		mViewPager = (ViewPager) findViewById(R.id.rmactymain_pager);
	}

	private void initData()
	{
		RMViewPagerAdapter shopDetailAdapter = new RMViewPagerAdapter(this, getSupportFragmentManager());
		mViewPager.setAdapter(shopDetailAdapter);

		MainPagerListener pagerListener = new MainPagerListener();
		tabView.initViewPager(mViewPager);
		tabView.setOnPageChangeListener(pagerListener);
	}

	//创建 添加tab
	private void setupTab()
	{
		float textSize = getResources().getDimension(R.dimen.textsize_small);
		tabView = (FCTabView) findViewById(R.id.rmactymain_tabview);
		tabView.addFCTab(getTab(textSize, "项目"));
		tabView.addFCTab(getTab(textSize, "我的任务"));
		tabView.addFCTab(getTab(textSize, "新闻"));
		tabView.addFCTab(getTab(textSize, "功能"));
		tabView.setFCTabLineSrc(R.drawable.actionbar_tabline);
	}

	/*
	 * 创建FCTab
	 * 
	 * @param textSize
	 * @param str
	 * @return
	 */
	private FCTab getTab(float textSize, String str)
	{
		FCTab tab = tabView.getNewTab();
		tab.setText(str);
		tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
		tab.setBackgroundResource(R.drawable.actionbar_tab_light_bg);
		tab.setTextColor(getResources().getColorStateList(R.drawable.actionbar_tabtext_light_tc));
		return tab;
	}

	/*
	 * 根据传入的位置创建新的Frag
	 * 
	 * @param i
	 * @return
	 */
	private Fragment getNewFragByPos(int i)
	{
		Fragment frag = null;
		if (i == TAB_POSITION[0])
		{
			frag = RMProjectsFrag.newInstance();
		} else if (i == TAB_POSITION[1])
		{
			frag = RMMyIssuesFrag.newInstance();
		} else if (i == TAB_POSITION[2])
		{
			frag = RMNewsFrag.newInstance();
		} else if (i == TAB_POSITION[3])
		{
			frag = RMFunctionFrag.newInstance();
		}
		return frag;
	}

	/*
	 * ViewPager监听类 
	 */
	private class MainPagerListener implements OnPageChangeListener
	{

		@Override
		public void onPageScrollStateChanged(int arg0)
		{}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{}

		@Override
		public void onPageSelected(int arg0)
		{
			if (arg0 == TAB_POSITION[1])
			{
				setActionbarExtraBackground(R.drawable.actionbar_filter_selector);
			} else
			{
				setActionbarExtra(null);
			}
		}
	}

}
