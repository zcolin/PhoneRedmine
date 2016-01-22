package com.phone.redmine.http.entity;

public class RMIssueAutoPost
{
	public int		project_id;
	public int		status_id;
	public String	notes;

	public static class RMIssueAutoPostApply
	{
		public RMIssueAutoPost	issue;
	}
}
