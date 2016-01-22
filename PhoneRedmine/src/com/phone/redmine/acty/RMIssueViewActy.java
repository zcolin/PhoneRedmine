package com.phone.redmine.acty;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import org.apache.http.Header;
import org.apache.http.conn.HttpHostConnectException;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import com.google.gson.Gson;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.phone.redmine.R;
import com.phone.redmine.app.App;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.RMIssueDetail;
import com.phone.redmine.entity.RMIssueDetail.Journal;
import com.phone.redmine.entity.RMIssueDetailTransferData;
import com.phone.redmine.http.RMHttpResponse;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.http.RMHttpUtil;
import com.phone.redmine.http.entity.RMIssueReply;
import com.phone.redmine.util.CalendarUtil;
import com.phone.redmine.util.FileOpenUtil;
import com.phone.redmine.util.FileUtil;
import com.phone.redmine.util.LogUtil;
import com.phone.redmine.util.NetworkUtil;
import com.phone.redmine.util.StringUtil;
import com.phone.redmine.view.FCDlg.FCSubmitInterface;
import com.phone.redmine.view.FCDlgComm;
import com.phone.redmine.view.FCProgressbar;
import com.phone.redmine.view.crouton.FCCrouton;

/**
 * RM 问题查看页面 
 */
public class RMIssueViewActy extends BaseSecLevelActy implements OnClickListener
{

	/**
	 * 进入 问题添加修改  页面的标识
	 */
	private static final int	RESULT_EDITISSUEACTY	= 10;

