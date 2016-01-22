package com.phone.redmine.entity;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.util.StringUtil;

/**
 * RM 问题详情
 */
public class RMIssueDetail
{

	/**字段关键字——附件*/
	public static final String		ATTACHMENT			= "attachment";
	/**字段关键字——项目Id*/
	public static final String		PROJECT_ID			= "project_id";
	/**字段关键字——跟踪Id*/
	public static final String		TRACKER_ID			= "tracker_id";
	/**字段关键字——状态Id*/
	public static final String		STATUS_ID			= "status_id";
	/**字段关键字——优先级Id*/
	public static final String		PRIORITY_ID			= "priority_id";
	/**字段关键字——指派给Id*/
	public static final String		ASSIGNED_TO_ID		= "assigned_to_id";
	/**字段关键字——版本Id*/
	public static final String		FIXED_VERSION_ID	= "fixed_version_id";
	/**字段关键字——类别Id*/
	public static final String		CATEGORY_ID			= "category_id";
	/**字段关键字——主题*/
	public static final String		SUBJECT				= "subject";
	/**字段关键字——描述*/
	public static final String		DESCRIPTION			= "description";
	/**字段关键字——开始时间*/
	public static final String		START_DATE			= "start_date";
	/**字段关键字——结束时间*/
	public static final String		DUE_DATE			= "due_date";
	/**字段关键字——完成比例*/
	public static final String		DONE_RATIO			= "done_ratio";

	public long						id;
	public RMPairInfo				project;									//工程
	public RMPairInfo				tracker;									//如支持 错误 功能等
	public RMPairInfo				status;									//状态 如新建等
	public RMPairInfo				priority;									//优先等级
	public RMPairInfo				author;									//创建人
	public RMPairInfo				category;									//问题分类
	public RMPairInfo				assigned_to;								//指派人
	public String					subject;									//问题主题
	public String					description;								//问题描述
	public String					start_date;								//开始时间
	public String					due_date;									//计划完成时间
	public RMPairInfo				fixed_version;								//关联的版本
	public int						done_ratio;								//完成比例
	public String					created_on;								//创建时间
	public String					updated_on;								//更新时间	
	public ArrayList<Children>		children;									//子任务列表
	public ArrayList<Attachment>	attachments;								//附件列表
	public ArrayList<Journal>		journals;									//操作记录详情
	public ArrayList<CustomFields>	custom_fields;								//自定义字段

	/**
	 * 自定义字段信息
	 */
	public static class CustomFields implements Parcelable
	{

		public int		id;
		public String	name;
		public String	value;

		@Override
		public int describeContents()
		{
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			dest.writeInt(id);
			dest.writeString(name);
			dest.writeString(value);
		}

		public static final Parcelable.Creator<CustomFields>	CREATOR	= new Parcelable.Creator<CustomFields>()
																		{

																			@Override
																			public CustomFields createFromParcel(
																					Parcel source)
																			{
																				CustomFields info = new CustomFields();
																				info.id = source.readInt();
																				info.name = source.readString();
																				info.value = source.readString();
																				return info;
																			}

																			@Override
																			public CustomFields[] newArray(int size)
																			{
																				return new CustomFields[size];
																			}
																		};
	}

	/**
	 *子任务信息 
	 */
	public static class Children
	{

		public int		id;
		public String	subject;
	}

	/**
	 * 附件信息
	 */
	public static class Attachment
	{

		public int		id;
		public String	filename;
		public long		filesize;
		public String	content_url;
		public String	created_on;
	}

	/**
	 * 每次操作记录信息
	 */
	public static class Journal
	{

		public int						id;
		public RMPairInfo				user;
		public String					created_on;
		public String					notes;
		public ArrayList<JournalDetail>	details;
	}

	/**
	 * 每次对应的多条的    每条操作记录信息
	 */
	public static class JournalDetail
	{

		public String	property;
		public String	name;
		public String	old_value;
		public String	new_value;
	}

