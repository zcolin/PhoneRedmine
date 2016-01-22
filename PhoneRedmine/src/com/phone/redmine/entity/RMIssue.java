package com.phone.redmine.entity;

import java.util.ArrayList;
import com.phone.redmine.entity.RMIssueDetail.CustomFields;

/**
 * RM 问题信息
 */
public class RMIssue
{

	public int						id;				//问题Id
	public RMPairInfo				project;		//工程
	public RMPairInfo				tracker;		//如支持 错误 功能等
	public RMPairInfo				status;			//状态 如新建等
	public RMPairInfo				priority;		//优先等级
	public RMPairInfo				author;			//创建人
	public RMPairInfo				assigned_to;	//指派人
	public String					subject;		//问题主题
	public String					description;	//问题描述
	public String					start_date;		//开始时间
	public String					due_date;		//计划完成时间
	public RMPairInfo				fixed_version;	//关联的版本
	public int						done_ratio;		//完成比例
	public String					created_on;		//创建时间
	public String					updated_on;		//更新时间	
	public ArrayList<CustomFields>	custom_fields;	//自定义字段列表
}
