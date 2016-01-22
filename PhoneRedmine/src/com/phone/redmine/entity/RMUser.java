package com.phone.redmine.entity;

/**
 * RM 登录用户信息
 */
public class RMUser
{

	public int		id;				//用户Id
	public String	login;			//username
	public String	firstname;		
	public String	lastname;
	public String	mail;
	public String	created_on;
	public String	last_login_on;	//最后一次登录时间
	public String	api_key;		//登录的apiKey
}
