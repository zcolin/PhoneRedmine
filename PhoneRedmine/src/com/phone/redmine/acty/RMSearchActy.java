package com.phone.redmine.acty;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.adapter.RMSearchAdapter;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.biz.RMLoginWebAsyncTask.OnLoginComplete;
import com.phone.redmine.entity.FCException;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.util.DeviceUtil;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.util.StringUtil;
import com.phone.redmine.view.FCProgressbar;
import com.phone.redmine.view.crouton.FCCrouton;

/**
 * RM搜索页面 
 */
public class RMSearchActy extends BaseSecLevelActy implements OnClickListener, OnItemClickListener
{

	private EditText				etSearch;
	private ListView				lstView;
	private RMSearchAdapter			queryAdapter;
	private ArrayList<SearchResult>	lstSearchResult	= new ArrayList<SearchResult>();
	private TextView				emptyTv;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmacty_search);
		setActionbarTitle("问题搜索");
		initRes();
	}

	private void initRes()
	{
		findViewById(R.id.rmactysearch_btn_search).setOnClickListener(this);
		findViewById(R.id.rmactysearch_btn_search).setOnClickListener(this);
		etSearch = (EditText) findViewById(R.id.rmactysearch_et_search);
		lstView = (ListView) findViewById(R.id.rmactysearch_lstview);
		lstView.setOnItemClickListener(this);
		//设置空view
		emptyTv = (TextView) findViewById(R.id.rmactysearch_emptytext);
		lstView.setEmptyView(emptyTv);
		DeviceUtil.showSoftKeyBoardDelay(this, etSearch);
	}

	private void notifyData()
	{
		if (lstSearchResult.size() > 0)
		{
			emptyTv.setText("");
		} else
		{
			emptyTv.setText("没有搜索到结果");
		}
		if (queryAdapter == null)
		{
			queryAdapter = new RMSearchAdapter(this, lstSearchResult);
			lstView.setAdapter(queryAdapter);
		} else
		{
			queryAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.rmactysearch_btn_search:
				preSearch();
				break;
			default:
				break;
		}
	}

	private void preSearch()
	{
		final String str = etSearch.getText().toString().trim();
		if (StringUtil.isBlank(str))
		{
			FCCrouton.alert(this, "搜索字段不能为空！");
			return;
		}
		String cookie = SharePrefUtil.getInstance().getString(RMConst.SHARE_REDMINECOOKIE, null);
		if (cookie == null)
		{
			InitActy.loginRedmineWeb(RMSearchActy.this, RMSearchActy.this, new OnLoginComplete()
			{

				@Override
				public void loginComplete(String faiReason)
				{
					if (faiReason == null)
					{
						new SearchIssueAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, str);
					} else
					{
						FCCrouton.error(RMSearchActy.this, faiReason);
					}
				}
			});
		} else
		{
			new SearchIssueAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, str);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		if (position < lstSearchResult.size())
		{
			SearchResult info = lstSearchResult.get(position);
			if (info.type == 0)
			{
				Intent intent = new Intent();
				intent.putExtra("issueId", info.infoId);
				intent.setClass(this, RMIssueViewActy.class);
				startActivity(intent);
			} else if (info.type == 1)
			{
				Intent intent = new Intent();
				intent.putExtra("newsId", info.infoId);
				intent.setClass(this, RMNewsViewActy.class);
				startActivity(intent);
			} else if (info.type == 2)
			{
				Intent intent = new Intent();
				intent.putExtra("documentId", info.infoId);
				intent.setClass(this, RMDocumentViewActy.class);
				startActivity(intent);
			} else
			{
				FCCrouton.error(this, "类型没有找到！");
			}

		}
	}

	/**
	 *  异步执行搜索 
	 */
	private class SearchIssueAsyncTask extends AsyncTask<String,Integer,Integer>
	{

		private FCProgressbar	processBar;

		public SearchIssueAsyncTask ()
		{
			processBar = new FCProgressbar(RMSearchActy.this);
		}

		@Override
		protected void onPreExecute()
		{
			lstSearchResult.clear();
			processBar.showBar();
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			processBar.hideBar();
			notifyData();
			DeviceUtil.hideKeyBoard(RMSearchActy.this, etSearch);

			if (result > 0)
			{
				Intent intent = new Intent();
				intent.putExtra("issueId", result);
				intent.setClass(RMSearchActy.this, RMIssueViewActy.class);
				startActivity(intent);
			}
		}

		@Override
		protected Integer doInBackground(String... params)
		{
			int result = -1;//0是列表,其他是问题
			String cookie = SharePrefUtil.getInstance().getString(RMConst.SHARE_REDMINECOOKIE, null);
			if (cookie != null)
			{
				try
				{
					String str = URLEncoder.encode(params[0], "utf-8");
					String serachUrl = "search?utf8=%E2%9C%93&scope=all&all_words=&all_words=1&titles_only=&issues=1&news=1&documents=1&q=" + str;
					Response res = Jsoup.connect(RMHttpUrl.URLHEAD_REDMINE + serachUrl).timeout(50000).header("Cookie", cookie).execute();
					Document doc = res.parse();
					
					/*如果到了登录页面，直接跳出，并且移除cookie*/
					Elements etlogin = doc.select("label[for=username]");
					if (etlogin.size() > 0)
					{
						throw new FCException();
					}

					/*以此判断为搜索返回的的是单个问题*/
					Elements etissue = doc.select("a[class=icon icon-del]");
					if (etissue.size() > 0)	//单个记录
					{
						Element et = etissue.first();
						if (et != null)
						{
							String strId = et.attr("href");
							if (strId != null)
							{
								int index = strId.lastIndexOf("/");
								if (index < strId.length())
								{
									try
									{
										result = Integer.parseInt(strId.substring(index + 1));
									} catch (Exception e)
									{
									}
								}
							}
						}
					} else		//列表
					{
						Elements ets = doc.getElementsByTag("dt");
						for (int i = 0; i < ets.size(); i++)
						{
							SearchResult info = new SearchResult();
							Element et = ets.get(i);
							Element e1 = et.select("span").first();
							Element e2 = et.select("a").first();
							info.project = e1.text();
							info.strTitle = e2.text();
							String herf = e2.attr("href");
							if (StringUtil.isNotEmpty(herf))
							{
								//根据链接判断列表的类型
								if (herf.contains("news"))
								{
									info.type = 1;
								} else if (herf.contains("documents"))
								{
									info.type = 2;
								} else if (herf.contains("issues"))
								{
									info.type = 0;
								} else
								{
									info.type = -1;
								}

								//获取ID
								int index = herf.lastIndexOf("/");
								if (index < herf.length())
								{
									try
									{
										info.infoId = Integer.parseInt(herf.substring(index + 1));
									} catch (Exception e)
									{
									}
								}
							}
							Element e3 = et.nextElementSibling();
							if (e3 != null && "dd".equals(e3.nodeName()))
							{
								Element e4 = e3.select("span[class=description]").first();
								Element e5 = e3.select("span[class=author]").first();
								info.description = e4.text();
								info.time = e5.text();
							}
							lstSearchResult.add(info);
						}
						result = 0;
					}

				} catch (IOException e)
				{
					FCCrouton.error(RMSearchActy.this, "网络连接出错！");
					e.printStackTrace();
				} catch (FCException e)
				{
					SharePrefUtil.getInstance().removeString(RMConst.SHARE_REDMINECOOKIE);
					FCCrouton.info(RMSearchActy.this, "Cookie失效，已重置，请重试！");
				}
			}
			return result;
		}
	}

	/**
	 * 搜索结果 信息
	 */
	public class SearchResult
	{

		public String	project;
		public String	strTitle;
		public String	description;
		public String	time;
		public int		infoId;
		public int		type;			//0 问题 1新闻 2文档
	}
}
