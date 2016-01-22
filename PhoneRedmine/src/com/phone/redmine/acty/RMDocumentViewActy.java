package com.phone.redmine.acty;

import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.phone.redmine.R;
import com.phone.redmine.biz.RMConst;
import com.phone.redmine.entity.FCException;
import com.phone.redmine.entity.RMPairStringInfo;
import com.phone.redmine.http.RMHttpUrl;
import com.phone.redmine.util.SharePrefUtil;
import com.phone.redmine.view.FCProgressbar;
import com.phone.redmine.view.crouton.FCCrouton;

/**
 * RM 查看文档
 */
public class RMDocumentViewActy extends BaseSecLevelActy implements OnClickListener
{

	private TextView			tvTitle;
	private TextView			tvSummary;
	private TextView			tvDescription;
	private LinearLayout		llAttachment;
	private RMDocumentHtmlInfo	documentInfo;	//列表接口过来的新闻信息， 是全的
	private int					documentId;	//搜索过来的ID， 需要从网页抓取详细信息

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rmacty_documentview);
		setActionbarTitle("文档");
		if (savedInstanceState != null)
		{
			documentId = savedInstanceState.getInt("documentId");
		}
		if (documentId == 0)
		{
			documentId = getIntent().getIntExtra("documentId", 0);
		}
		
		initRes();
		
		if (documentId > 0)
		{
			documentInfo = new RMDocumentHtmlInfo();
			new GetDocumentFromHtmlAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, documentId);
		}
		
	}

	@Override
	public void onClick(View v)
	{
		int vId = v.getId();
		if (vId >= 200 && vId < 300)
		{
			RMPairStringInfo attach = documentInfo.lstAttchments.get(vId - 200);
			String url = RMHttpUrl.URLHEAD_HTTP + attach.str2;

			String strFileSize = "";
			int i1 = attach.str1.lastIndexOf("(");
			int i2 = attach.str1.lastIndexOf(")");
			if (i1 > 0 && i2 > 0 && i1 < i2)
			{
				strFileSize = attach.str1.substring(i1 + 1, i2);
			}

			int i = attach.str2.lastIndexOf("/");
			String fileName;
			if (i > 0 && i < attach.str2.length())
			{
				fileName = attach.str2.substring(i + 1);
				RMIssueViewActy.openAttachment(this, url, fileName, strFileSize);
			} else
			{
				FCCrouton.error(this, "无法解析文件");
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		if (documentId > 0)
		{
			outState.putInt("documentId", documentId);
		}
	}

	private void initRes()
	{
		tvDescription = (TextView) findViewById(R.id.rmactydocument_description);
		tvSummary = (TextView) findViewById(R.id.rmactydocument_summary);
		tvTitle = (TextView) findViewById(R.id.rmactydocument_title);
		llAttachment = (LinearLayout) findViewById(R.id.rmactydocument_attchment);
	}

	private void initData()
	{
		if (documentInfo != null)
		{
			tvTitle.setText(documentInfo.title);
			tvSummary.setText(documentInfo.summary);
			if (documentInfo.description != null)
			{
				tvDescription.setText(Html.fromHtml(documentInfo.description));
			}

			llAttachment.removeAllViews();
			for (int i = 0; i < documentInfo.lstAttchments.size(); i++)
			{
				RMPairStringInfo info = documentInfo.lstAttchments.get(i);
				TextView tv = new AppCompatTextView(this);
				tv.setBackgroundResource(R.drawable.common_listitem_cango_tc);
				tv.setGravity(Gravity.CENTER_VERTICAL);
				tv.setText("附件     " + info.str1);
				tv.setId(200 + i);//在onClick中会使用
				tv.setTextColor(getResources().getColor(R.color.darkblue));
				tv.setOnClickListener(this);
				llAttachment.addView(tv);
			}
		}
	}

	/*
	 * 异步获取文档信息
	 */
	private class GetDocumentFromHtmlAsyncTask extends AsyncTask<Integer,Integer,Integer>
	{

		private FCProgressbar	processBar;

		public GetDocumentFromHtmlAsyncTask ()
		{
			processBar = new FCProgressbar(RMDocumentViewActy.this);
		}

		@Override
		protected void onPreExecute()
		{
			processBar.showBar();
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			processBar.hideBar();
			initData();
		}

		@Override
		protected Integer doInBackground(Integer... params)
		{
			int result = -1;//0是列表,其他是问题
			String cookie = SharePrefUtil.getInstance().getString(RMConst.SHARE_REDMINECOOKIE, null);
			if (cookie != null)
			{
				try
				{
					String newsUrl = "documents/" + params[0];
					Response res = Jsoup.connect(RMHttpUrl.URLHEAD_REDMINE + newsUrl).timeout(50000).header("Cookie", cookie).execute();
					Document doc = res.parse();
					
					/*如果到了登录页面，直接跳出，并且移除cookie*/
					Elements etlogin = doc.select("label[for=username]");
					if (etlogin.size() > 0)
					{
						throw new FCException();
					}
					
					Element e1 = doc.select("h2").first();
					documentInfo.title = e1.text();
					Element e2 = doc.select("em").first();
					documentInfo.summary = e2.text();
					Element e3 = doc.select("div[class=wiki]").first();
					documentInfo.description = e3.html();
					Elements e5s = doc.select("a[class=icon icon-attachment]");
					for (int i = 0; i < e5s.size(); i++)
					{
						Element e5 = e5s.get(i);
						if (e5 != null)
						{
							RMPairStringInfo pairInfo = new RMPairStringInfo();
							pairInfo.str1 = e5.text();
							pairInfo.str2 = e5.attr("href");
							Element e6 = e5.nextElementSibling();
							pairInfo.str1 += e6.text();
							documentInfo.lstAttchments.add(pairInfo);
						}
					}
				} catch (IOException e)
				{
					FCCrouton.error(RMDocumentViewActy.this, "网络连接出错！");
					e.printStackTrace();
				} catch (FCException e)
				{
					SharePrefUtil.getInstance().removeString(RMConst.SHARE_REDMINECOOKIE);
					FCCrouton.error(RMDocumentViewActy.this, "Cookie失效，已重置，请重试！");
				}
			}
			return result;
		}
	}

	/*
	 * 文档详情实体 
	 */
	private class RMDocumentHtmlInfo
	{

		public String						title;													//标题
		public String						summary;												//新闻描述
		public String						description;											//新闻内容
		public ArrayList<RMPairStringInfo>	lstAttchments	= new ArrayList<RMPairStringInfo>();
	}
}
