package com.stockbit.domain.usecase.getwatchlist

import com.stockbit.commonmodel.WatchList
import com.stockbit.domain.Resource
import com.stockbit.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow

interface GetWatchList : UseCase<GetWatchListParam, Flow<Resource<List<WatchList>>>>

