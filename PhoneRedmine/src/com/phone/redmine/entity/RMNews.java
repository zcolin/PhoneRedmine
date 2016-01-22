package com.phone.redmine.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * RM 新闻信息
 */
public class RMNews implements Parcelable
{

	public int			id;
	public RMPairInfo	project;		//工程
	public RMPairInfo	author;			//创建人
	public String		title;			//新闻主题
	public String		summary;		//新闻描述
	public String		description;	//新闻内容
	public String		created_on;		//创建时间

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeInt(id);
		dest.writeParcelable(project, flags);
		dest.writeParcelable(author, flags);
		dest.writeString(title);
		dest.writeString(summary);
		dest.writeString(description);
		dest.writeString(created_on);
	}

	public static final Parcelable.Creator<RMNews>	CREATOR	= new Parcelable.Creator<RMNews>()
															{

																@Override
																public RMNews createFromParcel(Parcel source)
																{
																	RMNews info = new RMNews();
																	info.id = source.readInt();
																	info.project = source.readParcelable(RMPairInfo.class.getClassLoader());
																	info.author = source.readParcelable(RMPairInfo.class.getClassLoader());
																	info.title = source.readString();
																	info.summary = source.readString();
																	info.description = source.readString();
																	info.created_on = source.readString();
																	return info;
																}

																@Override
																public RMNews[] newArray(int size)
																{
																	return new RMNews[size];
																}
															};
}
