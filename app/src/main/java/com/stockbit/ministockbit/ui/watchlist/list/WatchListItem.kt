package com.stockbit.ministockbit.ui.watchlist.list

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class WatchListItem<B : ViewBinding, T, R>(protected val binding: B) : RecyclerView.ViewHolder(binding.root) {
    abstract fun onBind(data: T, onClick: ((R)->Unit)? = null)
}