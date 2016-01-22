package com.phone.redmine.http.entity;

import java.util.ArrayList;
import com.phone.redmine.entity.RMIssueDetail.CustomFields;
import com.phone.redmine.http.entity.RMUploadReply.Token;

public class RMIssuePost
{
	public transient int			id; //transient在序列化成json时不会序列化
	public int						project_id;
	public int						tracker_id;
	public int						status_id;
	public int						priority_id;
	public String					subject;
	public String					description;
	public int						category_id;
	public int						fixed_version_id;
	public int						assigned_to_id;
	public int						done_ratio;
	public String					start_date;
	public String					due_date;
	public String					notes;
	public ArrayList<Token>			uploads;
	public ArrayList<CustomFields>	custom_fields;

	//	public int parent_issue_id;
	//	public int watcher_user_ids;
	//	public int is_private; //Use true or false to indicate whether the issue is private or not
	//	public int estimated_hours; //Number of hours estimated for issue
	public static class RMIssuePostApply
	{
		public RMIssuePost	issue;
	}
}
