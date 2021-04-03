@file:Suppress("SpellCheckingInspection")

package com.stockbit.data.source.remote.cryptocompare.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrencyInfoDto(
    @Json(name = "OPEN24HOUR")
    val open24Hour: Double,
    @Json(name = "PRICE")
    val price: Double,
)