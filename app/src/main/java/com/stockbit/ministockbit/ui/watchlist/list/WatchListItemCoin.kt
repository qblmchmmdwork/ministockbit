package com.stockbit.ministockbit.ui.watchlist.list

import androidx.core.content.ContextCompat
import com.stockbit.ministockbit.R
import com.stockbit.ministockbit.databinding.WatchListItemBinding
import com.stockbit.ministockbit.util.getResFromReference

class WatchListItemCoin(binding: WatchListItemBinding) :
    WatchListItem<WatchListItemBinding, WatchListItemUiModel.WatchList, Unit>(binding) {
    private val priceIncreaseColor = binding.root.context.getColor(R.color.green_A700)
    private val priceDecreaseColor = binding.root.context.getColor(R.color.red_600)
    private val priceStillColor =  ContextCompat.getColorStateList(binding.root.context, binding.root.context.getResFromReference(
        R.attr.colorOnSurface
    )!!)!!.defaultColor

    override fun onBind(data: WatchListItemUiModel.WatchList, onClick: ((Unit) -> Unit)?) {
        binding.apply {
            name.text = data.name
            fullName.text = data.fullName
            price.text = data.price
            priceChange.apply {
                text = data.priceChange
                setTextColor(
                    when (data.priceChangeState) {
                        WatchListItemUiModel.WatchList.PriceChangeState.Increase -> priceIncreaseColor.also {
                            alpha = 1f
                        }
                        WatchListItemUiModel.WatchList.PriceChangeState.Decrease -> priceDecreaseColor.also {
                            alpha = 1f
                        }
                        WatchListItemUiModel.WatchList.PriceChangeState.Still -> priceStillColor.also {
                            alpha = 0.4f
                        }
                    }
                )
            }
        }
    }
}