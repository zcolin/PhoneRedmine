package com.phone.redmine.acty;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.FileEntity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.AppCompatTextView;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.phone.redmine.R;
import com.phone.redmine.biz.RMBiz;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.RMIssueDetail;
import com.phone.redmine.entity.RMIssueDetail.Attachment;
import com.phone.redmine.entity.RMIssueDetailTransferData;
import com.phone.redmine.entity.RMPairBoolInfo;
import com.phone.redmine.entity.RMPairInfo;
import com.phone.redmine.entity.RMPairStringInfo;
import com.phone.redmine.entity.RMShowItem;
import com.phone.redmine.http.RMHttpResponse;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.http.entity.RMIssuePost;
import com.phone.redmine.http.entity.RMIssueReply;
import com.phone.redmine.http.entity.RMUploadReply;
import com.phone.redmine.http.entity.RMUploadReply.Token;
import com.phone.redmine.util.BitmapUtil;
import com.phone.redmine.util.CalendarUtil;
import com.phone.redmine.util.DeviceUtil;
import com.phone.redmine.util.FileOpenUtil;
import com.phone.redmine.util.FileUtil;
import com.phone.redmine.util.StringUtil;
import com.phone.redmine.util.TimeUtil;
import com.phone.redmine.view.FCDatePickerDialog;
import com.phone.redmine.view.FCDlg.FCParamSubmitInterface;
import com.phone.redmine.view.FCDlg.FCSubmitInterface;
import com.phone.redmine.view.FCDlgComm;
import com.phone.redmine.view.FCDlgEditOne;
import com.phone.redmine.view.FCToast;
import com.phone.redmine.view.RMDlgRadiogroup;
import com.phone.redmine.view.crouton.FCCrouton;

/**
 * RM 问题 添加 和 修改 页面
 */
public class RMIssueAddEditActy extends BaseSecLevelActy implements OnClickListener
{

	/**
	 * 进入 拍照  页面的标识
	 */
	private static final int			RESULT_TAKEPHOTO	= 10;

	/**
	 * 进入 选择文件  页面的标识
	 */
	private static final int			RESULT_SELECTFILE	= 11;
	private ArrayList<RMPairBoolInfo>	lstProject			= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstIssueStatus		= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstPriority			= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstTrackers			= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstAssignTo			= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstVersions			= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstCategories		= new ArrayList<RMPairBoolInfo>();
	private ArrayList<RMPairBoolInfo>	lstDoneRatio		= new ArrayList<RMPairBoolInfo>();
	private RMIssueDetail				issueDetail;
	private RMIssuePair					issuePair			= new RMIssuePair();

	private EditText					etSubject;
	private EditText					etDescription;
	private TextView					tvProject;
	private TextView					tvStatus;
	private TextView					tvTracker;
	private TextView					tvAssignto;
	private TextView					tvPriority;
	private TextView					tvVersion;
	private TextView					tvCategory;
	private TextView					tvNotes;
	private TextView					tvDoneRatio;
	private TextView					tvStartDate;
	private TextView					tvDueDate;

	private RelativeLayout				rlTrack;
	private RelativeLayout				rlPrority;
	private RelativeLayout				rlDoneRatio;
	private RelativeLayout				rlStartDate;
	private RelativeLayout				rlDueDate;
	private RelativeLayout				rlCategory;
	private RelativeLayout				rlFixedVersion;

