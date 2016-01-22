package com.phone.redmine.entity;

/**
 *	RM 人员和项目的关联信息 
 */
public class RMMemberShip
{

	public int			id;
	public RMPairInfo	project;	//项目信息
	public RMPairInfo	user;		//人员信息
}
