package com.phone.redmine.acty;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.phone.redmine.R;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.util.SharePrefUtil;

/**
 * RM  快速过滤器页面 
 */

public class RMFastIssueFilterActy extends BaseSecLevelActy
{

	private RadioGroup	rgFilter;
	private RadioGroup	rgCloumn;
	private RadioGroup	rgSort;
	private int			type;			//1为我的问题过滤器  2为项目过滤器

	private int[]		statusResArray;
	private int[]		cloumnResArray;
	private int[]		sortResArray;

	private String		statusKeyWord;
	private String		cloumnKeyWord;
	private String		sortKeyWord;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmacty_issuefilter);
		setActionbarTitle("快速过滤器");
		setActionbarExtraBackground(R.drawable.actionbar_save_selector);
		if (savedInstanceState != null)
		{
			type = savedInstanceState.getInt("type");
		}
		if (type == 0)
		{
			type = getIntent().getIntExtra("type", 2);
		}
		initRes();
		initData();
	}

	@Override
	protected void onActionbarExtraClick()
	{
		SharePrefUtil instance = SharePrefUtil.getInstance();

		/*过滤器*/
		int size = rgFilter.getChildCount();
		for (int i = 0; i < size; i++)
		{
			RadioButton btn = (RadioButton) rgFilter.getChildAt(i);
			if (btn.isChecked())
			{
				instance.setString(statusKeyWord, RMConst.ISSUEFILTER_SORT_STATUS_ARRAY[i]);
				break;
			}
		}

		/*列*/
		size = rgCloumn.getChildCount();
		for (int i = 0; i < size; i++)
		{
			RadioButton btn = (RadioButton) rgCloumn.getChildAt(i);
			if (btn.isChecked())
			{
				instance.setString(cloumnKeyWord, RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY[i]);
				break;
			}
		}

		/*排序*/
		size = rgSort.getChildCount();
		for (int i = 0; i < size; i++)
		{
			RadioButton btn = (RadioButton) rgSort.getChildAt(i);
			if (btn.isChecked())
			{
				instance.setString(sortKeyWord, RMConst.ISSUEFILTER_SORT_SORT_ARRAY[i]);
				break;
			}
		}

		this.setResult(RESULT_OK);
		this.finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (type > 0)
		{
			outState.putInt("type", type);
		}
	}

	private void initRes()
	{
		rgFilter = (RadioGroup) findViewById(R.id.rmactyissuefilter_filter_rg);
		rgCloumn = (RadioGroup) findViewById(R.id.rmactyissuefilter_cloumn_rg);
		rgSort = (RadioGroup) findViewById(R.id.rmactyissuefilter_sort_rg);

		statusResArray = new int[]
		{
				R.id.rmactyissuefilter_filter_rb_opened,
				R.id.rmactyissuefilter_filter_rb_closed,
				R.id.rmactyissuefilter_filter_rb_all
		};
		cloumnResArray = new int[]
		{
				R.id.rmactyissuefilter_cloumn_rb_issueid,
				R.id.rmactyissuefilter_cloumn_rb_duedate,
				R.id.rmactyissuefilter_cloumn_rb_startdate,
				R.id.rmactyissuefilter_cloumn_rb_updateon,
				R.id.rmactyissuefilter_cloumn_rb_priority
		};
		sortResArray = new int[]
		{
				R.id.rmactyissuefilter_rb_asc,
				R.id.rmactyissuefilter_rb_desc
		};
	}

	private void initData()
	{
		statusKeyWord = type == 2 ? RMConst.SHARE_ISSUEFILTER_PROJECT_STATUS : RMConst.SHARE_ISSUEFILTER_MY_STATUS;
		cloumnKeyWord = type == 2 ? RMConst.SHARE_ISSUEFILTER_PROJECT_CLOUMN : RMConst.SHARE_ISSUEFILTER_MY_CLOUMN;
		sortKeyWord = type == 2 ? RMConst.SHARE_ISSUEFILTER_PROJECT_SORT : RMConst.SHARE_ISSUEFILTER_MY_SORT;

		String strStatus = SharePrefUtil.getInstance().getString(statusKeyWord, RMConst.ISSUEFILTER_SORT_STATUS_ARRAY[0]);
		String strCloumn = SharePrefUtil.getInstance().getString(cloumnKeyWord, RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY[0]);
		String strSort = SharePrefUtil.getInstance().getString(sortKeyWord, RMConst.ISSUEFILTER_SORT_SORT_ARRAY[1]);

		for (int i = 0; i < RMConst.ISSUEFILTER_SORT_STATUS_ARRAY.length; i++)
		{
			if (RMConst.ISSUEFILTER_SORT_STATUS_ARRAY[i].equals(strStatus))
			{
				rgFilter.check(statusResArray[i]);
				break;
			}
		}

		for (int i = 0; i < RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY.length; i++)
		{
			if (RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY[i].equals(strCloumn))
			{
				rgCloumn.check(cloumnResArray[i]);
				break;
			}
		}

		for (int i = 0; i < RMConst.ISSUEFILTER_SORT_SORT_ARRAY.length; i++)
		{
			if (RMConst.ISSUEFILTER_SORT_SORT_ARRAY[i].equals(strSort))
			{
				rgSort.check(sortResArray[i]);
				break;
			}
		}
	}

}
