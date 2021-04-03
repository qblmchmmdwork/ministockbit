package com.stockbit.ministockbit.ui.watchlist.list

import com.stockbit.ministockbit.databinding.WatchListItemLoadingBinding

class WatchListItemLoading(binding: WatchListItemLoadingBinding) : WatchListItem<WatchListItemLoadingBinding, Unit, Unit>(binding){
    override fun onBind(data: Unit, onClick: ((Unit) -> Unit)?) {}
}