package com.stockbit.ministockbit.di

import com.stockbit.domain.usecase.getwatchlist.GetWatchList
import com.stockbit.domain.usecase.getwatchlist.GetWatchListImpl
import com.stockbit.domain.usecase.getwatchlistupdate.GetWatchListUpdate
import com.stockbit.domain.usecase.getwatchlistupdate.GetWatchListUpdateImpl
import com.stockbit.domain.usecase.watchlistupdatesubscriptionaction.WatchListUpdateSubscriptionAction
import com.stockbit.domain.usecase.watchlistupdatesubscriptionaction.WatchListUpdateSubscriptionActionImpl
import com.stockbit.ministockbit.ui.watchlist.WatchListViewModel
import com.stockbit.ministockbit.util.CoroutineDispatcherProvider
import kotlinx.coroutines.Dispatchers
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { CoroutineDispatcherProvider(Dispatchers.Main, Dispatchers.IO) }
    factory { GetWatchListImpl() } bind GetWatchList::class
    factory { GetWatchListUpdateImpl() } bind GetWatchListUpdate::class
    factory { WatchListUpdateSubscriptionActionImpl() } bind WatchListUpdateSubscriptionAction::class
    viewModel {
        WatchListViewModel(
            dispatcher = get(),
            get = get(),
            getUpdate = get(),
            updateSubscription = get()
        )
    }
}