	/**
	 * 拼接 历史操作记录 的显示字符串
	 * 
	 * @param keyword			字段关键字
	 * @param oldValue			旧的值
	 * @param newValue			新的值
	 * 
	 * @return					拼接完的字符串
	 */
	public static String getStringArrBykeyAndId(String keyword, String oldValue, String newValue)
	{
		
		/*如果只有新纪录，则为新增，只有旧记录则为删除， 新旧都有，则为从...到...*/
		StringBuilder builder = new StringBuilder();
		if (StringUtil.isEmpty(oldValue))
		{
			String name = null;
			if (PROJECT_ID.equals(keyword) || TRACKER_ID.equals(keyword) || STATUS_ID.equals(keyword) || CATEGORY_ID.equals(keyword)
					|| PRIORITY_ID.equals(keyword) || ASSIGNED_TO_ID.equals(keyword) || FIXED_VERSION_ID.equals(keyword))
			{
				try
				{
					RMPairBoolInfo info1 = getPairInfoByKeyAndId(keyword, Integer.parseInt(newValue));
					name = info1 == null ? "" : info1.name;
				} catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			} else if (SUBJECT.equals(keyword) || DESCRIPTION.equals(keyword) || START_DATE.equals(keyword)
					|| DUE_DATE.equals(keyword) || DONE_RATIO.equals(keyword) || ATTACHMENT.equals(keyword))
			{
				name = newValue;
			}

			builder.append(" 新增 ").append(name);
		} else if (StringUtil.isEmpty(newValue))
		{
			String name = null;
			if (PROJECT_ID.equals(keyword) || TRACKER_ID.equals(keyword) || STATUS_ID.equals(keyword) || CATEGORY_ID.equals(keyword)
					|| PRIORITY_ID.equals(keyword) || ASSIGNED_TO_ID.equals(keyword) || FIXED_VERSION_ID.equals(keyword))
			{
				try
				{
					RMPairBoolInfo info1 = getPairInfoByKeyAndId(keyword, Integer.parseInt(oldValue));
					name = info1 == null ? "" : info1.name;
				} catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			} else if (SUBJECT.equals(keyword) || DESCRIPTION.equals(keyword) || START_DATE.equals(keyword)
					|| DUE_DATE.equals(keyword) || DONE_RATIO.equals(keyword) || ATTACHMENT.equals(keyword))
			{
				name = oldValue;
			}

			builder.append(" 删除 ").append(name);
		} else
		{
			String name1 = null;
			String name2 = null;
			if (PROJECT_ID.equals(keyword) || TRACKER_ID.equals(keyword) || STATUS_ID.equals(keyword) || CATEGORY_ID.equals(keyword)
					|| PRIORITY_ID.equals(keyword) || ASSIGNED_TO_ID.equals(keyword) || FIXED_VERSION_ID.equals(keyword))
			{
				try
				{
					RMPairBoolInfo info1 = getPairInfoByKeyAndId(keyword, Integer.parseInt(oldValue));
					RMPairBoolInfo info2 = getPairInfoByKeyAndId(keyword, Integer.parseInt(newValue));
					name1 = info1 == null ? "" : info1.name;
					name2 = info2 == null ? "" : info2.name;
				} catch (NumberFormatException e)
				{
					e.printStackTrace();
				}
			} else if (SUBJECT.equals(keyword) || DESCRIPTION.equals(keyword) || START_DATE.equals(keyword)
					|| DUE_DATE.equals(keyword) || DONE_RATIO.equals(keyword) || ATTACHMENT.equals(keyword))
			{
				name1 = oldValue;
				name2 = newValue;
			}

			builder.append(" 从 ").append(name1).append(" 变更为 ").append(name2);
		}
		return builder.toString();
	}

	/**
	 * 根据关键字和Id从数据库获取关键字对应的 对象信息
	 * 
	 * @param keywork			字段关键字
	 * @param id				对象Id
	 * 
	 * @return					根据关键生成的对象，没有返回null
	 */
	public static RMPairBoolInfo getPairInfoByKeyAndId(String keywork, int id)
	{
		RMPairBoolInfo pairBooleanInfo = null;
		if (PROJECT_ID.equals(keywork))
		{
			pairBooleanInfo = RMDBLocal.getInstance().getProjectById(id);
		} else if (TRACKER_ID.equals(keywork))
		{
			pairBooleanInfo = RMDBLocal.getInstance().getTrackerById(id);
		} else if (STATUS_ID.equals(keywork))
		{
			pairBooleanInfo = RMDBLocal.getInstance().getIssueStatusById(id);
		} else if (PRIORITY_ID.equals(keywork))
		{
			pairBooleanInfo = RMDBLocal.getInstance().getPrioritieById(id);
		} else if (ASSIGNED_TO_ID.equals(keywork))
		{
			pairBooleanInfo = RMDBLocal.getInstance().getStaffById(id);
		} else if (FIXED_VERSION_ID.equals(keywork))
		{
			pairBooleanInfo = RMDBLocal.getInstance().getVersionById(id);
		} else if (CATEGORY_ID.equals(keywork))
		{
			pairBooleanInfo = RMDBLocal.getInstance().getIssueCategoryById(id);
		}
		return pairBooleanInfo;
	}
}
