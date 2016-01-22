package com.phone.redmine.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *  RM 高级过滤器信息
 */
public class RMIssueFilter implements Parcelable
{

	public int		id;
	public String	name;
	public int		project_id;
	public int		status_id;
	public int		tracker_id;
	public int		assigned_to_id;
	public int		priority_id;
	public int		fixed_version_id;
	public int		category_id;
	public String	cloumn;				//@see	RMConst.ISSUEFILTER_SORT_CLOUMN_ARRAY
	public String	sort;				//@see	RMConst.ISSUEFILTER_SORT_SORT_ARRAY

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
		dest.writeInt(project_id);
		dest.writeInt(status_id);
		dest.writeInt(tracker_id);
		dest.writeInt(assigned_to_id);
		dest.writeInt(priority_id);
		dest.writeInt(fixed_version_id);
		dest.writeInt(category_id);
		dest.writeString(cloumn);
		dest.writeString(sort);
	}

	public static final Parcelable.Creator<RMIssueFilter>	CREATOR	= new Parcelable.Creator<RMIssueFilter>()
																	{

																		@Override
																		public RMIssueFilter createFromParcel(
																				Parcel source)
																		{
																			RMIssueFilter info = new RMIssueFilter();
																			info.id = source.readInt();
																			info.name = source.readString();
																			info.project_id = source.readInt();
																			info.status_id = source.readInt();
																			info.tracker_id = source.readInt();
																			info.assigned_to_id = source.readInt();
																			info.priority_id = source.readInt();
																			info.fixed_version_id = source.readInt();
																			info.category_id = source.readInt();
																			info.cloumn = source.readString();
																			info.sort = source.readString();
																			return info;
																		}

																		@Override
																		public RMIssueFilter[] newArray(int size)
																		{
																			return new RMIssueFilter[size];
																		}
																	};
}
