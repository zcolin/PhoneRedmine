package com.phone.redmine.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.phone.redmine.R;

/**
 * 普遍对话框，有一个确定按钮
 */
public class FCAlert extends FCDlg implements OnClickListener, OnCancelListener
{

	private TextView			tvMakeSure;		// 确定按钮
	private TextView			tvCancel;			// 取消按钮
	private TextView			tvMessage;			// 消息内容
	private TextView			tvTitle;

	protected FCSubmitInterface	submitInterface;	// 点击确定按钮回调接口
	protected FCCancelInterface	cancelInterface;	// 点击取消按钮回调接口

	/**
	 * @param context
	 * @param strMsg		提示内容
	 */
	public FCAlert (Context context, String strMsg)
	{
		this(context, "提示", strMsg, null);
	}

	/**
	 * @param context
	 * @param strMsg		提示内容
	 */
	public FCAlert (Context context, String strTitle, String strMsg)
	{
		this(context, strTitle, strMsg, null);
	}

	/**
	 * @param context
	 * @param strTitle		标题
	 * @param strMsg		提示内容
	 * @param strBtn		按钮上的文字
	 */
	public FCAlert (Context context, String strTitle, String strMsg, String strBtn)
	{
		super(context, R.layout.dialog_common);

		initRes();
		initData(strTitle, strMsg, strBtn);
	}

	@Override
	public void onClick(View v)
	{
		if (v == tvMakeSure)
		{
			cancel();
			if (submitInterface != null)
			{
				submitInterface.submit();
			}
		}
	}

	@Override
	public void onCancel(DialogInterface dialog)
	{
		if (tvMessage != null)
			tvMessage.setText("");

		if (cancelInterface != null)
		{
			cancelInterface.cancel();
		}
	}

	/**
	 * 添加确定回调接口
	 * 
	 * @param callbackSure
	 */
	public void addSubmitInterface(FCSubmitInterface submitInterface)
	{
		this.submitInterface = submitInterface;
	}

	/**
	 * 添加取消回调接口
	 * 
	 * @param callbackCancel
	 */
	public void addCancelIngerface(FCCancelInterface cancelInterface)
	{
		this.cancelInterface = cancelInterface;
	}

	/**
	 * 设置提示内容对齐方式
	 * 
	 * @param gravity
	 */
	public void setMessageGravity(int gravity)
	{
		tvMessage.setGravity(gravity);
	}

	/**
	 * 刷新页面	
	 * 	
	 * @param strMsg		提示内容
	 */
	public void notifyDataChanged(String strMsg)
	{
		tvMessage.setText(strMsg);
	}

	/**
	 * 刷新页面
	 * @param strTitle		标题
	 * @param strMsg		提示内容
	 * @param strBtn		按钮上的文字
	 */
	public void notifyDataChanged(String strTitle, String strMsg, String strBtn)
	{
		initData(strTitle, strMsg, strBtn);
	}

	/**
	 * 追加字符串，会自动回车
	 * 
	 * @param strMsg		要追加的内容
	 */
	public void append(String strMsg)
	{
		String str = tvMessage.getText().toString();
		if (str.length() > 0)
		{
			if (!str.contains(strMsg))
			{
				tvMessage.append("\n" + strMsg);
			}
		} else
		{
			tvMessage.setText(strMsg);
		}
	}

	private void initRes()
	{
		tvMakeSure = (TextView) getView(R.id.dialog_okbutton);
		tvCancel = (TextView) getView(R.id.dialog_cancelbutton);
		tvMessage = (TextView) getView(R.id.dialog_message);
		tvTitle = (TextView) getView(R.id.dialog_tilte);
		tvMessage.setMovementMethod(new ScrollingMovementMethod());

		tvMakeSure.setOnClickListener(this);
		tvCancel.setVisibility(View.GONE);
		this.setOnCancelListener(this);
	}

	private void initData(String strTitle, String strMsg, String strBtn)
	{
		tvTitle.setText(strTitle);
		tvMessage.setText(strMsg);
		tvMakeSure.setText(strBtn == null ? "确定" : strBtn);
	}
}
