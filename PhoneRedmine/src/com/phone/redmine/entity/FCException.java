package com.phone.redmine.entity;

/**
 * 异常处理实体类
 */
public class FCException extends Exception
{

	private static final long	serialVersionUID	= 8023957238117792870L; // 版本ID
	private String				strDetailMsg;								// 异常信息
	private String				strContext;								// 异常内容
	private Throwable			throwable;									// 异常Throwable

	public FCException ()
	{
		super();
	}

	/**
	 * @param msg		异常信息
	 */
	public FCException (String msg)
	{
		super(msg);
		strDetailMsg = msg;
	}

	/**
	 * @param msg		异常信息
	 * @param able		异常实体
	 */
	public FCException (String msg, Throwable able)
	{
		super(msg, able);
		strDetailMsg = msg;
		throwable = able;
	}

	@Override
	public String getMessage()
	{
		return strDetailMsg;
	}

	@Override
	public Throwable getCause()
	{
		return throwable;
	}

	/**
	 * @return   内容描述
	 */
	public String getStrContext()
	{
		return strContext;
	}

	/**
	 * @param strContext  内容描述
	 */
	public void setStrContext(String strContext)
	{
		this.strContext = strContext;
	}

}
