package com.stockbit.ministockbit.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.appbar.MaterialToolbar
import com.stockbit.ministockbit.R

class StockbitToolbar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    MaterialToolbar(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null, R.attr.toolbarStyle)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, R.attr.toolbarStyle)
    private var titleTextView: AppCompatTextView? = null


    init {
        setContentInsetsAbsolute(0, 0)
        contentInsetStartWithNavigation = 0
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        titleTextView = if (title == null) null
        else (getChildAt(childCount - 1) as AppCompatTextView).apply {
            layoutParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            ).apply { gravity = Gravity.CENTER }
            typeface = Typeface.DEFAULT_BOLD
        }
    }
}