	private LinearLayout				llAttachment;
	private LinearLayout				llCustomFields;
	private Uri							photoPathUri;
	private RMIssueDetailTransferData	issueDetailTransferData;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		checkData(savedInstanceState);
		initRes();
		if (issueDetailTransferData.issueId > 0)
		{
			getIssueDetailFromServer();
		} else
		{
			if (!issueDetailTransferData.isEmpty())
			{
				issueDetail = new RMIssueDetail();
				if (issueDetailTransferData.projectId > 0)
				{
					RMPairInfo project = new RMPairInfo();
					project.id = issueDetailTransferData.projectId;
					issueDetail.project = project;
				}
				issueDetail.subject = issueDetailTransferData.subject;
				issueDetail.description = issueDetailTransferData.description;
				issueDetail.custom_fields = issueDetailTransferData.custom_fields;
			}
			initData();
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2)
	{
		if (arg1 == RESULT_OK)
		{
			if (arg0 == RESULT_TAKEPHOTO && photoPathUri != null)
			{
				String[] arrItems = new String[]{"1024*768", "原图"};
				final String photoPath = photoPathUri.getPath();
				new AlertDialog.Builder(this)
						.setItems(arrItems, new DialogInterface.OnClickListener()
						{

							public void onClick(DialogInterface dialog, int which)
							{
								switch (which)
								{
									case 0:
										final String name = TimeUtil.getDateTime(TimeUtil.getTimeNow(), TimeUtil.DEF_DATETIME_SEC) + ".jpg";
										String path = RMConst.PATH_REDMINEIMG + "/" + name;
										BitmapUtil.copyPic(photoPath, path, 768, 1024);//将系统拍摄的图片压缩并copy
										FileUtil.delete(new File(photoPath));//删除系统拍摄的图片
										RMPairStringInfo info = new RMPairStringInfo();
										info.str1 = name;
										info.str2 = path;
										issuePair.lstAttachment.add(info);
										initAttachments(issueDetail);
										break;
									case 1:
										String name1 = photoPath.substring(photoPath.lastIndexOf("/") + 1, photoPath.length());
										RMPairStringInfo info1 = new RMPairStringInfo();
										info1.str1 = name1;
										info1.str2 = photoPath;
										issuePair.lstAttachment.add(info1);
										initAttachments(issueDetail);
										break;
									default:
										break;
								}
							}
						}).show();
			} else if (arg0 == RESULT_SELECTFILE)
			{
				if (arg2 != null)
				{
					Uri uri = arg2.getData();
					if (uri != null)
					{
						String path = FileUtil.getPathFroUri(this, uri);
						RMPairStringInfo info1 = new RMPairStringInfo();
						String name = path.substring(path.lastIndexOf("/") + 1, path.length());
						info1.str1 = name;
						info1.str2 = path;
						issuePair.lstAttachment.add(info1);
						initAttachments(issueDetail);
					}
				}
			}
		}
	}

