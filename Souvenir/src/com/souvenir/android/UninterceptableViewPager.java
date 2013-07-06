package com.souvenir.android;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class UninterceptableViewPager extends ViewPager
{
  public UninterceptableViewPager(Context context)
  {
    super(context);
  }

  public UninterceptableViewPager(Context context, AttributeSet attrs)
  {
    super(context, attrs);
  }

  // @Override
  // public boolean onTouchEvent(MotionEvent event)
  // {
  //
  // if (event.getAction() == MotionEvent.ACTION_MOVE)
  // {
  // this.getParent().requestDisallowInterceptTouchEvent(true);
  // // My Other Code
  // }
  //
  // return true;
  // }
  //
  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev)
  {
    // Tell our parent to stop intercepting our events!
    boolean ret = super.onInterceptTouchEvent(ev);
    // if (ret)
    // {

    getParent().requestDisallowInterceptTouchEvent(true);
    // }
    return ret;
  }
}