	private int					issueId					= -1;
	private RMIssueDetail		issueDetail;
	private int					textSizeSmall;
	private TextView			tvIssueId;
	private TextView			tvTracker;
	private TextView			tvTitleTime;
	private TextView			tvSubject;
	private TextView			tvDescription;
	private TextView			tvProject;
	private TextView			tvStatus;
	private TextView			tvAssignTo;
	private TextView			tvDoneRatio;
	private TextView			tvPriority;
	private TextView			tvStartDate;
	private TextView			tvDueDate;
	private TextView			tvVersion;
	private TextView			tvAuthor;
	private TextView			tvCreatedOn;
	private TextView			tvUpdateOn;
	private TextView			tvCategory;
	private LinearLayout		llChildren;
	private LinearLayout		llCustomFields;
	private LinearLayout		llAttachment;
	private LinearLayout		llHistory;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmacty_issueview);
		setActionbarTitle("问题详情");
		setActionbarExtraBackground(R.drawable.actionbar_edit_selector);
		if (savedInstanceState != null)
		{
			issueId = savedInstanceState.getInt("issueId");
		}
		if (issueId == -1)
		{
			issueId = getIntent().getIntExtra("issueId", 0);
		}
		initRes();
		getIssueDetailFromServer();

		RMDBLocal.getInstance().deleteUpdatedIssueId(issueId);//消除小红点
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2)
	{
		if (arg1 == RESULT_OK)
		{
			if (arg2 != null)
			{
				this.setResult(RESULT_OK);
				this.finish();
			} else
			{
				getIssueDetailFromServer();
			}
		}
	}

	@Override
	protected void onActionbarExtraClick()
	{
		RMIssueDetailTransferData data = new RMIssueDetailTransferData();
		data.issueId = issueId;

		Intent intent = new Intent();
		intent.putExtra("issueDetailTransferData", data);
		intent.setClass(this, RMIssueAddEditActy.class);
		startActivityForResult(intent, RESULT_EDITISSUEACTY);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (issueId > 0)
		{
			outState.putInt("issueId", issueId);
		}
	}

	@Override
	public void onClick(View v)
	{
		int vId = v.getId();
		//子任务
		if (vId >= 100 && vId < 200)
		{
			Intent intent = new Intent();
			intent.putExtra("issueId", issueDetail.children.get(vId - 100).id);
			intent.setClass(this, RMIssueViewActy.class);
			startActivity(intent);
		}
		//附件
		else if (vId >= 200 && vId < 300)
		{
			RMIssueDetail.Attachment attach = issueDetail.attachments.get(vId - 200);
			openAttachment(this, attach.content_url, attach.filename, attach.filesize);
		}
	}

	private void initRes()
	{
		tvIssueId = (TextView) findViewById(R.id.rmactyissuedetail_title_id);
		tvTracker = (TextView) findViewById(R.id.rmactyissuedetail_title_tracker);
		tvTitleTime = (TextView) findViewById(R.id.rmactyissuedetail_title_time);
		tvSubject = (TextView) findViewById(R.id.rmactyissuedetail_subject);
		tvDescription = (TextView) findViewById(R.id.rmactyissuedetail_description);
		tvProject = (TextView) findViewById(R.id.rmactyissuedetail_content_project);
		tvStatus = (TextView) findViewById(R.id.rmactyissuedetail_content_status);
		tvAssignTo = (TextView) findViewById(R.id.rmactyissuedetail_content_assignto);
		tvDoneRatio = (TextView) findViewById(R.id.rmactyissuedetail_content_doneratio);
		tvPriority = (TextView) findViewById(R.id.rmactyissuedetail_content_priority);
		tvStartDate = (TextView) findViewById(R.id.rmactyissuedetail_content_startdate);
		tvDueDate = (TextView) findViewById(R.id.rmactyissuedetail_content_duedate);
		tvVersion = (TextView) findViewById(R.id.rmactyissuedetail_content_version);
		tvAuthor = (TextView) findViewById(R.id.rmactyissuedetail_content_author);
		tvCreatedOn = (TextView) findViewById(R.id.rmactyissuedetail_content_createdon);
		tvUpdateOn = (TextView) findViewById(R.id.rmactyissuedetail_content_updateon);
		tvCategory = (TextView) findViewById(R.id.rmactyissuedetail_content_category);
		llChildren = (LinearLayout) findViewById(R.id.rmactyissuedetail_children_ll);
		llCustomFields = (LinearLayout) findViewById(R.id.rmactyissuedetail_customfields_ll);
		llAttachment = (LinearLayout) findViewById(R.id.rmactyissuedetail_attachment_ll);
		llHistory = (LinearLayout) findViewById(R.id.rmactyissuedetail_history_ll);
	}

	private void initData()
	{
		textSizeSmall = (int) getResources().getDimension(R.dimen.textsize_small);
		if (null != issueDetail)
		{
			String trackerName = null;
			if (issueDetail.tracker != null)
			{
				trackerName = issueDetail.tracker == null ? null : issueDetail.tracker.name;
				tvTracker.setBackgroundColor(RMConst.TRACKER_DEFAULT_COLORS[issueDetail.tracker.id]);
			}
			String authorName = issueDetail.author == null ? null : issueDetail.author.name;
			Date date = CalendarUtil.parseUTC(issueDetail.created_on, CalendarUtil.DEF_UTC_FORMAT);
			tvIssueId.setText("#" + issueDetail.id);
			tvTracker.setText(trackerName);
			tvTitleTime.setText(authorName + " " + CalendarUtil.diffNow(date) + "前添加");
			tvSubject.setText(issueDetail.subject);
			tvDescription.setText(issueDetail.description);
			tvProject.setText(issueDetail.project == null ? null : issueDetail.project.name);
			tvStatus.setText(issueDetail.status == null ? null : issueDetail.status.name);
			tvPriority.setText(issueDetail.priority == null ? null : issueDetail.priority.name);
			tvAssignTo.setText(issueDetail.assigned_to == null ? null : issueDetail.assigned_to.name);
			tvDoneRatio.setText(issueDetail.done_ratio + "%");
			tvStartDate.setText(issueDetail.start_date);
			tvDueDate.setText(issueDetail.due_date);
			tvVersion.setText(issueDetail.fixed_version == null ? null : issueDetail.fixed_version.name);
			tvAuthor.setText(authorName);
			tvCreatedOn.setText(CalendarUtil.getDateTime(CalendarUtil.parseUTC(issueDetail.created_on, CalendarUtil.DEF_UTC_FORMAT), CalendarUtil.DEF_DATETIME_FORMAT));
			tvUpdateOn.setText(CalendarUtil.getDateTime(CalendarUtil.parseUTC(issueDetail.updated_on, CalendarUtil.DEF_UTC_FORMAT), CalendarUtil.DEF_DATETIME_FORMAT));
			tvCategory.setText(issueDetail.category == null ? null : issueDetail.category.name);
			viewChildren();
			viewCustomFields();
			viewAttachment();
			viewHistoryStr();
		}
	}

	/*
	 * 显示自定义字段 
	 */
	private void viewCustomFields()
	{
		llCustomFields.removeAllViews();
		if (issueDetail.custom_fields != null && issueDetail.custom_fields.size() > 0)
		{
			llCustomFields.setVisibility(View.VISIBLE);
			for (int i = 0; i < issueDetail.custom_fields.size(); i++)
			{
				RMIssueDetail.CustomFields child = issueDetail.custom_fields.get(i);
				if (child != null)
				{
					View view = getLayoutInflater().inflate(R.layout.rmissueview_customfile_item, null);
					TextView tvName = (TextView) view.findViewById(R.id.rmissueviewcustomfileitem_name);
					TextView tvValue = (TextView) view.findViewById(R.id.rmissueviewcustomfileitem_value);
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

	/*
	 * 显示子任务
	 */
	private void viewChildren()
	{
		llChildren.removeAllViews();
		if (issueDetail.children != null && issueDetail.children.size() > 0)
		{
			llChildren.setVisibility(View.VISIBLE);
			for (int i = 0; i < issueDetail.children.size(); i++)
			{
				RMIssueDetail.Children child = issueDetail.children.get(i);
				if (child != null)
				{
					TextView tv = new AppCompatTextView(this);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
					tv.setBackgroundResource(R.drawable.common_listitem_cango_tc);
					tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
					tv.setId(100 + i);
					tv.setText("子任务              #" + child.id);
					tv.setOnClickListener(this);
					llChildren.addView(tv);
				}
			}
		} else
		{
			llChildren.setVisibility(View.GONE);
		}
	}

	/*
	 * 显示附件 
	 */
	private void viewAttachment()
	{
		llAttachment.removeAllViews();
		if (issueDetail.attachments != null && issueDetail.attachments.size() > 0)
		{
			llAttachment.setVisibility(View.VISIBLE);
			for (int i = 0; i < issueDetail.attachments.size(); i++)
			{
				RMIssueDetail.Attachment attachment = issueDetail.attachments.get(i);
				if (attachment != null)
				{
					TextView tv = new AppCompatTextView(this);
					tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
					tv.setBackgroundResource(R.drawable.common_listitem_cango_tc);
					tv.setGravity(Gravity.CENTER_VERTICAL);
					String availMemStr = Formatter.formatFileSize(this, attachment.filesize);
					tv.setId(200 + i);
					tv.setText("附件     " + attachment.filename + "   " + availMemStr);
					tv.setOnClickListener(this);
					llAttachment.addView(tv);
				}
			}
		} else
		{
			llAttachment.setVisibility(View.GONE);
		}
	}

	/*
	 * 显示历史操作列表
	 */
	private void viewHistoryStr()
	{
		llHistory.removeAllViews();
		ArrayList<RMIssueDetail.Journal> lstJournal = issueDetail.journals;
		if (lstJournal != null && lstJournal.size() > 0)
		{
			llHistory.setVisibility(View.VISIBLE);
			int titleColor = getResources().getColor(R.color.gray_light);
			LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			layoutParams.topMargin = 30;
			for (int i = 0; i < lstJournal.size(); i++)
			{
				Journal journal = lstJournal.get(i);
				if (journal != null)
				{
					String userName = journal.user == null ? null : journal.user.name;
					String createOn = CalendarUtil.getDateTime(CalendarUtil.parseUTC(journal.created_on, CalendarUtil.DEF_UTC_FORMAT), CalendarUtil.DEF_DATETIME_FORMAT);
					TextView tvJournal = new AppCompatTextView(this);
					tvJournal.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSmall);
					tvJournal.setBackgroundColor(titleColor);
					tvJournal.setTextIsSelectable(true);
					tvJournal.setText(createOn + "          " + userName);
					StringBuilder builder = new StringBuilder();
					TextView tvDetail = new AppCompatTextView(this);
					tvDetail.setBackgroundColor(Color.WHITE);
					tvDetail.setTextIsSelectable(true);
					if (journal.details != null)
					{
						for (int j = 0; j < journal.details.size(); j++)
						{
							RMIssueDetail.JournalDetail detail = journal.details.get(j);
							if (StringUtil.isNotBlank(detail.name))
							{
								String detailName = RMIssueDetail.ATTACHMENT.equals(detail.property) ? RMIssueDetail.ATTACHMENT : detail.name;
								String name = RMIssueDetail.ATTACHMENT.equals(detail.property) ? "附件" : RMConst.ISSUENAME.get(detail.name);
								builder.append("*")
										.append(name)
										.append(RMIssueDetail.getStringArrBykeyAndId(detailName, detail.old_value, detail.new_value));
								if (j < journal.details.size() - 1)
								{
									builder.append("\n");
								}
							}
						}
						if (StringUtil.isNotEmpty(journal.notes))
						{
							builder.append("\n");
							builder.append(journal.notes);
						}
					}
					tvDetail.setText(builder);
					llHistory.addView(tvJournal, layoutParams);
					llHistory.addView(tvDetail);
				}
			}
		} else
		{
			llHistory.setVisibility(View.GONE);
		}
	}

	/*
	 * 获取问题详情
	 */
	private void getIssueDetailFromServer()
	{
		StringBuilder url = new StringBuilder(RMHttpUrl.URL_PRE_ISSUES);
		url.append(issueId).append(".json?include=attachments,journals,children,relations");
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

	/**
	 * 打开附件
	 * 
	 * @param acty			Activity实体
	 * @param url			URL	
	 * @param fileName		存入本地文件名称
	 * @param fileSize		文件大小(判断文件大小之后调用重载函数)
	 */
	public static void openAttachment(final Activity acty, final String url, String fileName, long fileSize)
	{
		if (fileSize > 5 * 1024 * 1024) //5M
		{
			openAttachment(acty, url, fileName, Formatter.formatFileSize(acty, fileSize));
		} else
		{
			openAttachment(acty, url, fileName, "");
		}
	}

	/**
	 * 打开附件
	 * 
	 * @param acty			Activity实体
	 * @param url			URL	
	 * @param fileName		存入本地文件名称
	 * @param fileSize		文件大小
	 */
	public static void openAttachment(final Activity acty, final String url, String fileName, final String fileSize)
	{
		final File file = new File(RMConst.PATH_REDMINEATTACHMENT, fileName);
		if (!file.exists())
		{
			if (NetworkUtil.isNetworkAvailable(acty))
			{
				if (StringUtil.isNotEmpty(fileSize))
				{
					String str = NetworkUtil.isWifiConnect(acty) ? "当前网络为WIFI网络，" : "当前操作会消耗手机数据流量，";
					FCDlgComm dlg = new FCDlgComm(acty, str + "\n文件大小为" + fileSize + ",\n确认要打开么?");
					dlg.addSubmitListener(new FCSubmitInterface()
					{

						@Override
						public boolean submit()
						{
							getAttachmentFromServer(acty, url, file, fileSize);
							return true;
						}
					});
					dlg.show();
				} else
				{
					getAttachmentFromServer(acty, url, file, fileSize);
				}
			} else
			{
				FCCrouton.error(acty, "网络不可用！");
			}
		} else
		{
			try
			{
				FileOpenUtil.openFile(acty, file);
			} catch (Exception e)
			{
				FCCrouton.error(acty, LogUtil.ExceptionToString(e));
			}
		}
	}

	/**
	 * 从服务器获取 附件列表
	 * 
	 * @param acty			Activity实体
	 * @param url			URL	
	 * @param file			存入本地的文件实体
	 */
	public static void getAttachmentFromServer(final Activity acty, String url, File file, final String fileSize)
	{
		final FCProgressbar proBar = new FCProgressbar(acty);
		FileUtil.checkFilePath(file, false);
		RMHttpUtil.addCommonSessionHeader();
		RMHttpUtil.client.get(App.APPCONTEXT, url, new FileAsyncHttpResponseHandler(file)
		{

			@Override
			public void onStart()
			{
				proBar.showBar();
				proBar.setMessage("正在加载文件……");
			}

			@Override
			public void onSuccess(int statusCode, Header[] arg1, File file)
			{
				if (statusCode == RMHttpResponse.SUCCESSCODE_200)
				{
					try
					{
						FileOpenUtil.openFile(acty, file);
					}
					catch (Exception e)
					{
						FCCrouton.error(acty, LogUtil.ExceptionToString(e));
					}
				} else
				{
					FCCrouton.error(acty, "附件下载失败.");
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable e, File response)
			{
				String str;
				if (e != null)
				{
					if (e instanceof HttpHostConnectException || statusCode == 0)
					{
						if (!NetworkUtil.isNetworkAvailable(App.APPCONTEXT))
						{
							str = "当前无网络连接，请开启网络！";
						} else
						{
							str = "连接服务器失败(" + statusCode + "), 请检查网络或稍后重试";
						}
					} else
					{
						str = e.getMessage() + "(" + statusCode + ")";
					}
					FCCrouton.error(acty, str);
				} else
				{
					FCCrouton.error(acty, "附件下载失败");
				}
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize)
			{
				String strLeng = Formatter.formatFileSize(App.APPCONTEXT, bytesWritten);
				proBar.setMessage("正在加载文件..." + strLeng + "/" + fileSize);
			}

			//有时会出现失败onFailure不回调的现象。
			@Override
			public void onFinish()
			{
				proBar.hideBar();
			}
		});
	}

}
