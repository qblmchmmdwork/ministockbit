package com.stockbit.ministockbit.ui.watchlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stockbit.commonmodel.WatchList
import com.stockbit.commonmodel.WatchListUpdate
import com.stockbit.domain.Resource
import com.stockbit.domain.usecase.getwatchlist.GetWatchList
import com.stockbit.domain.usecase.getwatchlist.GetWatchListParam
import com.stockbit.domain.usecase.getwatchlistupdate.GetWatchListUpdate
import com.stockbit.domain.usecase.watchlistupdatesubscriptionaction.WatchListUpdateSubscriptionAction
import com.stockbit.domain.usecase.watchlistupdatesubscriptionaction.WatchListUpdateSubscriptionParam.Subscribe
import com.stockbit.domain.usecase.watchlistupdatesubscriptionaction.WatchListUpdateSubscriptionParam.UnsubscribeAll
import com.stockbit.ministockbit.ui.watchlist.list.WatchListItemUiModel
import com.stockbit.ministockbit.util.CoroutineDispatcherProvider
import com.stockbit.ministockbit.util.asConsumable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WatchListViewModel(
    private val dispatcher: CoroutineDispatcherProvider,
    private val get: GetWatchList,
    private val getUpdate: GetWatchListUpdate,
    private val updateSubscription: WatchListUpdateSubscriptionAction,
) : ViewModel() {
    private val mutex = Mutex()
    private val _state = MutableLiveData<WatchListState>().apply { value = WatchListState() }
    val state: LiveData<WatchListState> = _state

    private var currentPage = 0
    private var hasReachEnd: Boolean = false

    private var currentState
        get() = _state.value
        set(value) {
            _state.value = value
        }

    private val currentWatchList
        get() = currentState?.data?.peek()
            ?.filterIsInstance(WatchListItemUiModel.WatchList::class.java)
            ?.toMutableList() ?: listOf()

    init {
        startWatchListUpdateSubscription()
    }

    fun load(loadMore: Boolean = true) {
        if (currentState?.loading == true) return
        if (loadMore) {
            if (hasReachEnd) return
            val dataEmpty = currentState?.data?.peek()?.size ?: 0 <= 1
            currentState = currentState?.copy(
                loading = true,
                error = null,
                data = if (dataEmpty) listOf(WatchListItemUiModel.Loading).asConsumable else currentState!!.data
            )
        } else {
            updateSubscription(UnsubscribeAll)
            currentPage = 0
            hasReachEnd = false
            currentState = currentState?.copy(
                loading = true,
                error = null,
                data = listOf(WatchListItemUiModel.Loading).asConsumable
            )
        }
        viewModelScope.launch(dispatcher.main) {
            get(GetWatchListParam(currentPage, PER_PAGE_SIZE))
                .map(::getWatchListMapper)
                .flowOn(dispatcher.io)
                .collect { mutex.withLock { currentState = it } }
        }
    }

    private fun startWatchListUpdateSubscription() {
        viewModelScope.launch(dispatcher.main) {
            getUpdate(Unit)
                .retry { delay(10000); true }
                .map(::watchListUpdateMapper)
                .flowOn(dispatcher.io)
                .collect { mutex.withLock { currentState = it } }
        }
    }

    private fun watchListUpdateMapper(update: WatchListUpdate): WatchListState? {
        val currentWatchListUi = currentWatchList
        val updateIndex = currentWatchListUi.indexOfFirst { it.name == update.name }
        return if (updateIndex <= -1) currentState
        else {
            val newUi = MutableList<WatchListItemUiModel>(currentWatchListUi.size) {
                currentWatchListUi[it]
            }
            newUi[updateIndex] = (newUi[updateIndex] as WatchListItemUiModel.WatchList)
                .copyUpdate(update)
            if (!hasReachEnd) newUi.add(WatchListItemUiModel.Loading)
            currentState?.copy(data = newUi.asConsumable)
        }
    }

    private fun getWatchListMapper(res: Resource<List<WatchList>>): WatchListState? {
        return when (res) {
            Resource.Loading -> currentState
            is Resource.Error -> {
                val currentWatchListUi = currentWatchList
                val newUi = MutableList<WatchListItemUiModel>(currentWatchListUi.size) {
                    currentWatchListUi[it]
                }
                newUi.add(WatchListItemUiModel.Error)
                currentState?.copy(
                    loading = false,
                    error = res.message.asConsumable,
                    data = newUi.asConsumable
                )
            }
            is Resource.Success -> {
                currentPage++
                val currentWatchListUi = currentWatchList
                val newUi = MutableList<WatchListItemUiModel>(currentWatchListUi.size) {
                    currentWatchListUi[it]
                }
                res.data.forEach { watchList ->
                    val uiModel = WatchListItemUiModel.WatchList.from(watchList)
                    val duplicate = currentWatchListUi.indexOfFirst { it.name == uiModel.name }
                    if (duplicate > -1) newUi[duplicate] = uiModel
                    else newUi.add(uiModel)
                }
                updateSubscription(Subscribe(res.data.map { it.name }))
                if (res.data.size >= PER_PAGE_SIZE && newUi.size < MAX_SIZE)
                    newUi.add(WatchListItemUiModel.Loading)
                else hasReachEnd = true
                currentState?.copy(loading = false, data = newUi.asConsumable)
            }
        }
    }

    companion object {
        private const val PER_PAGE_SIZE = 10
        private const val MAX_SIZE = 50
    }
}

