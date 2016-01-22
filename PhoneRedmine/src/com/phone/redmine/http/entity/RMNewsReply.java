package com.phone.redmine.http.entity;

import java.util.ArrayList;
import com.phone.redmine.entity.RMNews;

public class RMNewsReply
{
	public ArrayList<RMNews>	news;
	public int					total_count;	//本次返回的条数
	public int					offset;		//分页偏移
	public int					limit;			//本次返回的最大条数
}
