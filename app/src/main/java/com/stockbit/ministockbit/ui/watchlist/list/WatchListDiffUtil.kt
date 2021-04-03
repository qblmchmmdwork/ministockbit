package com.stockbit.ministockbit.ui.watchlist.list

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class WatchListDiffUtil : DiffUtil.Callback() {
    private var oldItems: List<WatchListItemUiModel>? = null
    private var newItems: List<WatchListItemUiModel>? = null

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldItems == null || newItems == null) return true
        val oldItem = oldItems!![oldItemPosition]
        val newItem = newItems!![newItemPosition]
        if (oldItem.viewType != newItem.viewType) return false
        return when (oldItem) {
            WatchListItemUiModel.Error -> true
            WatchListItemUiModel.Loading -> false
            is WatchListItemUiModel.WatchList -> {
                val newItemCasted = newItem as WatchListItemUiModel.WatchList
                oldItem.name == newItemCasted.name
            }
        }
    }

    override fun getOldListSize(): Int {
        return oldItems?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newItems?.size ?: 0
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        if (oldItems == null || newItems == null) return true
        val oldItem = oldItems!![oldItemPosition]
        val newItem = newItems!![newItemPosition]
        if (oldItem.viewType != newItem.viewType) return false
        return when (oldItem) {
            WatchListItemUiModel.Error -> true
            WatchListItemUiModel.Loading -> false
            is WatchListItemUiModel.WatchList -> {
                val newItemCasted = newItem as WatchListItemUiModel.WatchList
                oldItem == newItemCasted
            }
        }
    }

    fun updateAdapter(
        oldData: List<WatchListItemUiModel>,
        newData: List<WatchListItemUiModel>,
        adapter: RecyclerView.Adapter<*>
    ) {
        this.oldItems = oldData; this.newItems = newData
        DiffUtil.calculateDiff(this).dispatchUpdatesTo(adapter)
    }

}
