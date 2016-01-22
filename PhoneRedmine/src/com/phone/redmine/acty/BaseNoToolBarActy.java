package com.phone.redmine.acty;

import android.os.Bundle;
import android.view.ViewGroup;
import com.phone.redmine.util.DisplayUtil;

/** 
 * 没有ToolBar的Activity
 */
public class BaseNoToolBarActy extends BaseActy
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

		if (isImmerse())
		{
			setImmersePaddingTop();
		}
	}

	protected void setImmersePaddingTop()
	{
		ViewGroup viewGroup = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
		viewGroup.setPadding(0, DisplayUtil.getStatusBarHeight(this), 0, 0);
	}
}
