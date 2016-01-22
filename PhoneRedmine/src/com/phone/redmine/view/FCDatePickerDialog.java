package com.phone.redmine.view;

import android.app.DatePickerDialog;
import android.content.Context;

/**
 * 系统的DatePickerDialog有Bug，会调用两次onDateSet， 注释掉super.onStop()可解决
 */
public class FCDatePickerDialog extends DatePickerDialog
{

	public FCDatePickerDialog (Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth)
	{
		super(context, callBack, year, monthOfYear, dayOfMonth);
	}

	@Override
	protected void onStop()
	{
		//super.onStop();
	}
}