	@Override
	protected void onActionbarTitleClick()
	{
		back();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			return back();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActionbarExtraClick()
	{
		issuePair.subject = etSubject.getText().toString();
		issuePair.description = etDescription.getText().toString();
		if (StringUtil.isBlank(issuePair.subject))
		{
			FCCrouton.alert(this, "主题不能为空");
			return;
		}

		/*
		 *	如果有附件需要上传，首先上传附件，返回的token接下来需要绑定到问题上面
		 *	如果有附件，上传完附件之后会回调问题保存
		 */
		ArrayList<Token> lstTokens = new ArrayList<Token>();
		uploadAttachment(lstTokens, 0);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (issueDetailTransferData != null)
		{
			outState.putParcelable("issueDetailTransferData", issueDetailTransferData);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rmactyissueadd_project_rl:
				showProjectDlg();
				break;
			case R.id.rmactyissueadd_status_rl:
				showStatusDlg();
				break;
			case R.id.rmactyissueadd_priority_rl:
				showPriorityDlg();
				break;
			case R.id.rmactyissueadd_tracker_rl:
				showTrackersDlg();
				break;
			case R.id.rmactyissueadd_assignto_rl:
				showAssignToDlg();
				break;
			case R.id.rmactyissueadd_version_rl:
				showVersionsDlg();
				break;
			case R.id.rmactyissueadd_category_rl:
				showCategoriesDlg();
				break;
			case R.id.rmactyissueadd_notes_rl:
				showNotesDlg();
				break;
			case R.id.rmactyissueadd_doneratio_rl:
				showDoneRatioDlg();
				break;
			case R.id.rmactyissueadd_startdate_rl:
				showStartDateDlg();
				break;
			case R.id.rmactyissueadd_duedate_rl:
				showDueDateDlg();
				break;
			case R.id.rmactyissueadd_delete:
				deleteToServer();
				break;
			case R.id.rmactyissueadd_attachment:
				addAttachment();
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
			issueDetailTransferData = savedInstanceState.getParcelable("issueDetailTransferData");
		}
		if (issueDetailTransferData == null)
		{
			issueDetailTransferData = getIntent().getParcelableExtra("issueDetailTransferData");
		}
		if (issueDetailTransferData.issueId > 0)
		{
			setContentView(R.layout.rmacty_issueedit);
			setActionbarTitle("修改问题");
		} else
		{
			setContentView(R.layout.rmacty_issueadd);
			setActionbarTitle("新建问题");
		}
		setActionbarExtraBackground(R.drawable.actionbar_save_selector);
	}

	private void initRes()
	{
		etSubject = (EditText) findViewById(R.id.rmactyissueadd_subject);
		etDescription = (EditText) findViewById(R.id.rmactyissueadd_description);
		tvProject = (TextView) findViewById(R.id.rmactyissueadd_project);
		tvStatus = (TextView) findViewById(R.id.rmactyissueadd_status);
		tvTracker = (TextView) findViewById(R.id.rmactyissueadd_tracker);
		tvAssignto = (TextView) findViewById(R.id.rmactyissueadd_assignto);
		tvPriority = (TextView) findViewById(R.id.rmactyissueadd_priority);
		tvVersion = (TextView) findViewById(R.id.rmactyissueadd_version);
		tvCategory = (TextView) findViewById(R.id.rmactyissueadd_category);
		tvDoneRatio = (TextView) findViewById(R.id.rmactyissueadd_doneratio);
		tvStartDate = (TextView) findViewById(R.id.rmactyissueadd_startdate);
		tvDueDate = (TextView) findViewById(R.id.rmactyissueadd_duedate);
		llAttachment = (LinearLayout) findViewById(R.id.rmactyissueadd_attachment_ll);
		llCustomFields = (LinearLayout) findViewById(R.id.rmactyissueadd_customfields_ll);

		rlTrack = (RelativeLayout) findViewById(R.id.rmactyissueadd_tracker_rl);
		rlPrority = (RelativeLayout) findViewById(R.id.rmactyissueadd_priority_rl);
		rlDoneRatio = (RelativeLayout) findViewById(R.id.rmactyissueadd_doneratio_rl);
		rlStartDate = (RelativeLayout) findViewById(R.id.rmactyissueadd_startdate_rl);
		rlDueDate = (RelativeLayout) findViewById(R.id.rmactyissueadd_duedate_rl);
		rlCategory = (RelativeLayout) findViewById(R.id.rmactyissueadd_category_rl);
		rlFixedVersion = (RelativeLayout) findViewById(R.id.rmactyissueadd_version_rl);

		findViewById(R.id.rmactyissueadd_project_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_status_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_tracker_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_assignto_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_priority_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_version_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_category_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_doneratio_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_startdate_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_duedate_rl).setOnClickListener(this);
		findViewById(R.id.rmactyissueadd_attachment).setOnClickListener(this);
		if (issueDetailTransferData.issueId > 0)
		{
			findViewById(R.id.rmactyissueadd_notes_rl).setOnClickListener(this);;
			findViewById(R.id.rmactyissueadd_delete).setOnClickListener(this);;
			tvNotes = (TextView) findViewById(R.id.rmactyissueadd_notes);
		}
	}

	private void showData()
	{
		if (tvNotes != null)
		{
			tvNotes.setText(issuePair.strNotes);
		}
		tvProject.setText(issuePair.project.name);
		tvStatus.setText(issuePair.status.name);
		tvTracker.setText(issuePair.tracker.name);
		tvAssignto.setText(issuePair.assigned_to.name);
		tvPriority.setText(issuePair.priority.name);
		tvVersion.setText(issuePair.fixed_version.name);
		tvCategory.setText(issuePair.category.name);
		tvDoneRatio.setText(issuePair.doneRatio + "%");
		tvStartDate.setText(issuePair.startDate);
		tvDueDate.setText(issuePair.dueDate);
	}

	private void initData()
	{
		etSubject.setText(issueDetail == null ? null : issueDetail.subject);
		etDescription.setText(issueDetail == null ? null : issueDetail.description);
		initProjectData(issueDetail);
		initStatusData(issueDetail);
		initPriority(issueDetail);
		initTrackers(issueDetail);
		initAssignTo(issueDetail);
		initVersions(issueDetail);
		initCategories(issueDetail);
		initDoneRatio(issueDetail);
		initStartAndDueDate(issueDetail);
		initAttachments(issueDetail);
		intCustomFields(issueDetail);
		showData();
		showItem();
	}

	/*
	 * 从数据库读取根据project显示的条目，设置显示
	 */
	private void showItem()
	{
		RMShowItem item = null;
		if (issuePair.project != null)
		{
			item = RMDBLocal.getInstance().getShowItemByProjectId(issuePair.project.id);
		}

		if (item == null)
		{
			item = new RMShowItem(true);
		}

		rlTrack.setVisibility(item.track ? View.VISIBLE : View.GONE);
		rlPrority.setVisibility(item.priority ? View.VISIBLE : View.GONE);
		rlDoneRatio.setVisibility(item.done_ratio ? View.VISIBLE : View.GONE);
		rlStartDate.setVisibility(item.start_date ? View.VISIBLE : View.GONE);
		rlDueDate.setVisibility(item.due_date ? View.VISIBLE : View.GONE);
		rlCategory.setVisibility(item.category ? View.VISIBLE : View.GONE);
		rlFixedVersion.setVisibility(item.fixed_version ? View.VISIBLE : View.GONE);
	}

	/*
	 * 从服务器获取问题详情
	 */
	private void getIssueDetailFromServer()
	{
		StringBuilder url = new StringBuilder(RMHttpUrl.URL_PRE_ISSUES);
		url.append(issueDetailTransferData.issueId).append(".json?include=attachments,children,relations");
		RMHttpUtil.addCommonSessionHeader();
		RMHttpUtil.client.get(this, url.toString(), new RMHttpResponse(this, this, RMHttpResponse.SUCCESSCODE_200)
		{

			@Override
			public void success(Header[] headers, String response)
			{
				if (headers != null && response.length() > 0)
				{
					try
					{
						Gson gson = new Gson();
						RMIssueReply ps = gson.fromJson(response, RMIssueReply.class);
						//有子项目的项目
						if (ps != null)
						{
							issueDetail = ps.issue;
							initData();
						}
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		});
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

		showItem();
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
	 * 初始化 项目数据
	 * 
	 * @param issue
	 */
	private void initProjectData(RMIssueDetail issue)
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
		if (issue == null || issue.project == null || issue.project.id == 0)
		{
			issuePair.project.id = 0;
			issuePair.project.name = null;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstProject, issue.project.id);
			issuePair.project.id = issue.project.id;
			issuePair.project.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstProject);
	}

	/*
	 * 初始化状态
	 * 
	 * @param filter
	 */
	private void initStatusData(RMIssueDetail issue)
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
		if (issue == null || issue.status == null)
		{
			issuePair.status.id = defaultInfo == null ? 0 : defaultInfo.id;
			issuePair.status.name = defaultInfo == null ? null : defaultInfo.name;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstIssueStatus, issue.status.id);
			issuePair.status.id = issue.status.id;
			issuePair.status.name = info == null ? null : info.name;
		}
	}

	/*
	 * 初始化优先级
	 * 
	 * @param filter
	 */
	private void initPriority(RMIssueDetail issue)
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
		if (issue == null || issue.priority == null)
		{
			issuePair.priority.id = defaultInfo == null ? 0 : defaultInfo.id;
			issuePair.priority.name = defaultInfo == null ? null : defaultInfo.name;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstPriority, issue.priority.id);
			issuePair.priority.id = issue.priority.id;
			issuePair.priority.name = info == null ? null : info.name;
		}
	}

	/*
	 * 初始化跟踪
	 * 
	 * @param filter
	 */
	private void initTrackers(RMIssueDetail issue)
	{
		lstTrackers.clear();
		RMDBLocal.getInstance().getTrackersByProjectId(lstTrackers, issuePair.project.id);
		if (issue == null || issue.tracker == null)
		{
			if (lstTrackers.size() == 0)
			{
				addDefaultInfo(lstTrackers);
			}
			issuePair.tracker.id = lstTrackers.get(0).id;
			issuePair.tracker.name = lstTrackers.get(0).name;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstTrackers, issue.tracker.id);
			issuePair.tracker.id = issue.tracker.id;
			issuePair.tracker.name = info == null ? null : info.name;
		}
	}

	/*
	 * 初始化指派给
	 * 
	 * @param filter
	 */
	private void initAssignTo(RMIssueDetail issue)
	{
		lstAssignTo.clear();
		RMDBLocal.getInstance().getStaffsByProjectId(lstAssignTo, issuePair.project.id);
		if (issue == null || issue.assigned_to == null)
		{
			issuePair.assigned_to.id = 0;
			issuePair.assigned_to.name = null;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstAssignTo, issue.assigned_to.id);
			issuePair.assigned_to.id = issue.assigned_to.id;
			issuePair.assigned_to.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstAssignTo);
	}

	/*
	 * 初始化版本
	 * 
	 * @param filter
	 */
	private void initVersions(RMIssueDetail issue)
	{
		lstVersions.clear();
		RMDBLocal.getInstance().getVersionsByProjectId(lstVersions, issuePair.project.id);
		if (issue == null || issue.fixed_version == null)
		{
			issuePair.fixed_version.id = 0;
			issuePair.fixed_version.name = null;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstVersions, issue.fixed_version.id);
			issuePair.fixed_version.id = issue.fixed_version.id;
			issuePair.fixed_version.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstVersions);
	}

	/*
	 * 初始化类别
	 * 
	 * @param filter
	 */
	private void initCategories(RMIssueDetail issue)
	{
		lstCategories.clear();
		if (issuePair.project.id > 0)
		{
			RMDBLocal.getInstance().getIssueCategorysByProjectId(lstCategories, issuePair.project.id);
		}
		if (issue == null || issue.category == null)
		{
			issuePair.category.id = 0;
			issuePair.category.name = null;
		} else
		{
			RMPairBoolInfo info = RMBiz.getPairInfoById(lstCategories, issue.category.id);
			issuePair.category.id = issue.category.id;
			issuePair.category.name = info == null ? null : info.name;
		}
		addDefaultInfo(lstCategories);
	}

	/*
	 * 初始化完成百分比
	 * 
	 * @param filter
	 */
	private void initDoneRatio(RMIssueDetail issue)
	{
		lstDoneRatio.clear();
		issuePair.doneRatio = issue == null ? 0 : issue.done_ratio;
		for (int i = 0; i <= 10; i++)
		{
			RMPairBoolInfo info = new RMPairBoolInfo();
			info.id = (i * 10);
			info.name = (i * 10) + "%";
			lstDoneRatio.add(info);
		}
	}

	/*
	 * 初始化附件列表
	 * 
	 * @param filter
	 */
	private void initAttachments(RMIssueDetail issue)
	{
		llAttachment.removeAllViews();
		int textSizeSmall = (int) getResources().getDimension(R.dimen.textsize_small);
		AttachMentClickListener listener = new AttachMentClickListener();

		/*服务器上已上传的文件*/
		if (issue != null)
		{
			ArrayList<Attachment> attachments = issue.attachments;
			if (attachments != null && attachments.size() > 0)
			{
				for (int i = 0; i < attachments.size(); i++)
				{
					Attachment attachment = attachments.get(i);
					TextView tv = new AppCompatTextView(this);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
					tv.setBackgroundResource(R.drawable.common_listitem_cango_tc);
					tv.setGravity(Gravity.CENTER_VERTICAL);
					String availMemStr = Formatter.formatFileSize(this, attachment.filesize);
					tv.setId(200 + i);
					tv.setTag(attachment);
					tv.setText("附件     " + attachment.filename + "   " + availMemStr);
					tv.setOnClickListener(listener);
					llAttachment.addView(tv);
				}
			}
		}

		/*显示本地刚添加的文件*/
		int size = issuePair.lstAttachment.size();
		for (int i = 0; i < size; i++)
		{
			String fileName = issuePair.lstAttachment.get(i).str1;
			TextView tv = new AppCompatTextView(this);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
			tv.setBackgroundResource(R.drawable.common_listitem_cango_tc);
			tv.setTextColor(Color.BLUE);
			tv.setGravity(Gravity.CENTER_VERTICAL);
			tv.setId(300 + i);
			tv.setTag(issuePair.lstAttachment.get(i));
			tv.setText("附件              " + fileName);
			tv.setOnClickListener(listener);
			llAttachment.addView(tv);
		}
	}

	/*
	 * 初始化 自定义字段
	 * 
	 * @param issue
	 */
	private void intCustomFields(RMIssueDetail issue)
	{
		llCustomFields.removeAllViews();
		if (issue != null)
		{
			if (issue.custom_fields != null && issue.custom_fields.size() > 0)
			{
				llCustomFields.setVisibility(View.VISIBLE);
				for (int i = 0; i < issue.custom_fields.size(); i++)
				{
					RMIssueDetail.CustomFields child = issue.custom_fields.get(i);
					if (child != null)
					{
						View view = getLayoutInflater().inflate(R.layout.rmissueaddedit_customfile_item, null);
						TextView tvName = (TextView) view.findViewById(R.id.tv_name);
						TextView tvValue = (TextView) view.findViewById(R.id.tv_value);
						tvName.setText(child.name);
						tvValue.setText(child.value);
						llCustomFields.addView(view);
					}
				}
			} else
			{
				llCustomFields.setVisibility(View.GONE);
			}
		}
	}

	/*
	 * 初始化 开始日期和完成日期 
	 * 
	 * @param issue
	 */
	private void initStartAndDueDate(RMIssueDetail issue)
	{
		if (issue != null && issue.start_date != null)
		{
			issuePair.startDate = issue.start_date;
		} else
		{
			issuePair.startDate = CalendarUtil.getDate();
		}

		if (issue != null && issue.due_date != null)
		{
			issuePair.dueDate = issue.due_date;
		} else
		{
			issuePair.dueDate = null;
		}
	}

	/*
	 * 弹出 项目选择  对话框
	 */
	private void showProjectDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "项目", lstProject, issuePair.project.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				issuePair.project.id = lstProject.get(iData).id;
				issuePair.project.name = lstProject.get(iData).name;
				onProjectChange();
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 弹出 优先级选择  对话框
	 */
	private void showPriorityDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "优先级", lstPriority, issuePair.priority.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				issuePair.priority.id = lstPriority.get(iData).id;
				issuePair.priority.name = lstPriority.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 弹出 状态选择  对话框
	 */
	private void showStatusDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "问题状态", lstIssueStatus, issuePair.status.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				issuePair.status.id = lstIssueStatus.get(iData).id;
				issuePair.status.name = lstIssueStatus.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 弹出 跟踪选择  对话框
	 */
	private void showTrackersDlg()
	{
		if (issuePair.project.id <= 0)
		{
			FCCrouton.alert(this, "请先选择[项目]");
			return;
		}
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "跟踪状态", lstTrackers, issuePair.tracker.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				issuePair.tracker.id = lstTrackers.get(iData).id;
				issuePair.tracker.name = lstTrackers.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 弹出 指派给选择  对话框
	 */
	private void showAssignToDlg()
	{
		if (issuePair.project.id <= 0)
		{
			FCCrouton.alert(this, "请先选择[项目]");
			return;
		}
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "指派人", lstAssignTo, issuePair.assigned_to.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				issuePair.assigned_to.id = lstAssignTo.get(iData).id;
				issuePair.assigned_to.name = lstAssignTo.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 弹出 版本选择  对话框
	 */
	private void showVersionsDlg()
	{
		if (issuePair.project.id <= 0)
		{
			FCCrouton.alert(this, "请先选择[项目]");
			return;
		}
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "版本", lstVersions, issuePair.fixed_version.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				issuePair.fixed_version.id = lstVersions.get(iData).id;
				issuePair.fixed_version.name = lstVersions.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 弹出 类别选择  对话框
	 */
	private void showCategoriesDlg()
	{
		if (issuePair.project.id <= 0)
		{
			FCCrouton.alert(this, "请先选择[项目]");
			return;
		}
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "类别", lstCategories, issuePair.category.id);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				issuePair.category.id = lstCategories.get(iData).id;
				issuePair.category.name = lstCategories.get(iData).name;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 弹出 注释输入  对话框
	 */
	private void showNotesDlg()
	{
		FCDlgEditOne dlg = new FCDlgEditOne(this, "请输入操作注释", issuePair.strNotes);
		dlg.addSubmitListener(new FCParamSubmitInterface<String>()
		{

			@Override
			public boolean submit(String strData)
			{
				issuePair.strNotes = strData;
				showData();
				return true;
			}
		});
		dlg.show();
		DeviceUtil.showSoftKeyBoardDelay(this, dlg.getEditText());
	}

	/*
	 * 弹出 百分比选择  对话框
	 */
	private void showDoneRatioDlg()
	{
		RMDlgRadiogroup dlg = new RMDlgRadiogroup(this, "完成", lstDoneRatio, issuePair.doneRatio);
		dlg.addSubmitListener(new FCParamSubmitInterface<Integer>()
		{

			@Override
			public boolean submit(Integer iData)
			{
				issuePair.doneRatio = lstDoneRatio.get(iData).id;
				showData();
				return true;
			}
		});
		dlg.show();
	}

	/*
	 * 弹出 开始日期选择  对话框
	 */
	private void showStartDateDlg()
	{
		Calendar cal = Calendar.getInstance();
		Date date = CalendarUtil.parse(issuePair.startDate, CalendarUtil.DEF_DATE_FORMAT);
		if (date != null)
		{
			cal.setTime(date);
		}
		final FCDatePickerDialog datePickerDialog = new FCDatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
		{

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
			{
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, monthOfYear, dayOfMonth);
				issuePair.startDate = CalendarUtil.getDateTime(calendar.getTime(), CalendarUtil.DEF_DATE_FORMAT);
				showData();
			}
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				datePickerDialog.cancel();
			}
		});
		datePickerDialog.show();
	}

	/*
	 * 弹出 结束日期选择  对话框
	 */
	private void showDueDateDlg()
	{
		Calendar cal = Calendar.getInstance();
		if (issuePair.dueDate != null)
		{
			Date date = CalendarUtil.parse(issuePair.dueDate, CalendarUtil.DEF_DATE_FORMAT);
			if (date != null)
			{
				cal.setTime(date);
			}
		}
		final FCDatePickerDialog datePickerDialog = new FCDatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
		{

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
			{
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, monthOfYear, dayOfMonth);
				issuePair.dueDate = CalendarUtil.getDateTime(calendar.getTime(), CalendarUtil.DEF_DATE_FORMAT);
				showData();
			}
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				datePickerDialog.cancel();
			}
		});
		datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "清除", new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				issuePair.dueDate = null;
				showData();
			}
		});
		datePickerDialog.show();
	}

	/**
	 * 添加或者编辑问题
	 * 
	 * @param lstTokens			此次上传附件返回的token 的列表
	 */
	private void saveToServer(ArrayList<Token> lstTokens)
	{
		RMIssuePost issuePost = new RMIssuePost();
		issuePost.project_id = issuePair.project.id;
		issuePost.tracker_id = issuePair.tracker.id;
		issuePost.status_id = issuePair.status.id;
		issuePost.priority_id = issuePair.priority.id;
		issuePost.subject = issuePair.subject;
		issuePost.description = issuePair.description;
		issuePost.category_id = issuePair.category.id;
		issuePost.fixed_version_id = issuePair.fixed_version.id;
		issuePost.assigned_to_id = issuePair.assigned_to.id;
		issuePost.done_ratio = issuePair.doneRatio;
		issuePost.start_date = issuePair.startDate;
		issuePost.due_date = issuePair.dueDate;
		issuePost.notes = issuePair.strNotes;
		issuePost.uploads = lstTokens;
		issuePost.custom_fields = issueDetail == null ? null : issueDetail.custom_fields;
		RMIssuePost.RMIssuePostApply apply = new RMIssuePost.RMIssuePostApply();
		apply.issue = issuePost;
		HttpEntity entity = RMHttpUtil.getStringEntity(apply);
		if (issueDetailTransferData.issueId > 0)
		{
			String url = RMHttpUrl.URL_PRE_ISSUES + issueDetailTransferData.issueId + ".json";
			RMHttpUtil.addCommonSessionHeader();
			RMHttpUtil.client.put(this, url, entity, RMHttpUtil.CONTENTTYPE_JSON, new RMHttpResponse(this, this,
					RMHttpResponse.SUCCESSCODE_200)
			{

				@Override
				public void success(Header[] headers, String response)
				{
					RMIssueAddEditActy.this.setResult(RESULT_OK);
					RMIssueAddEditActy.this.finish();
				}
			});
		} else
		{
			RMHttpUtil.addCommonSessionHeader();
			RMHttpUtil.client.post(this, RMHttpUrl.URL_ISSUES, entity, RMHttpUtil.CONTENTTYPE_JSON, new RMHttpResponse(
					this, this,
					RMHttpResponse.SUCCESSCODE_201)
			{

				@Override
				public void success(Header[] headers, String response)
				{
					RMIssueAddEditActy.this.setResult(RESULT_OK);
					RMIssueAddEditActy.this.finish();
				}
			});
		}
	}

	//上传附件
	private void uploadAttachment(final ArrayList<Token> lstTokens, final int index)
	{
		if (index < issuePair.lstAttachment.size())
		{
			RMPairStringInfo info = issuePair.lstAttachment.get(index);
			final String name = info.str1;
			final String path = info.str2;
			final String contentType = "application/octet-stream";
			File file = new File(path);
			if (file.exists())
			{
				HttpEntity entity = new FileEntity(file, contentType);
				RMHttpUtil.addCommonSessionHeader();
				RMHttpUtil.client.post(this, RMHttpUrl.URL_UPLOADS, entity, contentType, new RMHttpResponse(this, null,
						RMHttpResponse.SUCCESSCODE_201)
				{

					@Override
					public void success(Header[] headers, String response)
					{
						try
						{
							Gson gson = new Gson();
							RMUploadReply ps = gson.fromJson(response, RMUploadReply.class);
							if (ps != null)
							{
								Token token = ps.upload;
								token.filename = name;
								token.content_type = "application/*";
								lstTokens.add(token);
							}
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}

					@Override
					public void failed(int statusCode, String failedReason)
					{
						FCToast.ToastShow(failedReason);
					}

					@Override
					public void onFinish()
					{
						super.onFinish();
						uploadAttachment(lstTokens, index + 1);
					}
				});
			} else
			{
				FCCrouton.error(this, "找不到附件 " + issuePair.lstAttachment.get(index).str1);
				uploadAttachment(lstTokens, index + 1);
			}
		} else
		{
			saveToServer(lstTokens);
		}
	}

	/*
	 * 删除问题
	 */
	private void deleteToServer()
	{
		if (issueDetailTransferData.issueId > 0)
		{
			FCDlgComm dlg = new FCDlgComm(this, "确定要删除本问题么？");
			dlg.addSubmitListener(new FCSubmitInterface()
			{

				@Override
				public boolean submit()
				{
					String url = RMHttpUrl.URL_PRE_ISSUES + issueDetailTransferData.issueId + ".json";
					RMHttpUtil.addCommonSessionHeader();
					RMHttpUtil.client.delete(RMIssueAddEditActy.this, url, new RMHttpResponse(RMIssueAddEditActy.this,
							RMIssueAddEditActy.this,
							RMHttpResponse.SUCCESSCODE_200)
					{

						@Override
						public void success(Header[] headers, String response)
						{
							RMIssueAddEditActy.this.setResult(RESULT_OK, new Intent("xx"));
							RMIssueAddEditActy.this.finish();
						}
					});
					return true;
				}
			});
			dlg.show();
		}
	}

	/*
	 * 添加附件
	 */
	private void addAttachment()
	{
		String[] arrItems = new String[]{"拍照", "本地文件"};
		new AlertDialog.Builder(this)
				.setItems(arrItems, new DialogInterface.OnClickListener()
				{

					public void onClick(DialogInterface dialog, int which)
					{
						switch (which)
						{
							case 0:
								processPhoto(RESULT_TAKEPHOTO);
								break;
							case 1:
								FileOpenUtil.selectFile(RMIssueAddEditActy.this, "*/*", RESULT_SELECTFILE);
								break;
							default:
								break;
						}
					}
				}).show();
	}

	/*
	 * 打开拍照页面
	 */
	private void processPhoto(final int requestCode)
	{
		if (DeviceUtil.isBackCameraCanUse())
		{	//图片路径，没有则创建
			File dir = new File(RMConst.PATH_REDMINEIMG);
			FileUtil.checkFilePath(dir, true);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String name = TimeUtil.getDateTime(TimeUtil.getTimeNow(), TimeUtil.DEF_ONLYTIME_FORMAT) + ".jpg";
			File f = new File(dir, name);
			photoPathUri = Uri.fromFile(f);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPathUri);
			startActivityForResult(intent, requestCode);
		} else if (DeviceUtil.isFrontCameraCanUse())
		{
			File dir = new File(RMConst.PATH_REDMINEIMG);
			FileUtil.checkFilePath(dir, true);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			String name = TimeUtil.getDateTime(TimeUtil.getTimeNow(), TimeUtil.DEF_ONLYTIME_FORMAT) + ".jpg";
			File f = new File(dir, name);
			photoPathUri = Uri.fromFile(f);
			intent.putExtra("camerasensortype", 2); // 调用前置摄像头  
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPathUri);
			startActivityForResult(intent, requestCode);
		} else
		{
			FCCrouton.error(this, "没有发现可用摄像头");
		}
	}

	/*
	 * 回退操作
	 */
	private boolean back()
	{
		FCDlgComm dlg = new FCDlgComm(this, "已填写的内容不会保存，是否确定");
		dlg.addSubmitListener(new FCSubmitInterface()
		{

			@Override
			public boolean submit()
			{
				RMIssueAddEditActy.this.finish();
				return true;
			}
		});
		dlg.show();
		return true;
	}

	/*
	 * 附件的点击监听器 
	 */
	private class AttachMentClickListener implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			if (v.getId() >= 200 && v.getId() < 300)
			{
				if (v.getTag() != null && v.getTag() instanceof Attachment)
				{
					Attachment attach = (Attachment) (v.getTag());
					RMIssueViewActy.openAttachment(RMIssueAddEditActy.this, attach.content_url, attach.filename, attach.filesize);
				}
			} else if (v.getId() >= 300 && v.getId() < 400)
			{
				if (v.getTag() != null && v.getTag() instanceof RMPairStringInfo)
				{
					RMPairStringInfo info = (RMPairStringInfo) (v.getTag());
					File file = new File(info.str2);
					if (file.exists())
					{
						FileOpenUtil.openFile(RMIssueAddEditActy.this, file);
					}
				}
			}
		}
	}

	/*
	 * 问题详情 显示 实体
	 */
	private class RMIssuePair
	{

		public String						subject;
		public String						description;
		public RMPairInfo					project			= new RMPairInfo();
		public RMPairInfo					status			= new RMPairInfo();
		public RMPairInfo					tracker			= new RMPairInfo();
		public RMPairInfo					assigned_to		= new RMPairInfo();
		public RMPairInfo					priority		= new RMPairInfo();
		public RMPairInfo					fixed_version	= new RMPairInfo();
		public RMPairInfo					category		= new RMPairInfo();
		public String						startDate;
		public String						dueDate;
		public int							doneRatio;
		public String						strNotes;
		public ArrayList<RMPairStringInfo>	lstAttachment	= new ArrayList<RMPairStringInfo>();
	}

}
