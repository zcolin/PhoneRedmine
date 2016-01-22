package com.phone.redmine.entity;

import java.util.ArrayList;

/**
 *	RM 项目信息
 */
public class RMProject
{
	public boolean 					isParent;			//是否父项目
	public int						id;	
	public String					name;
	public String					identifier;
	public String					description;
	public String					status;
	public String					created_on;
	public String					updated_on;
	public RMProject				parent;
	public ArrayList<RMPairInfo>	trackers;
	public ArrayList<RMPairInfo>	issue_categories;
}