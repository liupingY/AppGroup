/*******************************************
 *版权所有©2015,深圳市铂睿智恒科技有限公司
 *
 *内容摘要：
 *当前版本：
 *作	者：
 *完成日期：
 *修改记录：
 *修改日期：
 *版 本 号：
 *修 改 人：
 *修改内容：
...
 *修改记录：
 *修改日期：
 *版 本 号：
 *修 改 人：
 *修改内容：
 *********************************************/

package com.prize.appcenter.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager {
	private boolean isCanScroll = true;

	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
//		if (!isCanScroll) {
//			return false;
//		} else {
//			return super.onTouchEvent(ev);
//		}
		return isCanScroll && super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		if (!isCanScroll) {
//			return false;
//		} else {
//			return super.onInterceptTouchEvent(ev);
//		}
		return isCanScroll && super.onInterceptTouchEvent(ev);
	}
	@Override
	public void setCurrentItem(int item) {
		super.setCurrentItem(item, false);//表示切换的时候，不需要切换时间。
	}
}
