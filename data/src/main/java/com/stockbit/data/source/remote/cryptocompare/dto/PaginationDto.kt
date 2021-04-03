package com.stockbit.data.source.remote.cryptocompare.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaginationDto<T>(
    @Json(name = "Message")
    val message: String?,
    @Json(name = "MetaData")
    val metaData: PaginationMetaDataDto,
    @Json(name = "Data")
    val data: List<T>
)