package com.phone.redmine.entity;

/**
 * 处理信息携带类
 */
public class FCProcessInfo
{

	private int			id;		//id
	private String		msg;	//描述
	private Exception	e;		//异常
	private boolean		flag;	//扩展boolean
	private Object		info;	//扩展对象

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public boolean isFlag()
	{
		return flag;
	}

	public void setFlag(boolean flag)
	{
		this.flag = flag;
	}

	public String getMsg()
	{
		return msg;
	}

	public void setMsg(String msg)
	{
		this.msg = msg;
	}

	public Exception getE()
	{
		return e;
	}

	public void setE(Exception e)
	{
		this.e = e;
	}

	public Object getInfo()
	{
		return info;
	}

	public void setInfo(Object info)
	{
		this.info = info;
	}
}
