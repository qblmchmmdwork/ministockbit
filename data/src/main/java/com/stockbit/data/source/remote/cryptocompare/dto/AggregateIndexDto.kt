@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection",
    "SpellCheckingInspection", "SpellCheckingInspection"
)

package com.stockbit.data.source.remote.cryptocompare.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AggregateIndexDto(
    @Json(name = "FROMSYMBOL")
    val fromSymbol: String?,
    @Json(name = "OPEN24HOUR")
    val open24Hour: Double?, // 28044689.78
    @Json(name = "PRICE")
    val price: Double?, // 29412404.33
)