package com.stockbit.data.source.remote.cryptocompare.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinDto(
    @Json(name = "CoinInfo")
    val coinInfo: CoinInfoDto,
    @Json(name = "RAW")
    val raw: RawDto?
)
