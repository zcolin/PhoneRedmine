package com.phone.redmine.db;

import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.phone.redmine.app.App;
import com.phone.redmine.entity.RMIssue;
import com.phone.redmine.entity.RMIssueFilter;
import com.phone.redmine.entity.RMIssueStatus;
import com.phone.redmine.entity.RMMemberShip;
import com.phone.redmine.entity.RMPairBoolInfo;
import com.phone.redmine.entity.RMPairInfo;
import com.phone.redmine.entity.RMPrioritie;
import com.phone.redmine.entity.RMProject;
import com.phone.redmine.entity.RMShowItem;
import com.phone.redmine.entity.RMVersion;
import com.phone.redmine.util.LogUtil;

/**
 *	RM 常用数据主体数据库 
 */
public class RMDBLocal extends SQLiteOpenHelper
{

	public static final String	DB_NAME		= "fcredmine.db";
	public static final int		DB_VERSION	= 2;
	public static RMDBLocal		DBINSTANCE;
	private SQLiteDatabase		dataBase;

	public static RMDBLocal getInstance()
	{
		if (DBINSTANCE == null)
		{
			DBINSTANCE = new RMDBLocal();
		}
		return DBINSTANCE;
	}

	private RMDBLocal ()
	{
		super(App.APPCONTEXT, DB_NAME, null, DB_VERSION);
		dataBase = getWritableDatabase();
	}

