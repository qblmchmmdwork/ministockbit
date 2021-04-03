package com.stockbit.data.repository

import com.stockbit.commonmodel.WatchList
import com.stockbit.data.source.remote.RemoteResource
import com.stockbit.data.source.remote.WatchListRemoteDataSource

class WatchListRepository(private val remoteDataSource: WatchListRemoteDataSource) {
    suspend fun getWatchList(page: Int, limit: Int) :RepositoryResource<List<WatchList>> {
        return when(val res = remoteDataSource.getWatchList(page, limit)) {
            is RemoteResource.Error -> RepositoryResource.Error(res.message, res.cause)
            is RemoteResource.Failed -> RepositoryResource.Error(res.message)
            is RemoteResource.Success -> RepositoryResource.Success(res.data)
        }
    }

    fun getWatchListUpdate() = remoteDataSource.getWatchListUpdateFlow()
    fun subscribeToWatchListUpdate(name: List<String>) = remoteDataSource.subscribeToWatchListUpdate(*name.toTypedArray())
    fun unsubscribeFromWatchListUpdate(name: List<String>) = remoteDataSource.unsubscribeFromWatchListUpdate(*name.toTypedArray())
    fun unsubscribeAllToWatchListUpdate() = remoteDataSource.unsubscribeAllFromWatchListUpdate()
}