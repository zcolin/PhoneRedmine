package com.phone.redmine.biz;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.os.AsyncTask;
import com.phone.redmine.biz.RMBiz.OnLoadComplete;
import com.phone.redmine.db.RMDBLocal;
import com.phone.redmine.entity.FCException;
import com.phone.redmine.entity.RMMemberShip;
import com.phone.redmine.entity.RMPairInfo;
import com.phone.redmine.entity.RMProject;
import com.phone.redmine.entity.RMShowItem;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.util.StringUtil;
import com.phone.redmine.view.FCToast;

/**
 * 问题的AssignTo单独网页抓取 
 */
public class RMStaffWebAsyncTask extends AsyncTask<Integer,Integer,Boolean>
{

	private OnLoadComplete	listener;

	public RMStaffWebAsyncTask (OnLoadComplete listener)
	{
		this.listener = listener;
	}

	@Override
	protected void onPreExecute()
	{
		RMDBLocal.getInstance().clearStaffs();
	}

	@Override
	protected void onPostExecute(Boolean result)
	{
		if (listener != null)
		{
			listener.onComplete(result);
		}
	}

	@Override
	protected Boolean doInBackground(Integer... params)
	{
		boolean isSuccess = false;
		String cookie = SharePrefUtil.getInstance().getString(RMConst.SHARE_REDMINECOOKIE, null);
		if (cookie != null)
		{
			try
			{
				ArrayList<RMMemberShip> lstMemberShip = new ArrayList<RMMemberShip>();
				ArrayList<RMShowItem> lstShowItem = new ArrayList<RMShowItem>();
				ArrayList<RMProject> lstRMProjects = RMBiz.lstRMProjects;
				int size = lstRMProjects.size();
				for (int i = 0; i < size; i++)
				{
					RMProject project = lstRMProjects.get(i);
					if (project.isParent)
					{
						continue;
					}

					try
					{

						String url = RMHttpUrl.URL_PRE_PROJECTS + project.identifier + "/issues/new/";
						Response res = Jsoup.connect(url).timeout(50000).header("Cookie", cookie).execute();
						Document doc = res.parse();

						/*如果到了登录页面，直接跳出，并且移除cookie*/
						Elements etlogin = doc.select("label[for=username]");
						if (etlogin.size() > 0)
						{
							throw new FCException();
						}

						getAssignTo(doc, project, lstMemberShip);

						getShowItem(doc, project, lstShowItem);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
				RMDBLocal.getInstance().insertStaffs(lstMemberShip);
				RMDBLocal.getInstance().insertShowItem(lstShowItem);
				isSuccess = true;
			} catch (FCException e)
			{
				SharePrefUtil.getInstance().removeString(RMConst.SHARE_REDMINECOOKIE);
				FCToast.ToastShow("Cookie失效，已重置，请重新同步数据！");
			}
		}
		return isSuccess;
	}

	/*
	 * 获取 指派给 人员列表
	 * 
	 * @param doc
	 * @param project
	 * @param lstMemberShip
	 */
	private void getAssignTo(Document doc, RMProject project, ArrayList<RMMemberShip> lstMemberShip)
	{
		Element eAssignTo = doc.select("select[id=issue_assigned_to_id]").first();
		if (eAssignTo != null)
		{
			Elements eOptions = eAssignTo.select("option");
			for (int j = 0; j < eOptions.size(); j++)
			{
				Element et = eOptions.get(j);
				String strEt = et.text().trim();
				String strValue = et.attr("value");
				if (StringUtil.isNotBlank(strValue) && !"<< 我 >>".equals(strEt))
				{
					int userId = Integer.parseInt(et.attr("value"));
					RMMemberShip ship = new RMMemberShip();
					ship.user = new RMPairInfo();
					ship.project = new RMPairInfo();
					ship.user.id = userId;
					ship.user.name = strEt;
					ship.project.id = project.id;
					lstMemberShip.add(ship);
				}
			}
		}
	}

	/*
	 * 获取 显示项 列表
	 * 
	 * @param doc
	 * @param project
	 * @param RMShowItem
	 */
	private void getShowItem(Document doc, RMProject project, ArrayList<RMShowItem> RMShowItem)
	{
		RMShowItem showItem = new RMShowItem();
		showItem.project_id = project.id;
		showItem.track = doc.select("label[for=issue_tracker_id]").first() != null;
		showItem.priority = doc.select("label[for=issue_priority_id]").first() != null;
		showItem.done_ratio = doc.select("label[for=issue_done_ratio]").first() != null;
		showItem.start_date = doc.select("label[for=issue_start_date]").first() != null;
		showItem.due_date = doc.select("label[for=issue_due_date]").first() != null;
		showItem.fixed_version = doc.select("label[for=issue_fixed_version_id]").first() != null;
		showItem.category = doc.select("label[for=issue_category_id]").first() != null;
		RMShowItem.add(showItem);
	}
}
