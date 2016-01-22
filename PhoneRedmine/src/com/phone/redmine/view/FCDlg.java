package com.phone.redmine.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.phone.redmine.R;
import com.phone.redmine.util.DisplayUtil;

/** 
 * 对话框基类
 */
public class FCDlg extends Dialog
{
	/**
	 * @param context
	 * @param layResID		layoutId
	 */
	public FCDlg (Context context, int layResID)
	{
		super(context, R.style.dialog_style);
		setContentView(layResID);
		setLayout((int) (DisplayUtil.getScreenWidth(context) - 200), 0);
	}

	/**
	 * @param context
	 * @param view			view
	 */
	public FCDlg (Context context, View view)
	{
		super(context, R.style.dialog_style);
		setContentView(view);
	}

	/**
	 * 对话框显示, 点击其他地方可消失
	 */
	@Override
	public void show()
	{
		this.show(true);
	}

	/**
	 * 对话框显示
	 * 
	 * @param isCancleAble		点击其他地方是否可消失
	 */
	public void show(boolean isCancleAble)
	{
		this.show(isCancleAble, 0);
	}

	/**
	 * 对话框显示
	 * 
	 * @param isCancleAble		点击其他地方是否可消失
	 * @param anima				-1 无动画，else传入动画
	 */
	public void show(boolean isCancleAble, int anima)
	{
		show(isCancleAble, 0, anima);
	}

	/** 
	 * 对话框显示
	 * 
	 * @param isCancleAble 		点击其他地方是否可消失
	 * @param resBg 			-1透明 0默认背景 else传入背景，
	 * @param anima 			-1 无动画，else传入动画 
	 */
	public void show(boolean isCancleAble, int resBg, int anima)
	{
		setDlgAnimation(anima);
		setDlgBackGround(resBg);				// 经测试，必须在setAnimation之后才可起效果
		setCanceledOnTouchOutside(isCancleAble);// 设置触摸对话框以外的地方取消对话框
		super.show();
	}

	/** 
	 * 设定弹出动画
	 * 
	 * @param anima 			 -1 无动画，0 默认动画， else传入动画
	 */
	public void setDlgAnimation(int anima)
	{
		switch (anima)
		{
			case -1:
				break;
			case 0:
				getWindow().setWindowAnimations(R.style.dialogWindowAnim); // 设置窗口弹出动画
				break;
			default:
				getWindow().setWindowAnimations(anima);
				break;
		}
	}

	/** 
	 * 设置对话框背景
	 * 
	 * @param resBg 		-1透明， 0纯白， 背景图片资源Id
	 */
	public void setDlgBackGround(int resBg)
	{
		switch (resBg)
		{
			case -1:
				getWindow().setBackgroundDrawableResource(R.color.transparent);
				break;
			case 0:
				getWindow().setBackgroundDrawableResource(R.drawable.dlg_bg);
				break;
			default:
				getWindow().setBackgroundDrawableResource(resBg);
				break;
		}
	}

	/**
	 * 设置窗口居中方式
	 *  
	 * @param gravity 		对齐方式
	 */
	public void setDlgGravity(int gravity)
	{
		getWindow().getAttributes().gravity = gravity;
	}

	/**
	 * 设置窗口透明度
	 * 
	 * @param alpha			透明度
	 */
	public void setDlgAlpha(int alpha)
	{
		getWindow().getAttributes().alpha = alpha;
	}

	/** 
	 * 设置窗口显示偏移
	 * 
	 * @param x				x小于0左移，大于0右移
	 * @param y				y小于0上移，大于0下移
	 */
	public void windowDeploy(int x, int y)
	{
		if (x != 0 || y != 0)
		{
			Window window = getWindow();
			WindowManager.LayoutParams wl = window.getAttributes();

			// 根据x，y坐标设置窗口需要显示的位置
			wl.x = x;
			wl.y = y;
			window.setAttributes(wl);
		}
	}

	/** 
	 * 设定Dialog的固定大小
	 * 
	 * @param width			宽
	 * @param high 			高
	 */
	public void setLayout(int width, int high)
	{
		Window window = getWindow(); // 得到对话框
		WindowManager.LayoutParams wl = window.getAttributes();
		if (width > 0)
			wl.width = width; // x小于0左移，大于0右移
		if (high > 0)
			wl.height = high; // y小于0上移，大于0下移
		// wl.gravity = Gravity.BOTTOM; //设置重力
		window.setAttributes(wl);
	}

	/** 
	 * 通过资源ID获取view
	 * 
	 * @param resID 		资源ID
	 * 
	 * @return 				View
	 */
	public View getView(int resID)
	{
		return findViewById(resID);
	}

	/** 
	 * 确定回调接口
	 */
	public interface FCParamSubmitInterface<T>
	{

		boolean submit(T t);
	}

	/** 
	 * 取消回调接口
	 */
	public interface FCParamCancelInterface<T>
	{

		boolean cancel(T t);
	}
	
	/** 
	 * 确定回调接口
	 */
	public interface FCSubmitInterface
	{

		boolean submit();
	}

	/** 
	 * 取消回调接口
	 */
	public interface FCCancelInterface
	{

		boolean cancel();
	}
}
