package com.phone.redmine.http.entity;

import java.util.ArrayList;
import com.phone.redmine.entity.RMMemberShip;

public class RMMemberShipReply
{
	public ArrayList<RMMemberShip>	memberships;
	public int						total_count;	//本次返回的条数
	public int						offset;			//分页偏移
	public int						limit;			//本次返回的最大条数
}
