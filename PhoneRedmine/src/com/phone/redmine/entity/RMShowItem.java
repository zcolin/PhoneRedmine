package com.phone.redmine.entity;

/**
 * RM  添加RM任务时，控制选择项是否显示
 */
public class RMShowItem
{

	public int		project_id;		//项目Id
	public boolean	track;			//是否显示 跟踪
	public boolean	priority;		//是否显示 优先级
	public boolean	done_ratio;		//是否显示 完成比例
	public boolean	start_date;		//是否显示 开始时间
	public boolean	due_date;		//是否显示 完成日期
	public boolean	fixed_version;	//是否显示 版本
	public boolean	category;		//是否显示 类别

	public RMShowItem ()
	{}

	/**
	 * @param isShowAll		是否默认显示
	 */
	public RMShowItem (boolean isShowAll)
	{
		if (isShowAll)
		{
			track = true;
			priority = true;
			done_ratio = true;
			start_date = true;
			due_date = true;
			fixed_version = true;
			category = true;
		}
	}
}
