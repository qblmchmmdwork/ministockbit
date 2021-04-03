package com.stockbit.data.source.remote

import com.stockbit.commonmodel.WatchList
import com.stockbit.commonmodel.WatchListUpdate
import kotlinx.coroutines.flow.Flow


interface WatchListRemoteDataSource {
    suspend fun getWatchList(page: Int, limit: Int): RemoteResource<List<WatchList>>
    fun getWatchListUpdateFlow(): Flow<WatchListUpdate>
    fun subscribeToWatchListUpdate(vararg name: String)
    fun unsubscribeFromWatchListUpdate(vararg name: String)
    fun unsubscribeAllFromWatchListUpdate()
    fun stopSubscription()
}