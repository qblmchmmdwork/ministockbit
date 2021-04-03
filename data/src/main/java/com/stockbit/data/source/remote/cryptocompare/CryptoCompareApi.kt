@file:Suppress("SpellCheckingInspection")

package com.stockbit.data.source.remote.cryptocompare

import com.stockbit.data.source.remote.cryptocompare.dto.CoinDto
import com.stockbit.data.source.remote.cryptocompare.dto.PaginationDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoCompareApi {
    @GET("data/top/totaltoptiervolfull")
    suspend fun getTopTierCoinVolume24Hour(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("tsym") currencySymbol: String = "USD"
    ): PaginationDto<CoinDto>
}