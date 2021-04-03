@file:Suppress("SpellCheckingInspection", "SpellCheckingInspection", "SpellCheckingInspection")

package com.stockbit.data.source.remote.cryptocompare.dto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CoinInfoDto(
    @Json(name = "AssetLaunchDate")
    val assetLaunchDate: String?, // 2017-09-03
    @Json(name = "FullName")
    val fullName: String, // Aventus
    @Json(name = "Id")
    val id: String, // 138642
    @Json(name = "ImageUrl")
    val imageUrl: String?, // /media/37746592/avt.png
    @Json(name = "Name")
    val name: String, // AVT
)