package com.stockbit.ministockbit.util

import android.content.res.Resources

val Int.dp : Int
    get() = Resources.getSystem().displayMetrics.density.toInt() * this
