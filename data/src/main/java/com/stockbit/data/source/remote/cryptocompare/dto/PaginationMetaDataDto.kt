package com.stockbit.data.source.remote.cryptocompare.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaginationMetaDataDto(
    @Json(name = "Count")
    val count: Int?
)