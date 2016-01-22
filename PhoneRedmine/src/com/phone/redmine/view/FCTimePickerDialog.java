package com.phone.redmine.view;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * 系统的DatePickerDialog有Bug，会调用两次onDateSet， 注释掉super.onStop()可解决
 */
public class FCTimePickerDialog extends TimePickerDialog
{

	public FCTimePickerDialog (Context context, OnTimeSetListener callBack,
			int hourOfDay, int minute, boolean is24HourView)
	{
		super(context, callBack, hourOfDay, minute, is24HourView);
	}

	@Override
	protected void onStop()
	{
		//super.onStop();
	}
}
