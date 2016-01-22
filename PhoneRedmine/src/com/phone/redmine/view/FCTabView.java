package com.phone.redmine.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.phone.redmine.R;

/**
 * 自定义上有横条动画的TabView
 */
public class FCTabView extends RelativeLayout implements OnClickListener, OnPageChangeListener
{

	private DisplayMetrics			dm		= new DisplayMetrics();
	private ImageView				tabLine;						//横条View
	private LinearLayout			llTabLay;						//盛放TabView的容器
	private int						tabWidth;						//每个Tab的宽度
	private int						curTab	= 0;					//当前停留的Tab Index
	private FCTabListener			tabListener;					//tab切换时回调接口
	private OnPageChangeListener	pagerChangeListener;			//ViewPager切换时回调
	private ViewPager				pager;							//盛放内容的ViewPager
	private Matrix					matrix	= new Matrix();
	private Context					ctx;

	public FCTabView (Context context)
	{
		this(context, null);
	}

	public FCTabView (Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.ctx = context;
		LayoutInflater.from(context).inflate(R.layout.common_fctabview, this);
		llTabLay = (LinearLayout) findViewById(R.id.acty_tab_ll);
		tabLine = (ImageView) findViewById(R.id.acty_tab_tabLine);
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
	}

	@Override
	public void onClick(View v)
	{
		if (v instanceof FCTab)
		{
			if (pager != null)
			{
				pager.setCurrentItem(((FCTab) v).tabIndex);
			}

			if (tabListener != null)
			{
				tabListener.onTabSelected((FCTab) v, ((FCTab) v).tabIndex);
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0)
	{
		if (pagerChangeListener != null)
		{
			pagerChangeListener.onPageScrollStateChanged(arg0);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
		tabLineScoller(arg0, arg1);;
		if (pagerChangeListener != null)
		{
			pagerChangeListener.onPageScrolled(arg0, arg1, arg2);
		}
	}

	@Override
	public void onPageSelected(int arg0)
	{
		selectTab(arg0);
		if (pagerChangeListener != null)
		{
			pagerChangeListener.onPageSelected(arg0);
		}
	}

	/**
	 * 获取新建的Tab对象
	 * 
	 * @return
	 */
	public FCTab getNewTab()
	{
		return new FCTab(ctx);
	}

	/**
	 * 初始化ViewPage对象
	 * 
	 * @param pager
	 */
	public void initViewPager(ViewPager pager)
	{
		if (this.pager == pager)
		{
			return;
		}

		if (this.pager != null)
		{
			this.pager.setOnPageChangeListener(null);
		}

		final PagerAdapter adapter = pager.getAdapter();
		if (adapter == null)
		{
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}

		this.pager = pager;
		pager.setOnPageChangeListener(this);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 增加tab选中回调
	 * 
	 * @param tabListener
	 */
	public void addFCTabListener(FCTabListener tabListener)
	{
		this.tabListener = tabListener;
	}

	/**
	 * 设置ViewPager的回调接口
	 * 
	 * @param listener
	 */
	public void setOnPageChangeListener(OnPageChangeListener listener)
	{
		pagerChangeListener = listener;
	}

	/**
	 * 增加tab
	 * 
	 * @param tab
	 */
	public void addFCTab(FCTab tab)
	{
		llTabLay.addView(tab);
		tabWidth = dm.widthPixels / llTabLay.getChildCount();
		tab.setOnClickListener(this);
		tab.tabIndex = llTabLay.getChildCount() - 1;
		tab.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f));
	}

	/**
	 * 设置TabLine的图片资源
	 * 
	 * @param res			图片资源
	 */
	public void setFCTabLineSrc(int res)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), res);
		int width = tabWidth > bitmap.getWidth() ? bitmap.getWidth() : tabWidth;
		Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, width, bitmap.getHeight());//设置tab的宽度和高度
		tabLine.setImageBitmap(b);
	}

	/**
	 * 选中某个Tab 不会调用onTabSelect 
	 * 
	 * @param tab		选中的tabIndex
	 */
	public void selectTab(int tab)
	{
		if (curTab == tab)
		{
			return;
		}

		int childCount = llTabLay.getChildCount();
		if (tab > childCount)
		{
			tab = childCount - 1;
		}

		if (tab < 0)
		{
			tab = 0;
		}

		for (int i = 0; i < childCount; i++)
		{
			if (tab != i)
			{
				llTabLay.getChildAt(i).setSelected(false);
			} else
			{
				llTabLay.getChildAt(i).setSelected(true);
			}
		}
		curTab = tab;
	}

	/**
	 * 调用Tab进行滚动，一般是viewpager的onPagerScroll来调用 
	 * 
	 * @param tabIndex 			当前的tabIndex
	 * @param arg1				滚动的百分比
	 */
	public void tabLineScoller(int tabIndex, float arg1)
	{
		// 平移的目的地
		matrix.setTranslate(tabWidth * tabIndex, 0);
		// 在滑动的过程中，计算出激活条应该要滑动的距离
		float t = (tabWidth) * arg1;
		// 平移的距离
		matrix.postTranslate(t, 0);
		tabLine.setImageMatrix(matrix);
	}

	/**
	 * 获取当前Tab
	 * 
	 * @return
	 */
	public int getCurFCTab()
	{
		return curTab;
	}

	/**
	 * 自定义TAB
	 */
	public class FCTab extends AppCompatTextView
	{

		int	tabIndex;

		private FCTab (Context context)
		{
			super(context);
			this.setGravity(Gravity.CENTER);
		}
	}

	/**
	 * Tab选中回调 
	 */
	public interface FCTabListener
	{

		void onTabSelected(FCTab arg0, int index);
	}
}