	@Override
	public synchronized void close()
	{
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		/*项目*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_project (id integer not null ,name text not null ,"
				+ "identifier text, parent text,parent_id integer,description text, is_parent integer, "
				+ "create_date text,update_date text, statuses text, primary key(id))");
		/*人员*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_staffs (project_id integer not null ,"
				+ "id integer not null ,name text not null , sort_order integer, primary key(project_id,id))");
		/*跟踪状态*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_trackers (project_id integer not null ,"
				+ "id integer not null ,name text not null ,"
				+ "sort_order integer, primary key(project_id,id))");
		/*问题分类*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_issue_category (project_id integer not null, "
				+ "id integer not null ,project_name text not null ,name text not null, "
				+ "sort_order integer,primary key(project_id,id))");
		/*版本*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_version (id integer not null ,name text not null ,"
				+ "project_id integer not null ,project_name text not null ,description text,status text,"
				+ "sharing text,created_on text,updated_on text, primary key(id,project_id))");
		/*问题状态*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_issue_status (id integer not null, name TEXT not null, is_default integer, is_closed integer,"
				+ "sort_order integer,primary key(id))");
		/*问题优先级*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_issue_priorities (id integer not null, name TEXT not null, is_default integer, "
				+ "sort_order integer,primary key(id))");
		/*自定义过滤器*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_issue_self_filter (id integer not null primary key autoincrement, name TEXT not null, project_id integer, status_id integer,"
				+ "tracker_id integer, assigned_to_id integer, priority_id integer, fixed_version_id integer, category_id integer, cloumn text,"
				+ "sort text)");
		/*问题更新（新增的未读的）*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_issue_update (id integer not null primary key autoincrement, issue_id integer not null)");
		/*项目关联的需要显示的item*/
		db.execSQL("CREATE TABLE IF NOT EXISTS tbl_issue_show_item (project_id integer not null primary key, track integer not null, "
				+ "priority integer not null, done_ratio integer not null, start_date integer not null, due_date integer not null,"
				+ "fixed_version integer not null, category integer not null)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if (oldVersion == 1 && newVersion == 2)
		{
			/*项目关联的需要显示的item*/
			db.execSQL("CREATE TABLE IF NOT EXISTS tbl_issue_show_item (project_id integer not null primary key, track integer not null, "
					+ "priority integer not null, done_ratio integer not null, start_date integer not null, due_date integer not null,"
					+ "fixed_version integer not null, category integer not null)");
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db)
	{}

	/**
	 *	根据项目的Id获取项目信息
	 * 
	 * @param project_id		项目Id
	 * 
	 * @return					项目信息
	 */
	public RMPairBoolInfo getProjectById(int project_id)
	{
		String sql = "select id, name, is_parent from tbl_project where id=?";
		Cursor cursor = null;
		RMPairBoolInfo project = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(project_id)});
			if (cursor.moveToNext())
			{
				project = new RMPairBoolInfo();
				project.id = cursor.getInt(cursor.getColumnIndex("id"));
				project.name = cursor.getString(cursor.getColumnIndex("name"));
				project.bool1 = cursor.getInt(cursor.getColumnIndex("is_parent")) == 0 ? false : true;
			}
		} catch (Exception e)
		{
			LogUtil.w("RMDBLocal--getProjectById", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return project;
	}

	/**
	 * 获取所有的项目
	 * 
	 * @param lstProjects				项目列表
	 * 
	 * @return							异常
	 */
	public Throwable getAllProjects(ArrayList<RMPairBoolInfo> lstProjects)
	{
		Throwable ew = null;
		String sql = "select id, name, is_parent from tbl_project";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				RMPairBoolInfo project = new RMPairBoolInfo();
				project.id = cursor.getInt(cursor.getColumnIndex("id"));
				project.name = cursor.getString(cursor.getColumnIndex("name"));
				project.bool1 = cursor.getInt(cursor.getColumnIndex("is_parent")) == 0 ? false : true;
				lstProjects.add(project);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getProjects", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 插入项目数据
	 * 
	 * @param lstProject		要插入的项目列表
	 * 
	 * @return					异常
	 */
	public Throwable insertProjects(ArrayList<RMProject> lstProject)
	{
		Throwable ew = null;
		if (lstProject == null)
		{
			return ew;
		}
		String sql = "insert into tbl_project (id, name, identifier, parent, parent_id, description, is_parent, create_date, update_date, statuses)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try
		{
			dataBase.beginTransaction();
			for (int i = 0; i < lstProject.size(); i++)
			{
				RMProject info = lstProject.get(i);
				Integer parentId = info.parent == null ? null : info.parent.id;
				String parentName = info.parent == null ? null : info.parent.name;
				int isParent = info.isParent ? 1 : 0;
				dataBase.execSQL(sql, new Object[]{info.id, info.name, info.identifier, parentId,
						parentName, info.description, isParent, info.created_on, info.updated_on, info.status});
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertProjects", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
		}
		return ew;
	}

	/**
	 * 清除所有项目
	 * 
	 * @return			异常
	 */
	public Throwable clearProjects()
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_project";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--clearProjects", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 根据人员Id获取人员信息
	 * 
	 * @param staff_id			人员id
	 * 
	 * @return					人员信息
	 */
	public RMPairBoolInfo getStaffById(int staff_id)
	{
		String sql = "select id, name from tbl_staffs where id=? group by id";
		Cursor cursor = null;
		RMPairBoolInfo pairInfo = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(staff_id)});
			if (cursor.moveToNext())
			{
				pairInfo = new RMPairBoolInfo();
				pairInfo.id = cursor.getInt(cursor.getColumnIndex("id"));
				pairInfo.name = cursor.getString(cursor.getColumnIndex("name"));
			}
		} catch (Exception e)
		{
			LogUtil.w("RMDBLocal--getStaffById", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return pairInfo;
	}

	/**
	 * 获取所有的人员列表
	 * 	
	 * @param lstStaffs			人员列表
	 * 
	 * @return					异常	
	 */
	public Throwable getAllStaffs(ArrayList<RMPairBoolInfo> lstStaffs)
	{
		Throwable ew = null;
		String sql = "select id, name from tbl_staffs group by id";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				RMPairBoolInfo role = new RMPairBoolInfo();
				role.id = cursor.getInt(cursor.getColumnIndex("id"));
				role.name = cursor.getString(cursor.getColumnIndex("name"));
				lstStaffs.add(role);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getAllStaffByProjectId", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 根据项目Id获取项目关联的人员列表
	 * 	
	 * @param lstStaffs			人员列表			
	 * @param projectId			项目Id
	 * 
	 * @return					异常
	 */
	public Throwable getStaffsByProjectId(ArrayList<RMPairBoolInfo> lstStaffs, int projectId)
	{
		if (projectId < 0)
		{
			return getAllStaffs(lstStaffs);
		}
		Throwable ew = null;
		String sql = "select id, name from tbl_staffs where project_id=? order by sort_order";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(projectId)});
			while (cursor.moveToNext())
			{
				RMPairBoolInfo role = new RMPairBoolInfo();
				role.id = cursor.getInt(cursor.getColumnIndex("id"));
				role.name = cursor.getString(cursor.getColumnIndex("name"));
				lstStaffs.add(role);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getStaffsByProjectId", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 插入人员信息
	 * 
	 * @param lstMembers		人员信息列表
	 * 
	 * @return					异常
	 */
	public Throwable insertStaffs(ArrayList<RMMemberShip> lstMembers)
	{
		Throwable ew = null;
		if (lstMembers == null)
		{
			return ew;
		}
		String sql = "insert into tbl_staffs (id, name, project_id, sort_order) values (?, ?, ?, ?)";
		try
		{
			dataBase.beginTransaction();
			for (int i = 0; i < lstMembers.size(); i++)
			{
				RMMemberShip info = lstMembers.get(i);
				if (info.user != null && info.project != null)
				{
					dataBase.execSQL(sql, new Object[]{info.user.id, info.user.name, info.project.id, i});
				}
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertRoles", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
		}
		return ew;
	}

	/**
	 * 清除所有人员信息
	 * 
	 * @return		异常
	 */
	public Throwable clearStaffs()
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_staffs";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--clearStaffs", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 根据版本Id获取版本信息
	 * 
	 * @param version_id		版本Id
	 * 
	 * @return					版本信息
	 */
	public RMPairBoolInfo getVersionById(int version_id)
	{
		String sql = "select id, name from tbl_version where id=? group by id";
		Cursor cursor = null;
		RMPairBoolInfo pairInfo = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(version_id)});
			if (cursor.moveToNext())
			{
				pairInfo = new RMPairBoolInfo();
				pairInfo.id = cursor.getInt(cursor.getColumnIndex("id"));
				pairInfo.name = cursor.getString(cursor.getColumnIndex("name"));
			}
		} catch (Exception e)
		{
			LogUtil.w("RMDBLocal--getVersionById", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return pairInfo;
	}

	/**
	 * 获取所有的版本信息
	 * 
	 * @param lstVersions		版本信息列表
	 * 
	 * @return					异常
	 */
	public Throwable getAllVersions(ArrayList<RMPairBoolInfo> lstVersions)
	{
		Throwable ew = null;
		String sql = "select id, name from tbl_version group by id";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				RMPairBoolInfo version = new RMPairBoolInfo();
				version.id = cursor.getInt(cursor.getColumnIndex("id"));
				version.name = cursor.getString(cursor.getColumnIndex("name"));
				lstVersions.add(version);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getAllVersions", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 根据项目Id获取项目关联的版本信息列表
	 * 
	 * @param lstVersions		版本信息列表
	 * @param projectId			项目Id
	 * 
	 * @return					异常
	 */
	public Throwable getVersionsByProjectId(ArrayList<RMPairBoolInfo> lstVersions, int projectId)
	{
		if (projectId < 0)
		{
			return getAllVersions(lstVersions);
		}
		Throwable ew = null;
		String sql = "select id, name, project_name, description, status, sharing, created_on,"
				+ "updated_on from tbl_version where project_id=?";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(projectId)});
			while (cursor.moveToNext())
			{
				RMPairBoolInfo version = new RMPairBoolInfo();
				version.id = cursor.getInt(cursor.getColumnIndex("id"));
				version.name = cursor.getString(cursor.getColumnIndex("name"));
				lstVersions.add(version);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getVersionsByProjectId", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 插入版本信息
	 * 
	 * @param lstVersions		需要插入的版本列表
	 * @return
	 */
	public Throwable insertVersions(ArrayList<RMVersion> lstVersions)
	{
		Throwable ew = null;
		if (lstVersions == null)
		{
			return ew;
		}
		String sql = "insert into tbl_version (id, name, project_id, project_name, description, "
				+ "status, sharing, created_on, updated_on ) values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try
		{
			dataBase.beginTransaction();
			for (int i = 0; i < lstVersions.size(); i++)
			{
				RMVersion info = lstVersions.get(i);
				if (info.project != null)
				{
					dataBase.execSQL(sql, new Object[]{info.id, info.name, info.project.id, info.project.name,
							info.description, info.status, info.sharing, info.created_on, info.updated_on});
				}
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertVersions", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
		}
		return ew;
	}

	/**
	 * 清除所有版本信息
	 * 	
	 * @return			异常
	 */
	public Throwable clearVersions()
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_version";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--clearVersions", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 根据跟踪状态Id获取跟踪状态
	 * 
	 * @param tracker_id		跟踪状态Id
	 * 
	 * @return					跟踪状态信息
	 */
	public RMPairBoolInfo getTrackerById(int tracker_id)
	{
		String sql = "select id, name from tbl_trackers where id=? group by id";
		Cursor cursor = null;
		RMPairBoolInfo pairInfo = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(tracker_id)});
			if (cursor.moveToNext())
			{
				pairInfo = new RMPairBoolInfo();
				pairInfo.id = cursor.getInt(cursor.getColumnIndex("id"));
				pairInfo.name = cursor.getString(cursor.getColumnIndex("name"));
			}
		} catch (Exception e)
		{
			LogUtil.w("RMDBLocal--getTrackerById", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return pairInfo;
	}

	/**
	 * 获取所有的跟踪状态
	 * 
	 * @param lstTrackers			跟踪状态列表
	 * 
	 * @return						异常
	 */
	public Throwable getAllTrackers(ArrayList<RMPairBoolInfo> lstTrackers)
	{
		Throwable ew = null;
		String sql = "select id, name from tbl_trackers group by id";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				RMPairBoolInfo info = new RMPairBoolInfo();
				info.id = cursor.getInt(cursor.getColumnIndex("id"));
				info.name = cursor.getString(cursor.getColumnIndex("name"));
				lstTrackers.add(info);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getAllTrackers", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 根据项目Id获取项目关联的所有的跟踪状态列表
	 * 
	 * @param lstTrackers			跟踪状态列表
	 * @param projectId				项目Id
	 * 
	 * @return						异常
	 */
	public Throwable getTrackersByProjectId(ArrayList<RMPairBoolInfo> lstTrackers, int projectId)
	{
		if (projectId < 0)
		{
			return getAllTrackers(lstTrackers);
		}
		Throwable ew = null;
		String sql = "select id, name from tbl_trackers where project_id=? order by sort_order";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(projectId)});
			while (cursor.moveToNext())
			{
				RMPairBoolInfo info = new RMPairBoolInfo();
				info.id = cursor.getInt(cursor.getColumnIndex("id"));
				info.name = cursor.getString(cursor.getColumnIndex("name"));
				lstTrackers.add(info);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getTrackersByProjectId", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 插入跟踪状态
	 * 
	 * @param lstTrackers		要插入的跟踪列表
	 * @param projectId			跟踪状态关联的项目Id
	 * 
	 * @return					异常
	 */
	public Throwable insertTrackers(ArrayList<RMPairInfo> lstTrackers, int projectId)
	{
		Throwable ew = null;
		if (lstTrackers == null)
		{
			return ew;
		}
		String sql = "insert into tbl_trackers (id, name, project_id, sort_order) values (?, ?, ?, ?)";
		try
		{
			dataBase.beginTransaction();
			for (int i = 0; i < lstTrackers.size(); i++)
			{
				RMPairInfo info = lstTrackers.get(i);
				dataBase.execSQL(sql, new Object[]{info.id, info.name, projectId, i});
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertTrackers", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
		}
		return ew;
	}

	/**
	 * 清除所有的跟踪状态
	 * 
	 * @return			异常
	 */
	public Throwable clearTrackers()
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_trackers";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--clearTrackers", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 根据类别Id获取类别信息
	 * 
	 * @param issueCategory_id		类别Id
	 * 
	 * @return						类别信息
	 */
	public RMPairBoolInfo getIssueCategoryById(int issueCategory_id)
	{
		String sql = "select id, name from tbl_issue_category where id=? group by id";
		Cursor cursor = null;
		RMPairBoolInfo pairInfo = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(issueCategory_id)});
			if (cursor.moveToNext())
			{
				pairInfo = new RMPairBoolInfo();
				pairInfo.id = cursor.getInt(cursor.getColumnIndex("id"));
				pairInfo.name = cursor.getString(cursor.getColumnIndex("name"));
			}
		} catch (Exception e)
		{
			LogUtil.w("RMDBLocal--getIssueCategoryId", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return pairInfo;
	}

	/**
	 * 获取所有的类别信息
	 * 
	 * @param lstIssueCategorys		类别列表
	 * 
	 * @return						异常
	 */
	public Throwable getAllIssueCategorys(ArrayList<RMPairBoolInfo> lstIssueCategorys)
	{
		Throwable ew = null;
		String sql = "select id, name from tbl_issue_category group by id";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				RMPairBoolInfo info = new RMPairBoolInfo();
				info.id = cursor.getInt(cursor.getColumnIndex("id"));
				info.name = cursor.getString(cursor.getColumnIndex("name"));
				lstIssueCategorys.add(info);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getAllIssueCategorys", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 根据项目Id获取项目关联的类别列表
	 * 
	 * @param lstIssueCategorys			类别列表
	 * @param projectId					项目Id
	 * 
	 * @return
	 */
	public Throwable getIssueCategorysByProjectId(ArrayList<RMPairBoolInfo> lstIssueCategorys, int projectId)
	{
		if (projectId < 0)
		{
			return getAllIssueCategorys(lstIssueCategorys);
		}
		Throwable ew = null;
		String sql = "select id, name, project_name"
				+ " from tbl_issue_category where project_id=? order by sort_order";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(projectId)});
			while (cursor.moveToNext())
			{
				RMPairBoolInfo info = new RMPairBoolInfo();
				info.id = cursor.getInt(cursor.getColumnIndex("id"));
				info.name = cursor.getString(cursor.getColumnIndex("name"));
				lstIssueCategorys.add(info);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getIssueCategorysByProjectId", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 插入问题类别
	 * 
	 * @param lstIssueCategories		问题类别列表
	 * @param projId					项目Id
	 * @param projName					项目名称
	 * 
	 * @return							异常
	 */
	public Throwable insertIssueCategorys(ArrayList<RMPairInfo> lstIssueCategories, int projId, String projName)
	{
		Throwable ew = null;
		if (lstIssueCategories == null)
		{
			return ew;
		}
		String sql = "replace into tbl_issue_category (id, name, project_id, project_name, sort_order) values (?, ?, ?, ?, ?)";
		try
		{
			dataBase.beginTransaction();
			for (int i = 0; i < lstIssueCategories.size(); i++)
			{
				RMPairInfo info = lstIssueCategories.get(i);
				dataBase.execSQL(sql, new Object[]{info.id, info.name, projId, projName, i});
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertIssueCategorys", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
		}
		return ew;
	}

	/**
	 * 清除所有的问题类别
	 * 
	 * @return			异常
	 */
	public Throwable clearIssueCategory()
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_issue_category";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--clearIssueCategory", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 根据优先级Id获取优先级信息
	 * 
	 * @param prioritie_id		优先级Id
	 * 
	 * @return					优先级信息
	 */
	public RMPairBoolInfo getPrioritieById(int prioritie_id)
	{
		String sql = "select id, name, is_default from tbl_issue_priorities where id=?";
		Cursor cursor = null;
		RMPairBoolInfo pairInfo = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(prioritie_id)});
			if (cursor.moveToNext())
			{
				pairInfo = new RMPairBoolInfo();
				pairInfo.id = cursor.getInt(cursor.getColumnIndex("id"));
				pairInfo.name = cursor.getString(cursor.getColumnIndex("name"));
				pairInfo.bool1 = cursor.getInt(cursor.getColumnIndex("is_default")) == 0 ? false : true;
			}
		} catch (Exception e)
		{
			LogUtil.w("RMDBLocal--getPrioritieById", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return pairInfo;
	}

	/**
	 * 获取所有的问题优先级信息
	 * 
	 * @param lstPriorities			问题优先级列表
	 * 
	 * @return						异常
	 */
	public Throwable getAllPriorities(ArrayList<RMPairBoolInfo> lstPriorities)
	{
		Throwable ew = null;
		String sql = "select id, name, is_default from tbl_issue_priorities order by sort_order";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				RMPairBoolInfo info = new RMPairBoolInfo();
				info.id = cursor.getInt(cursor.getColumnIndex("id"));
				info.name = cursor.getString(cursor.getColumnIndex("name"));
				info.bool1 = cursor.getInt(cursor.getColumnIndex("is_default")) == 0 ? false : true;
				lstPriorities.add(info);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getAllPriorities", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 插入问题优先级数据
	 * 
	 * @param lstPriorities			要插入的问题优先级列表
	 * 
	 * @return						异常
	 */
	public Throwable insertPriorities(ArrayList<RMPrioritie> lstPriorities)
	{
		Throwable ew = null;
		if (lstPriorities == null)
		{
			return ew;
		}
		String sql = "insert into tbl_issue_priorities (id, name, is_default, sort_order) values (?, ?, ?, ?)";
		try
		{
			dataBase.beginTransaction();
			for (int i = 0; i < lstPriorities.size(); i++)
			{
				RMPrioritie info = lstPriorities.get(i);
				int default_int = info.is_default ? 1 : 0;
				dataBase.execSQL(sql, new Object[]{info.id, info.name, default_int, i});
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertPriorities", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
		}
		return ew;
	}

	/**
	 * 清除所有的问题优先级
	 * 
	 * @return			异常
	 */
	public Throwable clearPriorities()
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_issue_priorities";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--clearPriorities", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 根据问题状态Id获取问题状态信息
	 * 
	 * @param issueStatus_id			状态Id
	 * 
	 * @return							异常	
	 */
	public RMPairBoolInfo getIssueStatusById(int issueStatus_id)
	{
		String sql = "select id, name, is_default, is_closed from tbl_issue_status where id=?";
		Cursor cursor = null;
		RMPairBoolInfo pairInfo = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(issueStatus_id)});
			if (cursor.moveToNext())
			{
				pairInfo = new RMPairBoolInfo();
				pairInfo.id = cursor.getInt(cursor.getColumnIndex("id"));
				pairInfo.name = cursor.getString(cursor.getColumnIndex("name"));
				pairInfo.bool1 = cursor.getInt(cursor.getColumnIndex("is_default")) == 0 ? false : true;
			}
		} catch (Exception e)
		{
			LogUtil.w("RMDBLocal--getIssueStatusById", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return pairInfo;
	}

	/**
	 * 获取所有的问题状态信息
	 * 
	 * @param lstIssueStatus		状态信息列表
	 * 					
	 * @return						列表
	 */
	public Throwable getAllIssueStatus(ArrayList<RMPairBoolInfo> lstIssueStatus)
	{
		Throwable ew = null;
		String sql = "select id, name, is_default, is_closed from tbl_issue_status order by sort_order";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				RMPairBoolInfo info = new RMPairBoolInfo();
				info.id = cursor.getInt(cursor.getColumnIndex("id"));
				info.name = cursor.getString(cursor.getColumnIndex("name"));
				info.bool1 = cursor.getInt(cursor.getColumnIndex("is_default")) == 0 ? false : true;
				info.bool2 = cursor.getInt(cursor.getColumnIndex("is_closed")) == 0 ? false : true;
				lstIssueStatus.add(info);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getAllIssueStatus", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 插入问题状态信息
	 * 
	 * @param lstIssueStatus		要插入的问题状态列表
	 * 
	 * @return						异常
	 */
	public Throwable insertIssueStatus(ArrayList<RMIssueStatus> lstIssueStatus)
	{
		Throwable ew = null;
		if (lstIssueStatus == null)
		{
			return ew;
		}
		String sql = "insert into tbl_issue_status (id, name, is_default, is_closed, sort_order) values (?, ?, ?, ?, ?)";
		try
		{
			dataBase.beginTransaction();
			for (int i = 0; i < lstIssueStatus.size(); i++)
			{
				RMIssueStatus info = lstIssueStatus.get(i);
				int isDefault = info.is_default ? 1 : 0;
				int isClosed = info.is_closed ? 1 : 0;
				dataBase.execSQL(sql, new Object[]{info.id, info.name, isDefault, isClosed, i});
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertIssueStatus", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
		}
		return ew;
	}

	/**
	 * 清除问题状态数据
	 * 
	 * @return			异常
	 */
	public Throwable clearIssueStatus()
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_issue_status";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--clearIssueStatus", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 清除自定义过滤器
	 * 
	 * @return			异常
	 */
	public Throwable clearIssueSelfFilter()
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_issue_self_filter";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--clearIssueSelfFilter", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 根据Id删除自定义过滤器信息
	 * 
	 * @param id			自定义过滤器Id
	 * 
	 * @return				异常
	 */
	public Throwable deleteIssueSelfFilter(int id)
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_issue_self_filter where id=?";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{id});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--deleteIssueSelfFilter", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 获取所有的自定义过滤器列表
	 * 
	 * @param lstSelfFilter				自定义过滤器列表
	 * 
	 * @return							异常		
	 */
	public Throwable getAllIssueSelfFilter(ArrayList<RMIssueFilter> lstSelfFilter)
	{
		Throwable ew = null;
		String sql = "select id, name, project_id, status_id,tracker_id,assigned_to_id,priority_id,"
				+ "fixed_version_id,category_id, cloumn, sort from tbl_issue_self_filter";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				RMIssueFilter info = new RMIssueFilter();
				info.id = cursor.getInt(cursor.getColumnIndex("id"));
				info.name = cursor.getString(cursor.getColumnIndex("name"));
				info.project_id = cursor.getInt(cursor.getColumnIndex("project_id"));
				info.status_id = cursor.getInt(cursor.getColumnIndex("status_id"));
				info.tracker_id = cursor.getInt(cursor.getColumnIndex("tracker_id"));
				info.assigned_to_id = cursor.getInt(cursor.getColumnIndex("assigned_to_id"));
				info.priority_id = cursor.getInt(cursor.getColumnIndex("priority_id"));
				info.fixed_version_id = cursor.getInt(cursor.getColumnIndex("fixed_version_id"));
				info.category_id = cursor.getInt(cursor.getColumnIndex("category_id"));
				info.cloumn = cursor.getString(cursor.getColumnIndex("cloumn"));
				info.sort = cursor.getString(cursor.getColumnIndex("sort"));
				lstSelfFilter.add(info);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getAllIssueSelfFilter", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 插入自定义过滤器
	 * 
	 * @param issueFilter	自定义过滤器信息
	 * 
	 * @return				异常
	 */
	public Throwable insertIssueSelfFilter(RMIssueFilter issueFilter)
	{
		Throwable ew = null;
		if (issueFilter == null)
		{
			return ew;
		}
		String sqlSelect = "select * from tbl_issue_self_filter where id=?";
		String sqlInsert = "insert into tbl_issue_self_filter (name, project_id, status_id,tracker_id,assigned_to_id,priority_id,"
				+ "fixed_version_id,category_id, cloumn, sort) values (?, ?, ?, ?, ?,?, ?, ?, ?, ?)";
		String sqlUpdate = "update tbl_issue_self_filter set name=?, project_id=?, status_id=?,tracker_id=?,assigned_to_id=?,priority_id=?,"
				+ "fixed_version_id=?,category_id=?, cloumn=?, sort=? where id=?";
		Cursor cursor = null;
		try
		{
			dataBase.beginTransaction();
			cursor = dataBase.rawQuery(sqlSelect, new String[]{String.valueOf(issueFilter.id)});
			if (cursor.moveToNext())
			{
				dataBase.execSQL(sqlUpdate, new Object[]{issueFilter.name, issueFilter.project_id, issueFilter.status_id,
						issueFilter.tracker_id, issueFilter.assigned_to_id, issueFilter.priority_id,
						issueFilter.fixed_version_id, issueFilter.category_id, issueFilter.cloumn, issueFilter.sort, issueFilter.id});
			} else
			{
				dataBase.execSQL(sqlInsert, new Object[]{issueFilter.name, issueFilter.project_id, issueFilter.status_id,
						issueFilter.tracker_id, issueFilter.assigned_to_id, issueFilter.priority_id,
						issueFilter.fixed_version_id, issueFilter.category_id, issueFilter.cloumn, issueFilter.sort});
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertIssueSelfFilter", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 获取新问题的条数
	 * 
	 * @return			新问题条数
	 */
	public int getUpdatedIssueCount()
	{
		String sql = "select count(*) count from tbl_issue_update";
		int count = 0;
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			if (cursor.moveToNext())
			{
				count = cursor.getInt(cursor.getColumnIndex("count"));
			}
		} catch (Exception e)
		{
			LogUtil.w("RMDBLocal--getAllUpdatedIssueId", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return count;
	}

	/**
	 * 获取所有新问题的Id列表
	 * 
	 * @param lstPairId			新问题Id列表
	 * 
	 * @return					异常
	 */
	public Throwable getAllUpdatedIssueId(ArrayList<Integer> lstPairId)
	{
		Throwable ew = null;
		String sql = "select issue_id from tbl_issue_update";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				int int1 = cursor.getInt(cursor.getColumnIndex("issue_id"));
				lstPairId.add(int1);
			}
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--getAllUpdatedIssueId", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return ew;
	}

	/**
	 * 插入新问题
	 * 
	 * @param lstIssues			新问题列表
	 * 
	 * @return					异常
	 */
	public Throwable insertUpdatedIssueId(ArrayList<RMIssue> lstIssues)
	{
		if (lstIssues == null || lstIssues.size() == 0)
		{
			return null;
		}
		
		Throwable ew = null;
		String sql = "insert into tbl_issue_update (issue_id) select (?) where not exists (select issue_id from tbl_issue_update where issue_id = ?)";
		try
		{
			dataBase.beginTransaction();
			for (int i = 0; i < lstIssues.size(); i++)
			{
				RMIssue info = lstIssues.get(i);
				dataBase.execSQL(sql, new Object[]{info.id});
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertUpdatedIssueId", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
		}
		return ew;
	}

	/**
	 * 根据Id删除新问题
	 * 	
	 * @param id		要删除的新问题Id
	 * @return
	 */
	public Throwable deleteUpdatedIssueId(int id)
	{
		Throwable ew = null;
		String sqlDel = "delete from tbl_issue_update where issue_id=?";
		try
		{
			dataBase.execSQL(sqlDel, new Object[]{id});
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--deleteUpdatedIssueId", LogUtil.ExceptionToString(e));
		}
		return ew;
	}

	/**
	 * 插入 子项显示数据
	 * 
	 * @param lstShowItem		子项显示列表
	 * 
	 * @return					异常	
	 */
	public Throwable insertShowItem(ArrayList<RMShowItem> lstShowItem)
	{
		Throwable ew = null;
		String sql = "replace into tbl_issue_show_item (project_id, track, priority, done_ratio, "
				+ "start_date, due_date, fixed_version, category) values (?,?,?,?,?,?,?,?)";

		try
		{
			dataBase.beginTransaction();
			for (int i = 0; i < lstShowItem.size(); i++)
			{
				RMShowItem info = lstShowItem.get(i);
				dataBase.execSQL(sql, new Object[]{info.project_id, info.track ? 1 : 0, info.priority ? 1 : 0,
						info.done_ratio ? 1 : 0, info.start_date ? 1 : 0, info.due_date ? 1 : 0,
						info.fixed_version ? 1 : 0, info.category ? 1 : 0});
			}
			dataBase.setTransactionSuccessful();
		} catch (Exception e)
		{
			ew = e;
			LogUtil.w("RMDBLocal--insertShowItem", LogUtil.ExceptionToString(e));
		} finally
		{
			dataBase.endTransaction();
		}
		return ew;
	}

	/**
	 * 根据项目Id获取项目关联的子项显示信息
	 * 
	 * @param projectId			项目Id
	 * 
	 * @return					子项显示信息
	 */
	public RMShowItem getShowItemByProjectId(int projectId)
	{
		RMShowItem info = null;
		String sql = "select track, priority, done_ratio, start_date, due_date, fixed_version, category from tbl_issue_show_item where project_id=?";
		Cursor cursor = null;
		try
		{
			cursor = dataBase.rawQuery(sql, new String[]{String.valueOf(projectId)});
			if (cursor.moveToNext())
			{
				info = new RMShowItem();
				info.project_id = projectId;
				info.track = cursor.getInt(cursor.getColumnIndex("track")) == 0 ? false : true;
				info.priority = cursor.getInt(cursor.getColumnIndex("priority")) == 0 ? false : true;
				info.done_ratio = cursor.getInt(cursor.getColumnIndex("done_ratio")) == 0 ? false : true;
				info.start_date = cursor.getInt(cursor.getColumnIndex("start_date")) == 0 ? false : true;
				info.due_date = cursor.getInt(cursor.getColumnIndex("due_date")) == 0 ? false : true;
				info.category = cursor.getInt(cursor.getColumnIndex("category")) == 0 ? false : true;
				info.fixed_version = cursor.getInt(cursor.getColumnIndex("fixed_version")) == 0 ? false : true;
			}
		} catch (Exception e)
		{
			LogUtil.w("RMDBLocal--getShowItemByProjectId", LogUtil.ExceptionToString(e));
		} finally
		{
			if (cursor != null)
				cursor.close();
		}
		return info;
	}
}
