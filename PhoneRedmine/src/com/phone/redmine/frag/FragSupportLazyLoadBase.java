package com.phone.redmine.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 懒加载的Fragment基类
 */
public abstract class FragSupportLazyLoadBase extends BaseFrag
{

	protected View	rootView;

	/** Fragment当前状态是否可见 */
	private boolean	isVisible;

	/** 是否已被加载过一次，第二次就不再去请求数据了 */
	private boolean	mHasLoadedOnce;

	/** 是否已经准备好，防止在onCreateView之前调用 */
	private boolean	isPrepared;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		/*在ViewPager切换过程中会重新调用onCreateView，此时如果实例化过，需要移除，会自动再次添加*/
		if (rootView == null)
		{
			rootView = inflater.inflate(getRootViewLayId(), null);
			isPrepared = true;
			onPreLoad();
		} else
		{
			if (rootView.getParent() != null)
			{
				((ViewGroup) rootView.getParent()).removeView(rootView);
			}
		}
		return rootView;
	}

	/**
	 * 在OnCreatView之前执行
	 * 应该在onAttach之前
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser)
	{
		super.setUserVisibleHint(isVisibleToUser);

		if (isVisibleToUser)
		{
			isVisible = true;
			onPreLoad();
		} else
		{
			isVisible = false;
		}
	}

	/**
	 * 懒加载之前的判断
	 */
	private void onPreLoad()
	{
		if (!isVisible || !isPrepared || mHasLoadedOnce)
		{
			return;
		}

		mHasLoadedOnce = true;
		lazyLoad();
	}

	/** 
	 * 延迟加载， 子类必须重写此方法
	 */
	protected abstract void lazyLoad();

	/**
	 * 获取布局Id
	 * 
	 * @return
	 */
	protected abstract int getRootViewLayId();
}
