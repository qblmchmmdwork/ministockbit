package com.stockbit.ministockbit.ui.watchlist.list

import com.stockbit.ministockbit.databinding.WatchListItemErrorBinding

class WatchListItemError(binding: WatchListItemErrorBinding) :
    WatchListItem<WatchListItemErrorBinding, Unit, Unit>(binding) {
    override fun onBind(data: Unit, onClick: ((Unit) -> Unit)?) {
        binding.reloadButton.setOnClickListener { onClick?.invoke(Unit) }
    }
}