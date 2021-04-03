package com.stockbit.commonmodel

data class WatchList(
    val name: String,
    val fullName: String,
    val price: Double?,
    val open24Hr: Double?,
)