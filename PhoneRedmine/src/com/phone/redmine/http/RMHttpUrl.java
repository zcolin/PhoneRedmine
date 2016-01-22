package com.phone.redmine.http;

import com.phone.redmine.util.SharePrefUtil;

public class RMHttpUrl
{

	public static String	URLHEAD_HTTP;
	public static String	URLHEAD_REDMINE;
	public static String	URL_LOGIN;				//登录
	public static String	URL_ISSUES;			//获取问题列表
	public static String	URL_PRE_ISSUES;		//问题前缀
	public static String	URL_UPLOADS;			//上传附件
	public static String	URL_PROJECTS;			//获取项目列表
	public static String	URL_NEWS;				//新闻列表
	public static String	URL_PRE_PROJECTS;		//项目前缀
	public static String	URL_ISSUE_PRIORITIES;	//优先级列表
	public static String	URL_ISSUES_STATUSES;	//问题状态列表

	static
	{
		loadUrl();
	}

	public static void loadUrl()
	{
		URLHEAD_HTTP = SharePrefUtil.getInstance().getString("url", null);
		URLHEAD_REDMINE = URLHEAD_HTTP + "/redmine/";
		URL_LOGIN = URLHEAD_REDMINE + "users/current.json";
		URL_ISSUES = URLHEAD_REDMINE + "issues.json";
		URL_PRE_ISSUES = URLHEAD_REDMINE + "issues/";
		URL_UPLOADS = URLHEAD_REDMINE + "uploads.json";
		URL_PROJECTS = URLHEAD_REDMINE + "projects.json";
		URL_NEWS = URLHEAD_REDMINE + "news.json";
		URL_PRE_PROJECTS = URLHEAD_REDMINE + "projects/";
		URL_ISSUE_PRIORITIES = URLHEAD_REDMINE + "enumerations/issue_priorities.json";
		URL_ISSUES_STATUSES = URLHEAD_REDMINE + "issue_statuses.json";
	}
}
