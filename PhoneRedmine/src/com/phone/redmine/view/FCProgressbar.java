package com.phone.redmine.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;
import com.phone.redmine.R;

/**
 * 进度条封装类
 */
public class FCProgressbar
{

	private Context			conText	= null;
	private ProgressDialog	progressDialog;
	private TextView		tvMessage;

	public FCProgressbar (Context context)
	{
		conText = context;
		progressDialog = new ProgressDialog(conText);
		progressDialog.setCancelable(false);
	}

	public void showBar()
	{
		progressDialog.show();
		progressDialog.setContentView(R.layout.progress);
		tvMessage = (TextView) progressDialog.findViewById(R.id.progressBar_tv);
	}

	public boolean isShow()
	{
		return progressDialog.isShowing();
	}

	public void hideBar()
	{
		if (progressDialog.isShowing())
		{
			progressDialog.dismiss();
		}
	}

	public void setMessage(int message)
	{
		if (tvMessage != null)
			tvMessage.setText(message);
	}

	public void setMessage(String message)
	{
		if (tvMessage != null)
			tvMessage.setText(message);
	}
}
