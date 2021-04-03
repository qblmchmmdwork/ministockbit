package com.stockbit.ministockbit.ui.watchlist

import com.stockbit.ministockbit.ui.watchlist.list.WatchListItemUiModel
import com.stockbit.ministockbit.util.Consumable
import com.stockbit.ministockbit.util.asConsumable

data class WatchListState(
    val loading: Boolean = false,
    val error: Consumable<String>? = null,
    val data: Consumable<List<WatchListItemUiModel>> = listOf(WatchListItemUiModel.Loading).asConsumable
)