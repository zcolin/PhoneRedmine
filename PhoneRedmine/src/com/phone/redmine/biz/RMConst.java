package com.phone.redmine.biz;

import java.util.HashMap;
import android.graphics.Color;
import android.os.Environment;
import com.phone.redmine.entity.RMIssueDetail;

/**
 * RM Constant数据
 */
public class RMConst
{

	//REDMINE的登录网页的cookie
	public static final String					SHARE_REDMINECOOKIE					= "share_redminecookie";
	//REDMINE USER_PASSWORD加密key
	public static final String					USER_PRIVATE_KEY					= "fc@xe&f*";

	/**
	 * 持久化数据   TOKEN  KEY
	 */
	public static final String					SHARE_APIKEY						= "REDMINEAPIKEY";
	/**
	 * 持久化数据   用户名  KEY
	 */
	public static final String					SHARE_RMUSERNAME					= "REDMINEUSERNAME";
	/**
	 * 持久化数据   用户密码  KEY
	 */
	public static final String					SHARE_RMPASSWORD					= "REDMINERMPASSWORD";
	/**
	 * 持久化数据   用户Id  KEY
	 */
	public static final String					SHARE_RMUSERID						= "REDMINEUSERID";
	/**
	 * 持久化数据  数据库的同步时间 KEY
	 */
	public static final String					SHARE_DBSYNCTIME					= "share_dbsynctime";
	/**
	 * 持久化数据  上次的问题同步时间 KEY
	 */
	public static final String					SHARE_UPDATEISSUE_TIME				= "share_updateissue_time";
	/**
	 * 持久化数据  有更新是否通知  KEY
	 */
	public static final String					SHARE_IS_NOTIFICATION				= "share_is_notification";

	/**
	 * 持久化数据  我的任务_快速问题过滤器_问题状态   KEY
	 */
	public static final String					SHARE_ISSUEFILTER_MY_STATUS			= "share_issuefilter_my_status";
	/**
	 * 持久化数据  我的任务_快速问题过滤器_列   KEY
	 */
	public static final String					SHARE_ISSUEFILTER_MY_CLOUMN			= "share_issuefilter_my_column";
	/**
	 * 持久化数据  我的任务_快速问题过滤器_排序   KEY
	 */
	public static final String					SHARE_ISSUEFILTER_MY_SORT			= "share_issuefilter_my_sort";

	/**
	 * 持久化数据  项目_快速问题过滤器_问题状态   KEY
	 */
	public static final String					SHARE_ISSUEFILTER_PROJECT_STATUS	= "share_issuefilter_project_status";
	/**
	 * 持久化数据  项目_快速问题过滤器_列   KEY
	 */
	public static final String					SHARE_ISSUEFILTER_PROJECT_CLOUMN	= "share_issuefilter_project_column";
	/**
	 * 持久化数据  项目_快速问题过滤器_排序   KEY
	 */
	public static final String					SHARE_ISSUEFILTER_PROJECT_SORT		= "share_issuefilter_project_sort";

	/**
	 * 过滤结果_全部数据   KEY
	 */
	public static final String					ISSUEFILTER_SORT_STATUS_ALL			= "=*";
	/**
	 *过滤结果_已关闭   KEY
	 */
	public static final String					ISSUEFILTER_SORT_STATUS_CLOSED		= "=closed";
	/**
	 * 过滤结果_未关闭   KEY
	 */
	public static final String					ISSUEFILTER_SORT_STATUS_NOTCLOSED	= "!=closed";

	/**
	 * 排序条件_问题Id
	 */
	public static final String					ISSUEFILTER_SORT_CLOUMN_ISSUEID		= "id";
	/**
	 * 排序条件_完成时间
	 */
	public static final String					ISSUEFILTER_SORT_CLOUMN_DUEDATE		= "due_date";
	/**
	 * 排序条件_开始时间
	 */
	public static final String					ISSUEFILTER_SORT_CLOUMN_STARTDATE	= "start_date";
	/**
	 * 排序条件_更新时间
	 */
	public static final String					ISSUEFILTER_SORT_CLOUMN_UPDATEDON	= "updated_on";
	/**
	 * 排序条件_优先级
	 */
	public static final String					ISSUEFILTER_SORT_CLOUMN_PRIORITY	= "priority";
	/**
	 * 排序条件_降序
	 */
	public static final String					ISSUEFILTER_SORT_DESC				= ":desc";
	/**
	 * 排序条件_升序
	 */
	public static final String					ISSUEFILTER_SORT_ASC				= ":asc";

	/**
	 * 过滤结果集合
	 * @see		ISSUEFILTER_SORT_STATUS_NOTCLOSED
	 * @see		ISSUEFILTER_SORT_STATUS_CLOSED
	 * @see		ISSUEFILTER_SORT_STATUS_ALL
	 */
	public static String[]						ISSUEFILTER_SORT_STATUS_ARRAY;

	/**
	 * 排序条件集合
	 * @see 	ISSUEFILTER_SORT_CLOUMN_ISSUEID
	 * @see		ISSUEFILTER_SORT_CLOUMN_DUEDATE
	 * @see		ISSUEFILTER_SORT_CLOUMN_STARTDATE
	 * @see		ISSUEFILTER_SORT_CLOUMN_UPDATEDON
	 * @see		ISSUEFILTER_SORT_CLOUMN_PRIORITY
	 */
	public static String[]						ISSUEFILTER_SORT_CLOUMN_ARRAY;

