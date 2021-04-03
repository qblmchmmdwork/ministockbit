package com.stockbit.domain.usecase.watchlistupdatesubscriptionaction

import com.stockbit.data.repository.WatchListRepository
import com.stockbit.domain.di.KoinContext

class WatchListUpdateSubscriptionActionImpl :
    WatchListUpdateSubscriptionAction {

    private val repository: WatchListRepository by KoinContext.koinApp.koin.inject()

    override fun invoke(data: WatchListUpdateSubscriptionParam) {
        when(data) {
            is WatchListUpdateSubscriptionParam.Subscribe -> repository.subscribeToWatchListUpdate(data.name)
            is WatchListUpdateSubscriptionParam.Unsubscribe -> repository.unsubscribeFromWatchListUpdate(data.name)
            WatchListUpdateSubscriptionParam.UnsubscribeAll -> repository.unsubscribeAllToWatchListUpdate()
        }
    }
}