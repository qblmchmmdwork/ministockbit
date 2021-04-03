package com.stockbit.domain.usecase.getwatchlistupdate

import com.stockbit.commonmodel.WatchListUpdate
import com.stockbit.data.repository.WatchListRepository
import com.stockbit.domain.di.KoinContext
import kotlinx.coroutines.flow.Flow

class GetWatchListUpdateImpl : GetWatchListUpdate {

    private val repository: WatchListRepository by KoinContext.koinApp.koin.inject()

    override fun invoke(data: Unit): Flow<WatchListUpdate> = repository.getWatchListUpdate()
}