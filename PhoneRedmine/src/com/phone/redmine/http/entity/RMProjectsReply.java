package com.phone.redmine.http.entity;

import java.util.ArrayList;
import com.phone.redmine.entity.RMProject;

public class RMProjectsReply
{
	public ArrayList<RMProject>	projects;
	public int					total_count;	//本次返回的条数
	public int					offset;			//分页偏移
	public int					limit;			//本次返回的最大条数
}
