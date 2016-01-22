package com.phone.redmine.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *	RM id和name对信息 
 */
public class RMPairInfo implements Parcelable
{

	public int		id;
	public String	name;

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
	}

	public static final Parcelable.Creator<RMPairInfo>	CREATOR	= new Parcelable.Creator<RMPairInfo>()
																{

																	@Override
																	public RMPairInfo createFromParcel(Parcel source)
																	{
																		RMPairInfo info = new RMPairInfo();
																		info.id = source.readInt();
																		info.name = source.readString();
																		return info;
																	}

																	@Override
																	public RMPairInfo[] newArray(int size)
																	{
																		return new RMPairInfo[size];
																	}
																};
}
