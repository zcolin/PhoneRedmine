package com.phone.redmine.view;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.TypedValue;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.entity.RMPairBoolInfo;

/**
 * 单选框组合框，直接传入 ArrayList<RMPairBoolInfo>处理
 */
public class RMDlgRadiogroup extends FCDlg implements OnCheckedChangeListener
{

	private TextView						tvTitle;
	private Context							context;
	private RadioGroup						rgChoise;
	private FCParamSubmitInterface<Integer>	submitInter;	// 点击确定按钮回调接口

	/**
	 * @param context
	 * @param title			标题
	 * @param lstMsg		数据集
	 */
	public RMDlgRadiogroup (Activity context, String title, ArrayList<RMPairBoolInfo> lstMsg)
	{
		this(context, title, lstMsg, 0);
	}

	/**
	 * @param context
	 * @param title			标题
	 * @param lstMsg		数据集
	 * @param choiceId		默认值
	 */
	public RMDlgRadiogroup (Activity context, String title, ArrayList<RMPairBoolInfo> lstMsg, int choiceId)
	{
		super(context, R.layout.dialog_radiogroup);
		this.context = context;
		initRes();
		initData(title, lstMsg, choiceId);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		if (submitInter != null)
		{
			submitInter.submit(checkedId - 100);
			cancel();
		}
	}

	/**
	 * 刷新页面数据
	 * 
	 * @param title			标题
	 * @param lstMsg		数据集
	 * @param choiceId		默认值
	 */
	public void notifyDataChanged(String title, ArrayList<RMPairBoolInfo> lstMsg, int choiceId)
	{
		initData(title, lstMsg, choiceId);
	}

	public void addSubmitListener(FCParamSubmitInterface<Integer> submitInter)
	{
		this.submitInter = submitInter;
	}

	private void initRes()
	{
		tvTitle = (TextView) getView(R.id.dialogradiogroup_title);
		rgChoise = (RadioGroup) getView(R.id.dialogradiogroup_radiogroup);
	}

	/*
	 * @param context
	 * @param title			标题
	 * @param lstMsg		数据集
	 * @param choiceId		默认值
	 */
	private void initData(String title, ArrayList<RMPairBoolInfo> lstMsg, int choiceId)
	{
		int padding = (int) context.getResources().getDimension(R.dimen.dimens_mid);
		float textSize = context.getResources().getDimension(R.dimen.textsize_big);
		LayoutParams layout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		tvTitle.setText(title);
		rgChoise.removeAllViews();
		rgChoise.setOnCheckedChangeListener(null);
		int size = lstMsg.size();
		for (int i = 0; i < size; i++)
		{
			RadioButton btn = new AppCompatRadioButton(context);
			btn.setText(lstMsg.get(i).name);
			btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
			btn.setPadding(0, padding, 0, padding);
			btn.setBackgroundResource(R.drawable.common_listitem_tc);
			btn.setId(i + 100);
			if (choiceId == lstMsg.get(i).id)
			{
				btn.setChecked(true);
			}
			rgChoise.addView(btn, layout);
		}
		rgChoise.setOnCheckedChangeListener(this);
	}
}
