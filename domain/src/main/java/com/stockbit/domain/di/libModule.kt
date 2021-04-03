package com.stockbit.domain.di

import com.stockbit.data.repository.WatchListRepository
import com.stockbit.data.source.remote.WatchListRemoteDataSource
import com.stockbit.data.source.remote.cryptocompare.CryptoCompareRemoteDataSource
import org.koin.dsl.binds
import org.koin.dsl.module

val libModule = module {
    single { CryptoCompareRemoteDataSource() } binds arrayOf(WatchListRemoteDataSource::class)
    single { WatchListRepository(get()) }
}