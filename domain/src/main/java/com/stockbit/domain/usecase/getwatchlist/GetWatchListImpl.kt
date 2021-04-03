package com.stockbit.domain.usecase.getwatchlist

import com.stockbit.commonmodel.WatchList
import com.stockbit.data.repository.RepositoryResource
import com.stockbit.data.repository.WatchListRepository
import com.stockbit.domain.Resource
import com.stockbit.domain.di.KoinContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetWatchListImpl : GetWatchList {

    private val repository: WatchListRepository by KoinContext.koinApp.koin.inject()

    override fun invoke(data: GetWatchListParam): Flow<Resource<List<WatchList>>> = flow {
        emit(Resource.Loading)
        when (val res = repository.getWatchList(data.page, data.limit)) {
            is RepositoryResource.Error -> emit(Resource.Error(res.message, res.cause))
            is RepositoryResource.Success -> emit(Resource.Success(res.data))
        }
    }
}