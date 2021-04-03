package com.stockbit.ministockbit.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.android.material.button.MaterialButton
import com.stockbit.ministockbit.R
import com.stockbit.ministockbit.util.dp
import com.stockbit.ministockbit.util.getResFromReference

class LoginServiceButton(context: Context, attrs: AttributeSet?, defStyleArt: Int) :
    MaterialButton(context, attrs, defStyleArt) {
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        R.attr.materialButtonStyle
    )

    constructor(context: Context) : this(context, null)

    init {
        backgroundTintList =
            ContextCompat.getColorStateList(
                context,
                context.getResFromReference(android.R.attr.colorBackground)!!
            )
        setTextColor(
            ContextCompat.getColorStateList(
                context,
                context.getResFromReference(android.R.attr.textColorPrimary)!!
            )
        )
        isAllCaps = false
        iconTint = null
        setPadding(16.dp)
        iconSize = 32.dp
        strokeColor = ContextCompat.getColorStateList(context, R.color.dark_grey)
        iconPadding = -(icon?.intrinsicWidth ?: 0)
    }
}