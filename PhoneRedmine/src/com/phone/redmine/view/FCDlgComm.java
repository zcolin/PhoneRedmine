package com.phone.redmine.view;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.phone.redmine.R;

/**
 * 普通对话框，有两个按钮
 */
public class FCDlgComm extends FCDlg implements OnClickListener
{

	private TextView			tvTitle;			// 标题文字
	private TextView			tvMakeSure;		// 确定按钮
	private TextView			tvCancel;			// 取消按钮
	private TextView			tvMessage;			// 消息内容

	protected FCSubmitInterface	submitInterface;	// 点击确定按钮回调接口
	protected FCCancelInterface	cancelInterface;	// 点击取消按钮回调接口

	/**
	 * @param context
	 * @param strMsg 		显示的对话框信息
	 */
	public FCDlgComm (Activity context, String strMsg)
	{
		this(context, "提示", strMsg, null, null);
	}
	
	/**
	 * @param context
	 * @param strMsg 		显示的对话框信息
	 */
	public FCDlgComm (Activity context, String strTitle, String strMsg)
	{
		this(context, strTitle, strMsg, null, null);
	}

	/**
	 * @param context
	 * @param strTitle		标题文字
	 * @param strMsg 		显示的对话框信息
	 * @param strMakeSure 	确定按钮文字
	 * @param strCancel 	取消按钮文字
	 */
	public FCDlgComm (Activity context, String strTitle, String strMsg, String strMakeSure, String strCancel)
	{
		super(context, R.layout.dialog_common);

		initRes();
		initData(strTitle, strMsg, strMakeSure, strCancel);
	}

	@Override
	public void onClick(View v)
	{
		if (v == tvMakeSure)
		{
			if (submitInterface != null)
			{
				boolean flag = submitInterface.submit();
				if (flag)
				{
					cancel();
				}
			}
		} else if (v == tvCancel)
		{
			if (cancelInterface != null)
			{
				boolean flag = cancelInterface.cancel();
				if (flag)
				{
					cancel();
				}
			}
			else
			{
				cancel();
			}
		}
	}

	/**
	 * 刷新页面
	 * 
	 * @param strTitle		标题文字
	 * @param strMsg 		显示的对话框信息
	 * @param strMakeSure 	确定按钮文字
	 * @param strCancel 	取消按钮文字
	 */
	public void notifyDataChanged(String strTitle, String strMsg, String strMakeSure, String strCancel)
	{
		initData(strTitle, strMsg, strMakeSure, strCancel);
	}

	/** 
	 * @param strTitle		标题文字
	 * @param strMsg 		显示的对话框信息
	 */
	public void notifyDataChanged(String strTitle, String strMsg)
	{
		initData(strTitle, strMsg, null, null);
	}

	/**
	 * 添加确定回调接口
	 * 
	 * @param callbackSure
	 */
	public void addSubmitListener(FCSubmitInterface submitInterface)
	{
		this.submitInterface = submitInterface;
	}

	/**
	 * 添加取消回调接口
	 * 
	 * @param callbackCancel
	 */
	public void addCancelListener(FCCancelInterface cancelInterface)
	{
		this.cancelInterface = cancelInterface;
	}

	private void initRes()
	{
		tvCancel = (TextView) getView(R.id.dialog_cancelbutton);
		tvMakeSure = (TextView) getView(R.id.dialog_okbutton);
		tvTitle = (TextView) getView(R.id.dialog_tilte);
		tvMessage = (TextView) getView(R.id.dialog_message);
		tvMakeSure.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
	}

	/*
	 * 初始化数据
	 * 
	 * @param strTitle		标题文字
	 * @param strMsg 		显示的对话框信息
	 * @param strMakeSure 	确定按钮文字
	 * @param strCancel 	取消按钮文字
	 */
	private void initData(String strTitle, String strMsg, String strMakeSure, String strCancel)
	{
		tvMakeSure.setText(strMakeSure == null ? "确定" : strMakeSure);
		tvCancel.setText(strCancel == null ? "取消" : strCancel);
		tvTitle.setText(strTitle);
		tvMessage.setText(strMsg);
	}

}
