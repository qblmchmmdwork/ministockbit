package com.stockbit.ministockbit.util

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes

fun Context.getResFromReference(@AttrRes resId: Int): Int? {
    val outTypedValue = TypedValue()
    return if (theme.resolveAttribute(resId, outTypedValue, true))
        if(outTypedValue.resourceId != 0) outTypedValue.resourceId else outTypedValue.data
    else null
}