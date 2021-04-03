package com.stockbit.ministockbit.ui.watchlist.list

import com.stockbit.commonmodel.WatchListUpdate
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

sealed class WatchListItemUiModel(val viewType: Int) {
    enum class ViewType {
        WatchList,
        Loading,
        Error;

        companion object {
            private val values = values()
            fun fromID(id: Int) = values[id]
        }

        val id get() = values.indexOf(this)
    }

    data class WatchList(
        val name: String,
        val fullName: String,
        val price: String,
        val priceChange: String,
        val priceChangeState: PriceChangeState,
        val priceOpen24Hour: Double?
    ) : WatchListItemUiModel(ViewType.WatchList.id) {
        enum class PriceChangeState {
            Increase,
            Decrease,
            Still,
        }

        fun copyUpdate(update: WatchListUpdate): WatchList {
            val (priceText, priceChangeText) = getPriceAndPriceChangeText(update.price, priceOpen24Hour)
            val priceChangeState = getPriceChangeState(update.price, priceOpen24Hour)
            return copy(
                price = priceText,
                priceChange = priceChangeText,
                priceChangeState = priceChangeState
            )
        }

        companion object{
            private val decimalFormat = DecimalFormat("#.##").apply {
                roundingMode = RoundingMode.UP
                decimalFormatSymbols = DecimalFormatSymbols(Locale("id", "ID"))
            }

            fun from(wl: com.stockbit.commonmodel.WatchList) : WatchList {
                val (priceText, priceChangeText) = getPriceAndPriceChangeText(wl.price, wl.open24Hr)
                val priceChangeState = getPriceChangeState(wl.price, wl.open24Hr)
                return WatchList(
                    name = wl.name,
                    fullName = wl.fullName,
                    price = priceText,
                    priceChange = priceChangeText,
                    priceChangeState = priceChangeState,
                    priceOpen24Hour = wl.open24Hr
                )
            }

            private fun getPriceAndPriceChangeText(
                price: Double?,
                priceOpen24Hour: Double?
            ): Pair<String, String> {
                var (priceText, priceChangeText) = "-" to "-"
                if (price != null && priceOpen24Hour != null) {
                    val priceChange = price - priceOpen24Hour
                    val priceChangePercent = priceChange / priceOpen24Hour * 100
                    priceText = decimalFormat.format(price)
                    priceChangeText =
                        "${decimalFormat.format(priceChange)}(${decimalFormat.format(priceChangePercent)}%)"
                }
                return priceText to priceChangeText
            }

            private fun getPriceChangeState(price: Double?, priceOpen24Hour: Double?): PriceChangeState {
                if (price == null || priceOpen24Hour == null) return PriceChangeState.Still
                val priceChange = price - priceOpen24Hour
                return when {
                    priceChange < 0.0 -> PriceChangeState.Decrease
                    priceChange > 0.0 -> PriceChangeState.Increase
                    else -> PriceChangeState.Still
                }
            }
        }
    }

    object Loading : WatchListItemUiModel(ViewType.Loading.id) {
        override fun toString() = "WatchListItemUiModel.Loading"
    }
    object Error : WatchListItemUiModel(ViewType.Error.id) {
        override fun toString() = "WatchListItemUiModel.Error"
    }
}