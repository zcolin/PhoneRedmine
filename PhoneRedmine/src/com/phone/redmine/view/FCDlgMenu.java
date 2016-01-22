package com.phone.redmine.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioGroup.LayoutParams;
import android.widget.TextView;
import com.phone.redmine.R;

/**
 * 菜单弹出框
 */
public class FCDlgMenu extends FCDlg implements OnClickListener
{

	private LinearLayout					llMenu;		//菜单容器布局
	private TextView						tvTitle;		//标题控件
	private FCParamSubmitInterface<Integer>	submitInter;	// 点击确定按钮回调接口
	private Context							context;

	/**
	 * @param context
	 * @param strTitle			标题
	 * @param attrStr			显示的值集合
	 */
	public FCDlgMenu (Activity context, String[] attrStr)
	{
		this(context, "请选择操作", attrStr);
	}

	/**
	 * @param context
	 * @param strTitle			标题
	 * @param attrStr			显示的值集合
	 */
	public FCDlgMenu (Activity context, String strTitle, String[] attrStr)
	{
		super(context, R.layout.dialog_menu);
		this.context = context;
		initRes();
		initData(strTitle, attrStr);
	}

	@Override
	public void onClick(View v)
	{
		int id = v.getId();
		if (id >= 100 && id < 200)
		{
			if (submitInter != null)
			{
				//返回的是 点击的attrStr的index
				submitInter.submit(id - 100);
				cancel();
			}
		}
	}

	/**
	 * 添加点击回调接口
	 * 
	 * @param submitInter
	 */
	public void addSubmitListener(FCParamSubmitInterface<Integer> submitInter)
	{
		this.submitInter = submitInter;
	}

	private void initRes()
	{
		llMenu = (LinearLayout) getView(R.id.dialogmenu_ll);
		tvTitle = (TextView) getView(R.id.dialogmenu_title);
	}

	/*
	 * 初始化数据
	 * 
	 * param strTitle			标题
	 * @param attrStr			显示的值集合
	 */
	private void initData(String strTitle, String[] attrStr)
	{
		tvTitle.setText(strTitle);

		int padding = (int) context.getResources().getDimension(R.dimen.dimens_big);
		float textSize = context.getResources().getDimension(R.dimen.textsize_big);
		int color = context.getResources().getColor(R.color.gray_dark);
		LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		llMenu.removeAllViews();
		int size = attrStr.length;
		for (int i = 0; i < size; i++)
		{
			TextView tv = new AppCompatTextView(context);
			tv.setText(attrStr[i]);
			tv.setBackgroundResource(R.drawable.common_listitem_tc);
			tv.setTextColor(color);
			tv.setPadding(0, padding, 0, padding);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			tv.setId(i + 100);
			tv.setOnClickListener(this);
			llMenu.addView(tv, layout);
		}
	}
}
