package dev.agenda.adapters

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent

class CustomViewPager : ViewPager {
    var isPagingEnabled: Boolean = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return super.onTouchEvent(ev) && this.isPagingEnabled
    }

    override fun executeKeyEvent(event: KeyEvent): Boolean {
        return super.executeKeyEvent(event) && this.isPagingEnabled
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev) && this.isPagingEnabled
    }

}