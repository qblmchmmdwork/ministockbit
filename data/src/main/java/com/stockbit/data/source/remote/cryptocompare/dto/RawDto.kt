package com.stockbit.data.source.remote.cryptocompare.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RawDto(
    @Json(name = "USD")
    val currencyInfo: CurrencyInfoDto
)
