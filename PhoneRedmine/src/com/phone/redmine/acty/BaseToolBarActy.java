package com.phone.redmine.acty;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.util.DisplayUtil;
import com.phone.redmine.util.StringUtil;

/** 
 * 带Toolbar的Acitivity的基类，实现公共操作 ,封装Toolbar
 */
public class BaseToolBarActy extends BaseActy
{

	private View			actionBarView;			//自定义的ActionBar的布局
	private TextView		actionbarTitleView;	//标题
	private TextView		actionbarExtraView;	//预制按钮一
	private TextView		actionbarExtra2View;	//预制按钮二

	/*
	* 两个属性
	* 1、toolbar是否悬浮在窗口之上
	* 2、toolbar的高度获取
	* */
	private static int[]	ATTRS	= {
									R.attr.windowActionBarOverlay,
									R.attr.actionBarSize
									};

	public Toolbar			toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(initToolBar(layoutResID));
		setSupportActionBar(toolbar);
	}

	private FrameLayout initToolBar(int layoutResID)
	{
		/*直接创建一个帧布局，作为视图容器的父容器*/
		FrameLayout contentView = new FrameLayout(this);
		ViewGroup.LayoutParams parentParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		contentView.setLayoutParams(parentParams);

		/*将toolbar引入到父容器中*/
		View toolbarLay = LayoutInflater.from(this).inflate(R.layout.toolbar, contentView);
		toolbar = (Toolbar) toolbarLay.findViewById(R.id.id_tool_bar);
		if (isImmerse())
		{
			toolbar.setPadding(0, DisplayUtil.getStatusBarHeight(this), 0, 0);
			toolbar.getLayoutParams().height += 60; 
		}
		actionBarView = getLayoutInflater().inflate(R.layout.actionbar_baseview, toolbar);
		actionbarTitleView = (TextView) actionBarView.findViewById(R.id.actionbar_title);
		actionbarExtraView = (TextView) actionBarView.findViewById(R.id.actionbar_extra);
		actionbarExtra2View = (TextView) actionBarView.findViewById(R.id.actionbar_extra2);
		actionbarTitleView.setVisibility(View.GONE);
		actionbarExtraView.setVisibility(View.GONE);
		actionbarExtra2View.setVisibility(View.GONE);
		BaseClickListener clickListener = new BaseClickListener();
		actionbarTitleView.setOnClickListener(clickListener);
		actionbarExtraView.setOnClickListener(clickListener);
		actionbarExtra2View.setOnClickListener(clickListener);

		/*将自定义的布局引入到父容器中*/
		View userView = LayoutInflater.from(this).inflate(layoutResID, null);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		TypedArray typedArray = getTheme().obtainStyledAttributes(ATTRS);
		/*获取主题中定义的悬浮标志*/
		boolean overly = typedArray.getBoolean(0, false);
		/*获取主题中定义的toolbar的高度*/
		int toolBarSize = toolbar.getLayoutParams().height;
		typedArray.recycle();
		/*如果是悬浮状态，则不需要设置间距*/
		params.topMargin = overly ? 0 : toolBarSize;
		contentView.addView(userView, params);

		return contentView;
	}

	/**
	 * 设置ActionBar的标题
	 * 
	 * @param title	：ActionBar的标题
	 */
	public void setActionbarTitle(String title)
	{
		if (StringUtil.isNotEmpty(title))
		{
			actionbarTitleView.setText(title);
			actionbarTitleView.setVisibility(View.VISIBLE);
		} else
		{
			actionbarTitleView.setText(null);
			actionbarTitleView.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置ActionBar标题的背景图片
	 * 
	 * @param res ：资源ID
	 */
	public void setActionbarTitleBackground(int res)
	{
		actionbarTitleView.setBackgroundResource(res);
		actionbarTitleView.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置ActionBar的预置按钮长按可用
	 */
	public void setLongClickEnable()
	{
		BaseLongClickListener longClickListener = new BaseLongClickListener();
		actionbarTitleView.setOnLongClickListener(longClickListener);
		actionbarExtraView.setOnLongClickListener(longClickListener);
		actionbarExtra2View.setOnLongClickListener(longClickListener);
	}

	/**
	 * 设置ActionBar的预置按钮长按不可用
	 */
	public void setLongClickDisable()
	{
		actionbarTitleView.setOnLongClickListener(null);
		actionbarExtraView.setOnLongClickListener(null);
		actionbarExtra2View.setOnLongClickListener(null);
	}

	/**
	 * 设置ActionBar标题的背景图片
	 * 
	 * @param able : 标题显示图片Drawable对象
	 */
	@SuppressWarnings("deprecation")
	public void setActionbarTitleBackground(Drawable able)
	{
		if (able != null)
		{
			actionbarTitleView.setBackgroundDrawable(able);
			actionbarTitleView.setVisibility(View.VISIBLE);
		} else
		{
			actionbarTitleView.setBackgroundDrawable(null);
			actionbarTitleView.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置ActionBar预制按钮一的文字
	 * 
	 * @param extra	: 显示的文字
	 */
	public void setActionbarExtra(String extra)
	{
		if (StringUtil.isNotEmpty(extra))
		{
			actionbarExtraView.setText(extra);
			actionbarExtraView.setVisibility(View.VISIBLE);
		} else
		{
			actionbarExtraView.setText(null);
			actionbarExtraView.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置ActionBar预制按钮一的图片
	 * 
	 * @param res : 按钮显示图片资源ID
	 */
	public void setActionbarExtraBackground(int res)
	{
		actionbarExtraView.setBackgroundResource(res);
		actionbarExtraView.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置ActionBar预制按钮一的图片
	 * 
	 * @param able : 按钮显示图片
	 */
	@SuppressWarnings("deprecation")
	public void setActionbarExtraBackground(Drawable able)
	{
		if (able != null)
		{
			actionbarExtraView.setBackgroundDrawable(able);
			actionbarExtraView.setVisibility(View.VISIBLE);
		} else
		{
			actionbarExtraView.setBackgroundDrawable(null);
			actionbarExtraView.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置ActionBar预制按钮二的文字
	 * 
	 * @param extra : 按钮显示的文字
	 */
	public void setActionbarExtra2(String extra)
	{
		if (StringUtil.isNotEmpty(extra))
		{
			actionbarExtra2View.setText(extra);
			actionbarExtra2View.setVisibility(View.VISIBLE);
		} else
		{
			actionbarExtra2View.setText(null);
			actionbarExtra2View.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置ActionBar预制按钮二的图片
	 * 
	 * @param res	显示的资源ID
	 */
	public void setActionbarExtra2Background(int res)
	{
		actionbarExtra2View.setBackgroundResource(res);
		actionbarExtra2View.setVisibility(View.VISIBLE);
	}

	/**
	 * 设置ActionBar预制按钮二的图片
	 * 
	 * @param able	显示的Drawable对象
	 */
	@SuppressWarnings("deprecation")
	public void setActionbarExtra2Background(Drawable able)
	{
		if (able != null)
		{
			actionbarExtra2View.setBackgroundDrawable(able);
			actionbarExtra2View.setVisibility(View.VISIBLE);
		} else
		{
			actionbarExtra2View.setBackgroundDrawable(null);
			actionbarExtra2View.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置ActionBar的背景颜色 
	 * 
	 * @param color	颜色值
	 */
	public void setActionBarBackBroundColor(int color)
	{
		actionBarView.setBackgroundColor(color);
	}

	/**
	 * 设置ActionBar的背景资源 
	 * 
	 * @param res	资源ID
	 */
	public void setActionBarBackBroundRes(int res)
	{
		actionBarView.setBackgroundResource(res);
	}

	/**
	 * 获取Actionbar的标题控件
	 * 
	 * @return 标题控件
	 */
	public TextView getActionbarTitleView()
	{
		return actionbarTitleView;
	}

	/**
	 * 获取Actionbar的预制按钮一
	 * 
	 * @return 预制按钮一控件
	 */
	public TextView getActionbarExtraView()
	{
		return actionbarExtraView;
	}

	/**
	 * 获取Actionbar的预制按钮二
	 * 
	 * @return 预制按钮二控件
	 */
	public TextView getActionbarExtra2View()
	{
		return actionbarExtra2View;
	}

	/**
	 * 预制按钮一点击回调，子类如需要处理点击事件，重写此方法
	 */
	protected void onActionbarExtraClick()
	{}

	/**
	 * 预制按钮二点击回调，子类如需要处理点击事件，重写此方法
	 */
	protected void onActionbarExtra2Click()
	{}

	/**
	 * 标题按钮点击回调，子类如需要处理点击事件，重写此方法
	 */
	protected void onActionbarTitleClick()
	{}

	/**
	 * 预制按钮一长按回调，子类如需要处理点击事件，重写此方法
	 */
	protected void onActionbarExtraLongClick()
	{}

	/**
	 * 预制按钮二长按回调，子类如需要处理点击事件，重写此方法
	 */
	protected void onActionbarExtra2LongClick()
	{}

	/**
	 * 标题长按回调，子类如需要处理点击事件，重写此方法
	 */
	protected void onActionbarTitleLongClick()
	{}

	/*
	 * 预置按钮的点击事件类 
	 */
	private class BaseClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			if (v.getId() == R.id.actionbar_title)
			{
				onActionbarTitleClick();
			} else if (v.getId() == R.id.actionbar_extra)
			{
				onActionbarExtraClick();
			} else if (v.getId() == R.id.actionbar_extra2)
			{
				onActionbarExtra2Click();
			}
		}
	}

	/*
	 * 预制按钮的 长按事件类
	 */
	private class BaseLongClickListener implements OnLongClickListener
	{

		@Override
		public boolean onLongClick(View v)
		{
			if (v.getId() == R.id.actionbar_title)
			{
				onActionbarTitleLongClick();
			} else if (v.getId() == R.id.actionbar_extra)
			{
				onActionbarExtraLongClick();
			} else if (v.getId() == R.id.actionbar_extra2)
			{
				onActionbarExtra2LongClick();
			}
			return true;
		}
	}
}
