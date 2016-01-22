package com.phone.redmine.entity;

/**
 * EventBus 的事件传递类
 */
public class EventEntity
{

	/**
	 * 服务器获取到新的问题通知
	 */
	public static class OnComplectUpdateIssueEvent
	{
	}

	/**
	 * 立即执行从服务器获取新问题（用于红点显示）
	 */
	public static class UpdateIssueNowEvent
	{
	}
}
