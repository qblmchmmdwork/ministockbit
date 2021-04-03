package com.stockbit.domain.usecase.getwatchlistupdate

import com.stockbit.commonmodel.WatchListUpdate
import com.stockbit.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow

interface GetWatchListUpdate : UseCase<Unit, Flow<WatchListUpdate>>

