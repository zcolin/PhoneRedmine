package com.phone.redmine.entity;

import java.util.ArrayList;
import android.os.Parcel;
import android.os.Parcelable;
import com.phone.redmine.entity.RMIssueDetail.CustomFields;
import com.phone.redmine.util.StringUtil;

/**
 * RM 进入问题详情页面时传入的需要反填的信息
 */
public class RMIssueDetailTransferData implements Parcelable
{

	public int						issueId;		//问题Id
	public int						projectId;		//项目Id
	public String					subject;		//主题
	public String					description;	//描述
	public ArrayList<CustomFields>	custom_fields;	//自定义字段

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(issueId);
		dest.writeInt(projectId);
		dest.writeString(subject);
		dest.writeString(description);
		dest.writeList(custom_fields);
	}

	public static final Parcelable.Creator<RMIssueDetailTransferData>	CREATOR	= new Parcelable.Creator<RMIssueDetailTransferData>()
																				{

																					@Override
																					@SuppressWarnings("unchecked")
																					public RMIssueDetailTransferData createFromParcel(
																							Parcel source)
																					{
																						RMIssueDetailTransferData info = new RMIssueDetailTransferData();
																						info.issueId = source.readInt();
																						info.projectId = source.readInt();
																						info.subject = source.readString();
																						info.description = source.readString();
																						info.custom_fields = source.readArrayList(CustomFields.class.getClassLoader());
																						return info;
																					}

																					@Override
																					public RMIssueDetailTransferData[] newArray(
																							int size)
																					{
																						return new RMIssueDetailTransferData[size];
																					}
																				};

	/**
	 * 判定传递的内容是否是空内容
	 * 
	 * @return			传递的内容是否为空
	 */
	public boolean isEmpty()
	{
		return projectId == 0 && StringUtil.isEmpty(subject) && StringUtil.isEmpty(description) && custom_fields == null;
	}
}
