package com.phone.redmine.acty;

import java.util.ArrayList;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.biz.RMBiz;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.RMIssueFilter;
import com.phone.redmine.entity.RMPairBoolInfo;
import com.phone.redmine.entity.RMPairInfo;
import com.phone.redmine.util.StringUtil;
import com.phone.redmine.view.FCDlg.FCParamSubmitInterface;
import com.phone.redmine.view.RMDlgRadiogroup;
import com.phone.redmine.view.crouton.FCCrouton;

/**
 * RM 高级过滤器页面 
 * 此页面包含添加 和 修改
 * issueFilter 是传入的已存在的过滤器，issueFilter中只包含Id；
 * filterObj是本页面的过滤对象，包含ID和name
 * 当issueFilter传入时，为了反填显示信息，需要根据加载各个数据列表，然后根据issueFilter中的ID与列表中的数据对应，然后将至赋给filterObj进行显示
 */
public class RMAdvancedIssueFilterActy extends BaseSecLevelActy implements OnClickListener
{

	private ArrayList<RMPairBoolInfo>	lstProject		= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstIssueStatus	= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstPriority		= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstTrackers		= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstAssignTo		= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstVersions		= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstCategories	= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstCloumns		= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstSorts		= new ArrayList<RMPairBoolInfo>();
	private RMIssueFilter				issueFilter;
	private RMIssuePairFilter			filterObj		= new RMIssuePairFilter();
	private EditText					etFilterName;
	private TextView					tvProject;
	private TextView					tvStatus;
	private TextView					tvTracker;
	private TextView					tvAssignto;
	private TextView					tvPriority;
	private TextView					tvVersion;
	private TextView					tvCategory;
	private TextView					tvColoumn;
	private TextView					tvSort;
	private int							opType;											//1操作按钮显示保存， 2显示为过滤

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmacty_advancedissuefilter);
		setActionbarTitle("高级过滤器");
		checkData(savedInstanceState);
		initRes();
		initData();
	}

	@Override
	protected void onActionbarExtraClick()
	{
		if (issueFilter == null)
		{
			issueFilter = new RMIssueFilter();
		}

		issueFilter.project_id = filterObj.project.id;
		issueFilter.assigned_to_id = filterObj.assigned_to.id;
		issueFilter.category_id = filterObj.category.id;
		issueFilter.fixed_version_id = filterObj.fixed_version.id;
		issueFilter.priority_id = filterObj.priority.id;
		issueFilter.status_id = filterObj.status.id;
		issueFilter.tracker_id = filterObj.tracker.id;
		issueFilter.sort = RMConst.ISSUEFILTER_SORT_SORT_ARRAY[filterObj.sort.id];
		issueFilter.cloumn = RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY[filterObj.cloumn.id];
		if (opType == 1)
		{
			if (StringUtil.isBlank(etFilterName.getText()))
			{
				FCCrouton.alert(this, "过滤名称不能为空");
				return;
			}
			issueFilter.name = etFilterName.getText().toString();
			RMDBLocal.getInstance().insertIssueSelfFilter(issueFilter);
			this.setResult(RESULT_OK);
			this.finish();
		} else if (opType == 2)
		{
			Intent intent = new Intent();
			intent.putExtra("issueFilter", issueFilter);
			intent.setClass(this, RMIssuesListActy.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (opType > 0)
		{
			outState.putInt("type", opType);
			outState.putParcelable("filter", issueFilter);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rmadvancedactyissuefilter_filter_project_rl:
				showProjectDlg();
				break;
			case R.id.rmadvancedactyissuefilter_filter_status_rl:
				showStatusDlg();
				break;
			case R.id.rmadvancedactyissuefilter_filter_priority_rl:
				showPriorityDlg();
				break;
			case R.id.rmadvancedactyissuefilter_filter_tracker_rl:
				showTrackersDlg();
				break;
			case R.id.rmadvancedactyissuefilter_filter_assignto_rl:
				showAssignToDlg();
				break;
			case R.id.rmadvancedactyissuefilter_filter_version_rl:
				showVersionsDlg();
				break;
			case R.id.rmadvancedactyissuefilter_filter_category_rl:
				showCategoriesDlg();
				break;
			case R.id.rmadvancedactyissuefilter_sort_cloumn_rl:
				showCloumnDlg();
				break;
			case R.id.rmadvancedactyissuefilter_sort_sort_rl:
				showSortDlg();
				break;
			default:
				break;
		}
	}

	/*
	 * 根据传递过来的数据判断操作类型，并获取数据
	 * 	
	 * @param savedInstanceState	
	 */
	private void checkData(Bundle savedInstanceState)
	{
		if (savedInstanceState != null)
		{
			opType = savedInstanceState.getInt("type");
			issueFilter = savedInstanceState.getParcelable("filter");

		}
		if (opType == 0)
		{
			opType = getIntent().getIntExtra("type", 2);
			issueFilter = getIntent().getParcelableExtra("filter");
		}

		if (opType == 1)
		{
			setActionbarExtraBackground(R.drawable.actionbar_save_selector);
		} else
		{
			setActionbarExtraBackground(R.drawable.actionbar_filter_selector);
		}
	}

	private void initRes()
	{

		etFilterName = (EditText) findViewById(R.id.rmadvancedactyissuefilter_name);
		tvProject = (TextView) findViewById(R.id.rmadvancedactyissuefilter_filter_project);
		tvStatus = (TextView) findViewById(R.id.rmadvancedactyissuefilter_filter_status);
		tvTracker = (TextView) findViewById(R.id.rmadvancedactyissuefilter_filter_tracker);
		tvAssignto = (TextView) findViewById(R.id.rmadvancedactyissuefilter_filter_assignto);
		tvPriority = (TextView) findViewById(R.id.rmadvancedactyissuefilter_filter_priority);
		tvVersion = (TextView) findViewById(R.id.rmadvancedactyissuefilter_filter_version);
		tvCategory = (TextView) findViewById(R.id.rmadvancedactyissuefilter_filter_category);
		tvColoumn = (TextView) findViewById(R.id.rmadvancedactyissuefilter_sort_cloumn);
		tvSort = (TextView) findViewById(R.id.rmadvancedactyissuefilter_sort_sort);
		findViewById(R.id.rmadvancedactyissuefilter_filter_project_rl).setOnClickListener(this);
		findViewById(R.id.rmadvancedactyissuefilter_filter_status_rl).setOnClickListener(this);
		findViewById(R.id.rmadvancedactyissuefilter_filter_tracker_rl).setOnClickListener(this);
		findViewById(R.id.rmadvancedactyissuefilter_filter_assignto_rl).setOnClickListener(this);
		findViewById(R.id.rmadvancedactyissuefilter_filter_priority_rl).setOnClickListener(this);
		findViewById(R.id.rmadvancedactyissuefilter_filter_version_rl).setOnClickListener(this);
		findViewById(R.id.rmadvancedactyissuefilter_filter_category_rl).setOnClickListener(this);
		findViewById(R.id.rmadvancedactyissuefilter_sort_cloumn_rl).setOnClickListener(this);
		findViewById(R.id.rmadvancedactyissuefilter_sort_sort_rl).setOnClickListener(this);
	}

	private void initData()
	{
		if (opType == 2)
		{
			etFilterName.setVisibility(View.GONE);
		} else
		{
			etFilterName.setVisibility(View.VISIBLE);
			if (issueFilter != null)
			{
				etFilterName.setText(issueFilter.name);
			}
		}

		initProjectData(issueFilter);
		initStatusData(issueFilter);
		initPriority(issueFilter);
		initTrackers(issueFilter);
		initAssignTo(issueFilter);
		initVersions(issueFilter);
		initCategories(issueFilter);
		initCloumns(issueFilter);
		initSorts(issueFilter);
		showData();
	}

	/*
	 * 设置显示信息
	 */
	private void showData()
	{
		tvProject.setText(filterObj.project.name);
		tvStatus.setText(filterObj.status.name);
		tvTracker.setText(filterObj.tracker.name);
		tvAssignto.setText(filterObj.assigned_to.name);
		tvPriority.setText(filterObj.priority.name);
		tvVersion.setText(filterObj.fixed_version.name);
		tvCategory.setText(filterObj.category.name);
		tvColoumn.setText(filterObj.cloumn.name);
		tvSort.setText(filterObj.sort.name);
	}

	/*
	 * 如果项目变化之后需要根据项目对数据进行重新检查
	 */
	private void onProjectChange()
	{
		initTrackers(null);
		initAssignTo(null);
		initVersions(null);
		initCategories(null);
	}

	/*
	 * 添加默认子项
	 * 
	 * @param lstCommon		要添加到的列表
	 */
	private void addDefaultInfo(ArrayList<RMPairBoolInfo> lstCommon)
	{
		RMPairBoolInfo commonInfo = new RMPairBoolInfo();
		commonInfo.id = 0;
		commonInfo.name = "";
		lstCommon.add(0, commonInfo);
	}

	/*
	 * 初始化项目数据
	 * 
	 * @param filter
	 */
	private void initProjectData(RMIssueFilter filter)
	{
		lstProject.clear();
		RMDBLocal.getInstance().getAllProjects(lstProject);
		for (int i = 0; i < lstProject.size(); i++)
		{
			RMPairBoolInfo info = lstProject.get(i);
			if (info.bool1)
			{
				lstProject.remove(i);
				i--;
			}
		}

		if (filter == null)
		{
			filterObj.project.id = 0;
			filterObj.project.name = null;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstProject, filter.project_id);
			filterObj.project.id = filter.project_id;
			filterObj.project.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstProject);
	}

	/*
	 * 初始化状态
	 * 
	 * @param filter
	 */
	private void initStatusData(RMIssueFilter filter)
	{
		lstIssueStatus.clear();
		RMPairBoolInfo defaultInfo = null;
		RMDBLocal.getInstance().getAllIssueStatus(lstIssueStatus);
		for (int i = 0; i < lstIssueStatus.size(); i++)
		{
			RMPairBoolInfo info = lstIssueStatus.get(i);
			if (info.bool1)
			{
				defaultInfo = info;
				break;
			}
		}
		if (filter == null)
		{
			filterObj.status.id = defaultInfo == null ? 0 : defaultInfo.id;
			filterObj.status.name = defaultInfo == null ? null : defaultInfo.name;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstIssueStatus, filter.status_id);
			filterObj.status.id = filter.status_id;
			filterObj.status.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstIssueStatus);
	}

	/*
	 * 初始化优先级
	 * 
	 * @param filter
	 */
	private void initPriority(RMIssueFilter filter)
	{
		lstPriority.clear();
		RMPairBoolInfo defaultInfo = null;
		RMDBLocal.getInstance().getAllPriorities(lstPriority);
		for (int i = 0; i < lstPriority.size(); i++)
		{
			RMPairBoolInfo info = lstPriority.get(i);
			if (info.bool1)
			{
				defaultInfo = info;
				break;
			}
		}
		if (filter == null)
		{
			filterObj.priority.id = defaultInfo == null ? 0 : defaultInfo.id;
			filterObj.priority.name = defaultInfo == null ? null : defaultInfo.name;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstPriority, filter.priority_id);
			filterObj.priority.id = filter.priority_id;
			filterObj.priority.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstPriority);
	}

	/*
	 * 初始化跟踪
	 * 
	 * @param filter
	 */
	private void initTrackers(RMIssueFilter filter)
	{
		lstTrackers.clear();
		RMDBLocal.getInstance().getTrackersByProjectId(lstTrackers, filterObj.project.id);
		if (filter == null)
		{
			filterObj.tracker.id = 0;
			filterObj.tracker.name = null;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstTrackers, filter.tracker_id);
			filterObj.tracker.id = filter.tracker_id;
			filterObj.tracker.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstTrackers);
	}

	/*
	 * 初始化指派给
	 * 
	 * @param filter
	 */
	private void initAssignTo(RMIssueFilter filter)
	{
		lstAssignTo.clear();
		RMDBLocal.getInstance().getStaffsByProjectId(lstAssignTo, filterObj.project.id);
		if (filter == null)
		{
			filterObj.assigned_to.id = 0;
			filterObj.assigned_to.name = null;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstAssignTo, filter.assigned_to_id);
			filterObj.assigned_to.id = filter.assigned_to_id;
			filterObj.assigned_to.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstAssignTo);
	}

	/*
	 * 初始化版本
	 * 
	 * @param filter
	 */
	private void initVersions(RMIssueFilter filter)
	{
		lstVersions.clear();
		RMDBLocal.getInstance().getVersionsByProjectId(lstVersions, filterObj.project.id);
		if (filter == null)
		{
			filterObj.fixed_version.id = 0;
			filterObj.fixed_version.name = null;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstVersions, filter.fixed_version_id);
			filterObj.fixed_version.id = filter.fixed_version_id;
			filterObj.fixed_version.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstVersions);
	}

	/*
	 * 初始化类别
	 * 
	 * @param filter
	 */
	private void initCategories(RMIssueFilter filter)
	{
		lstCategories.clear();
		if (filterObj.project.id > 0)
		{
			RMDBLocal.getInstance().getIssueCategorysByProjectId(lstCategories, filterObj.project.id);
		}
		if (filter == null)
		{
			filterObj.category.id = 0;
			filterObj.category.name = null;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstCategories, filter.category_id);
			filterObj.category.id = filter.category_id;
			filterObj.category.name = info == null ? null : info.name;
		}

		addDefaultInfo(lstCategories);
	}

	/*
	 * 初始化列
	 * 
	 * @param filter
	 */
	private void initCloumns(RMIssueFilter filter)
	{
		lstCloumns.clear();
		int length = RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY.length;
		for (int i = 0; i < length; i++)
		{
			RMPairBoolInfo info = new RMPairBoolInfo();
			info.id = i;
			info.name = RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY_NAME[i];
			lstCloumns.add(info);
		}
		if (filter == null)
		{
			filterObj.cloumn.id = 0;
			filterObj.cloumn.name = RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY_NAME[filterObj.cloumn.id];
		} else
		{
			for (int i = 0; i < length; i++)
			{
				String str = RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY[i];
				if (str.equals(filter.cloumn))
				{
					filterObj.cloumn.id = i;
					filterObj.cloumn.name = RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY_NAME[i];
					break;
				}
			}
		}
	}

	/*
	 * 初始化排序
	 * 
	 * @param filter
	 */
	private void initSorts(RMIssueFilter filter)
	{
		lstSorts.clear();
		int length = RMConst.ISSUEFILTER_SORT_SORT_ARRAY.length;
		for (int i = 0; i < length; i++)
		{
			RMPairBoolInfo info = new RMPairBoolInfo();
			info.id = i;
			info.name = RMConst.ISSUEFILTER_SORT_SORT_ARRAY_NAME[i];
			lstSorts.add(info);
		}
		if (filter == null)
		{
			filterObj.sort.id = 1;
			filterObj.sort.name = RMConst.ISSUEFILTER_SORT_SORT_ARRAY_NAME[filterObj.sort.id];
		} else
		{
			for (int i = 0; i < length; i++)
			{
				String str = RMConst.ISSUEFILTER_SORT_SORT_ARRAY[i];
				if (str.equals(filter.sort))
				{
					filterObj.sort.id = i;
					filterObj.sort.name = RMConst.ISSUEFILTER_SORT_SORT_ARRAY_NAME[i];
					break;
				}
			}
		}
	}

	/*
	 * 显示  项目选择   对话框
	 */
	private void showProjectDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "项目", lstProject, filterObj.project.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				filterObj.project.id = lstProject.get(iData).id;
				filterObj.project.name = lstProject.get(iData).name;
				onProjectChange();
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 显示  优先级选择   对话框
	 */
	private void showPriorityDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "优先级", lstPriority, filterObj.priority.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				filterObj.priority.id = lstPriority.get(iData).id;
				filterObj.priority.name = lstPriority.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 显示  状态选择   对话框
	 */
	private void showStatusDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "问题状态", lstIssueStatus, filterObj.status.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				filterObj.status.id = lstIssueStatus.get(iData).id;
				filterObj.status.name = lstIssueStatus.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 显示  跟踪选择   对话框
	 */
	private void showTrackersDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "跟踪状态", lstTrackers, filterObj.tracker.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				filterObj.tracker.id = lstTrackers.get(iData).id;
				filterObj.tracker.name = lstTrackers.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 显示  指派给选择   对话框
	 */
	private void showAssignToDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "指派人", lstAssignTo, filterObj.assigned_to.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				filterObj.assigned_to.id = lstAssignTo.get(iData).id;
				filterObj.assigned_to.name = lstAssignTo.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 显示  版本选择   对话框
	 */
	private void showVersionsDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "版本", lstVersions, filterObj.fixed_version.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				filterObj.fixed_version.id = lstVersions.get(iData).id;
				filterObj.fixed_version.name = lstVersions.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 显示  类别选择   对话框
	 */
	private void showCategoriesDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "类别", lstCategories, filterObj.category.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				filterObj.category.id = lstCategories.get(iData).id;
				filterObj.category.name = lstCategories.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 显示  列选择   对话框
	 */
	private void showCloumnDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "列", lstCloumns, filterObj.cloumn.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				filterObj.cloumn.id = lstCloumns.get(iData).id;
				filterObj.cloumn.name = lstCloumns.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 显示  排序选择   对话框
	 */
	private void showSortDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "排序", lstSorts, filterObj.sort.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				filterObj.sort.id = lstSorts.get(iData).id;
				filterObj.sort.name = lstSorts.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/**
	 * 过滤器信息实体
	 */
	public class RMIssuePairFilter
	{

		public RMPairInfo	project			= new RMPairInfo();
		public RMPairInfo	status			= new RMPairInfo();
		public RMPairInfo	tracker			= new RMPairInfo();
		public RMPairInfo	assigned_to		= new RMPairInfo();
		public RMPairInfo	priority		= new RMPairInfo();
		public RMPairInfo	fixed_version	= new RMPairInfo();
		public RMPairInfo	category		= new RMPairInfo();
		public RMPairInfo	cloumn			= new RMPairInfo();
		public RMPairInfo	sort			= new RMPairInfo();
	}
}
