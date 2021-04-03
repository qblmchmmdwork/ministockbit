package com.stockbit.domain.usecase.watchlistupdatesubscriptionaction

sealed class WatchListUpdateSubscriptionParam {
    object UnsubscribeAll: WatchListUpdateSubscriptionParam()
    data class Subscribe(val name: List<String>): WatchListUpdateSubscriptionParam()
    data class Unsubscribe(val name: List<String>): WatchListUpdateSubscriptionParam()
}