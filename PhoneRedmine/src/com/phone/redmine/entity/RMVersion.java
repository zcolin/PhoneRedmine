package com.phone.redmine.entity;

/**
 * RM 版本信息
 */
public class RMVersion
{

	public long			id;
	public String		name;			//版本主题
	public RMPairInfo	project;		//项目信息
	public String		description;	//描述
	public String		status;			//状态
	public String		sharing;		
	public String		created_on;		//创建时间
	public String		updated_on;		//更新时间
}
