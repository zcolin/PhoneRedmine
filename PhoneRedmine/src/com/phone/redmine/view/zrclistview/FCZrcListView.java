package com.phone.redmine.view.zrclistview;

import com.phone.redmine.util.DisplayUtil;
import android.content.Context;
import android.util.AttributeSet;


public class FCZrcListView extends ZrcListView
{
	public FCZrcListView(Context context)
	{
		super(context);
		initRes(context);
	}

	public FCZrcListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initRes(context);
	}

	public FCZrcListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initRes(context);
	}
	
	private void initRes(Context context)
	{
//		设置默认偏移量，主要用于实现透明标题栏功能。（可选）
//      float density = getResources().getDisplayMetrics().density;
//      setFirstTopOffset((int) (50 * density));

        // 设置下拉刷新的样式（可选，但如果没有Header则无法下拉刷新）
        SimpleHeader header = new SimpleHeader(context);
        header.setTextColor(0xff0066aa);
        header.setTextSize(DisplayUtil.dip2px(context, 16));
        header.setCircleColor(0xff33bbee);
        setHeadable(header);

        // 设置加载更多的样式（可选）
        SimpleFooter footer = new SimpleFooter(context);
        footer.setCircleColor(0xff33bbee);
        setFootable(footer);

        // 设置列表项出现动画（可选）
//      setItemAnimForTopIn(R.anim.topitem_in);
//      setItemAnimForBottomIn(R.anim.bottomitem_in);
	}
}
