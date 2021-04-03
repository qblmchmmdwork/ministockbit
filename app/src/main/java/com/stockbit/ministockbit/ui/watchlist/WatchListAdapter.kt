package com.stockbit.ministockbit.ui.watchlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stockbit.ministockbit.databinding.WatchListItemBinding
import com.stockbit.ministockbit.databinding.WatchListItemErrorBinding
import com.stockbit.ministockbit.databinding.WatchListItemLoadingBinding
import com.stockbit.ministockbit.ui.watchlist.list.*

class WatchListAdapter : RecyclerView.Adapter<WatchListItem<*, *, *>>() {
    private val diffCallback = WatchListDiffUtil()
    private var data = mutableListOf<WatchListItemUiModel>()

    var onReloadClickListener: (() -> Unit)? = null
    var onLoadMoreListener: (() -> Unit)? = null

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int = data[position].viewType

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): WatchListItem<*, *, *> {
        val layoutInflater = LayoutInflater.from(viewGroup.context)
        return when (WatchListItemUiModel.ViewType.fromID(viewType)) {
            WatchListItemUiModel.ViewType.WatchList -> WatchListItemCoin(
                WatchListItemBinding.inflate(layoutInflater, viewGroup, false)
            )
            WatchListItemUiModel.ViewType.Loading -> WatchListItemLoading(
                WatchListItemLoadingBinding.inflate(layoutInflater, viewGroup, false)
            )
            WatchListItemUiModel.ViewType.Error -> WatchListItemError(
                WatchListItemErrorBinding.inflate(layoutInflater, viewGroup, false)
            )
        }
    }

    override fun onBindViewHolder(holder: WatchListItem<*, *, *>, position: Int) {
        val data = data[position]
        when (holder) {
            is WatchListItemCoin -> holder.onBind(data as WatchListItemUiModel.WatchList)
            is WatchListItemError -> holder.onBind(Unit) {
                onReloadClickListener?.invoke()
            }
            is WatchListItemLoading -> {
            }
        }
    }

    override fun onViewAttachedToWindow(holder: WatchListItem<*, *, *>) {
        if (holder is WatchListItemLoading) onLoadMoreListener?.invoke()
    }

    fun update(newData: List<WatchListItemUiModel>) {
        val oldData = data.toList()
        data.clear()
        data.addAll(newData)
        diffCallback.updateAdapter(oldData, newData, this)
    }
}