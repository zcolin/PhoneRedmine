package com.phone.redmine.acty;

import android.os.Bundle;
import com.phone.redmine.R;

/**
 * 默认设置了返回图片，实现返回操作,继承此类的子类不必再实现setActionbarTitleBackground，
 * 如果需要设置标题文字，可以使用setActionbarTitle
 */
public class BaseSecLevelActy extends BaseToolBarActy
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void setContentView(int layoutResID)
	{
		super.setContentView(layoutResID);
		setActionbarTitleBackground(R.drawable.actionbar_back_selector);
	}

	@Override
	protected void onActionbarTitleClick()
	{
		this.finish();
	}
}
