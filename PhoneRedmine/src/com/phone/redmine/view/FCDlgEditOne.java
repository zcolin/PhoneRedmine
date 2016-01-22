package com.phone.redmine.view;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.phone.redmine.R;

/**
 * 带有一个输入框的对话框 ，有两个按钮
 */
public class FCDlgEditOne extends FCDlg implements OnClickListener
{

	private TextView						tvMakeSure;		// 确定按钮
	private TextView						tvCancel;			// 取消按钮
	private TextView						tvTitle;			// 消息内容
	private EditText						etEdit;			// 编辑框

	private FCParamSubmitInterface<String>	submitInterface;
	private FCCancelInterface				cancelInterface;

	/**
	 * @param context
	 * @param strTitle 		显示的对话框标题
	 */
	public FCDlgEditOne (Activity context, String strTitle)
	{
		this(context, strTitle, "", null, null);
	}

	/**
	 * @param context
	 * @param strMsg 		显示的对话框标题
	 * @param editText 		输入框反填的文字
	 */
	public FCDlgEditOne (Activity context, String strTitle, String editText)
	{
		this(context, strTitle, editText, null, null);
	}

	/**
	 * @param context
	 * @param strMsg 		显示的对话框标题
	 * @param editText 		输入框反填的文字
	 * @param strMakeSure 	确定按钮文字
	 * @param strCancel 	取消按钮文字
	 */
	public FCDlgEditOne (Activity context, String strTitle, String editText, String strMakeSure, String strCancel)
	{
		super(context, R.layout.dialog_editone);
		setDlgBackGround(R.drawable.bg_white_rect_nopadding);

		initRes();
		initData(strTitle, editText, strMakeSure, strCancel);
	}

	@Override
	public void onClick(View v)
	{
		if (v == tvMakeSure)
		{
			if (submitInterface != null)
			{
				boolean flag = submitInterface.submit(etEdit.getText().toString());
				if (flag)
				{
					cancel();
				}
			}
		} else if (v == tvCancel)
		{
			if (cancelInterface != null)
			{
				cancelInterface.cancel();
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
	 * @param strTitle 		显示的对话框标题
	 */
	public void notifyDataChanged(String strTitle)
	{
		initData(strTitle, null, null, null);
	}

	/**
	 * 刷新页面
	 * 
	 * @param strTitle 		显示的对话框标题
	 * @param editText 		输入框反填的文字
	 * @param strMakeSure 	确定按钮文字
	 * @param strCancel 	取消按钮文字
	 */
	public void notifyDataChanged(String strTitle, String editText, String strMakeSure, String strCancel)
	{
		initData(strTitle, editText, strMakeSure, strCancel);
	}

	/**
	 * 设置说明文字
	  * @param str
	 */
	public FCDlgEditOne setInstruction(CharSequence str)
	{
		TextView tv = (TextView) getView(R.id.dlgeditone_instruction);
		if (str != null)
		{
			tv.setVisibility(View.VISIBLE);
			tv.setText(str);
		} else
		{
			tv.setVisibility(View.GONE);
		}
		return this;
	}

	/**
	 * 添加确定回调接口
	 * 
	 * @param callbackSure
	 */
	public void addSubmitListener(FCParamSubmitInterface<String> submitInterface)
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

	/**
	 * 获取输入框对象
	 * 
	 * @return
	 */
	public EditText getEditText()
	{
		return etEdit;
	}

	private void initRes()
	{
		tvMakeSure = (TextView) getView(R.id.dialog_okbutton);
		tvCancel = (TextView) getView(R.id.dialog_cancelbutton);
		tvTitle = (TextView) getView(R.id.dlgeditone_title);
		etEdit = (EditText) getView(R.id.dlgeditone_edit);
		tvMakeSure.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
	}

	/*
	 * @param strTitle 		显示的对话框信息
	 * @param editText 		输入框反填的文字
	 * @param strMakeSure 	确定按钮文字
	 * @param strCancel 	取消按钮文字
	 */
	private void initData(String strTitle, String editText, String strMakeSure, String strCancel)
	{
		tvMakeSure.setText(strMakeSure == null ? "确定" : strMakeSure);
		tvCancel.setText(strCancel == null ? "取消" : strCancel);
		etEdit.setText(editText);
		etEdit.selectAll();
		tvTitle.setText(strTitle);
	}
}