	/**
	 * 排序集合
	 * @see ISSUEFILTER_SORT_ASC
	 * @see	ISSUEFILTER_SORT_DESC
	 */
	public static String[]						ISSUEFILTER_SORT_SORT_ARRAY;

	/**
	 * 排序条件集合对应文字
	 * @see		ISSUEFILTER_SORT_CLOUMN_ARRAY
	 */
	public static String[]						ISSUEFILTER_SORT_CLOUMN_ARRAY_NAME;

	/**
	 * 排序集合对应文字
	 * @see		ISSUEFILTER_SORT_SORT_ARRAY
	 */
	public static String[]						ISSUEFILTER_SORT_SORT_ARRAY_NAME;

	/**
	 * 问题属性集合
	 */
	public static final HashMap<String,String>	ISSUENAME							= new HashMap<String,String>();

	/**
	 * GET申请每页的数量
	 */
	public static final int						PAGELIMIT							= 30;

	/**
	 * 跟踪状态的颜色数组
	 */
	public static final int[]					TRACKER_DEFAULT_COLORS;

	public static final String					PATH_SDCARD;
	public static String						PATH_REDMINE;
	public static String						PATH_REDMINEIMG;
	public static String						PATH_REDMINEATTACHMENT;
	static
	{
		boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);   //判断sd卡是否存在 
		if (sdCardExist)
		{
			PATH_SDCARD = Environment.getExternalStorageDirectory().getPath();//获取根目录 
		} else
		{
			PATH_SDCARD = Environment.getRootDirectory().getAbsolutePath();//获取根目录
		}

		PATH_REDMINE = PATH_SDCARD + "/phone_redmine";
		PATH_REDMINEIMG = PATH_REDMINE + "/img";
		PATH_REDMINEATTACHMENT = PATH_REDMINE + "/attachment";

		ISSUENAME.clear();
		ISSUENAME.put(RMIssueDetail.PROJECT_ID, "项目");
		ISSUENAME.put(RMIssueDetail.TRACKER_ID, "跟踪");
		ISSUENAME.put(RMIssueDetail.STATUS_ID, "状态");
		ISSUENAME.put(RMIssueDetail.PRIORITY_ID, "优先级");
		ISSUENAME.put(RMIssueDetail.ASSIGNED_TO_ID, "指派给");
		ISSUENAME.put(RMIssueDetail.FIXED_VERSION_ID, "版本");
		ISSUENAME.put(RMIssueDetail.CATEGORY_ID, "类型");
		ISSUENAME.put(RMIssueDetail.SUBJECT, "主题");
		ISSUENAME.put(RMIssueDetail.DESCRIPTION, "描述");
		ISSUENAME.put(RMIssueDetail.START_DATE, "开始日期");
		ISSUENAME.put(RMIssueDetail.DUE_DATE, "计划完成日期");
		ISSUENAME.put(RMIssueDetail.DONE_RATIO, "完成比例");

		//颜色数组
		int[] arrayOfInt = new int[10];
		arrayOfInt[0] = Color.rgb(147, 147, 147);	//灰
		arrayOfInt[1] = Color.rgb(201, 23, 23);		//红
		arrayOfInt[2] = Color.rgb(67, 141, 252);	//绿
		arrayOfInt[3] = Color.rgb(101, 93, 136);	//紫
		arrayOfInt[4] = Color.rgb(234, 215, 44);	//黄
		arrayOfInt[5] = Color.rgb(230, 87, 215);	//粉	
		arrayOfInt[6] = Color.rgb(45, 201, 198);	//草绿
		arrayOfInt[7] = Color.rgb(255, 184, 205);	//浅粉
		arrayOfInt[8] = Color.rgb(200, 116, 83);	//棕
		arrayOfInt[9] = Color.rgb(67, 205, 126);	//浅绿
		TRACKER_DEFAULT_COLORS = arrayOfInt;

		ISSUEFILTER_SORT_STATUS_ARRAY = new String[]
		{
			ISSUEFILTER_SORT_STATUS_NOTCLOSED,
			ISSUEFILTER_SORT_STATUS_CLOSED,
			ISSUEFILTER_SORT_STATUS_ALL
		};

		ISSUEFILTER_SORT_CLOUMN_ARRAY = new String[]
		{
			ISSUEFILTER_SORT_CLOUMN_ISSUEID,
			ISSUEFILTER_SORT_CLOUMN_DUEDATE,
			ISSUEFILTER_SORT_CLOUMN_STARTDATE,
			ISSUEFILTER_SORT_CLOUMN_UPDATEDON,
			ISSUEFILTER_SORT_CLOUMN_PRIORITY
		};

		ISSUEFILTER_SORT_SORT_ARRAY = new String[]
		{
			ISSUEFILTER_SORT_ASC,
			ISSUEFILTER_SORT_DESC
		};

		ISSUEFILTER_SORT_CLOUMN_ARRAY_NAME = new String[]
		{
			"问题ID",
			"计划完成时间",
			"开始时间",
			"更新时间",
			"优先级"
		};

		ISSUEFILTER_SORT_SORT_ARRAY_NAME = new String[]
		{
			"升序",
			"降序"
		};

	}
}
