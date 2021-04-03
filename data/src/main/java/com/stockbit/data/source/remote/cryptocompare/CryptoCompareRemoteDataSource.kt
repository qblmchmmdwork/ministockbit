package com.stockbit.data.source.remote.cryptocompare

import com.squareup.moshi.Moshi
import com.stockbit.commonmodel.WatchList
import com.stockbit.commonmodel.WatchListUpdate
import com.stockbit.data.source.remote.RemoteResource
import com.stockbit.data.source.remote.WatchListRemoteDataSource
import com.stockbit.data.source.remote.cryptocompare.dto.AggregateIndexDto
import com.stockbit.data.util.safeApiCall
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class CryptoCompareRemoteDataSource : WatchListRemoteDataSource {

    private val okHttpClient by lazy {
        OkHttpClient().newBuilder().build()
    }

    private val wsOkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .retryOnConnectionFailure(true)
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    private val api by lazy { retrofit.create(CryptoCompareApi::class.java) }

    private var webSocket: WebSocket? = null
        private set(value) {
            field = value
            if (value != null) {
                subscribeAction(value)
            }
        }

    private val subscriptions = mutableSetOf<String>()

    override suspend fun getWatchList(page: Int, limit: Int): RemoteResource<List<WatchList>> {
        return safeApiCall("Failed to get watch list") {
            val response = api.getTopTierCoinVolume24Hour(page, limit)
            response.data.map {
                WatchList(
                    name = it.coinInfo.name,
                    fullName = it.coinInfo.fullName,
                    price = it.raw?.currencyInfo?.price,
                    open24Hr = it.raw?.currencyInfo?.open24Hour,
                )
            }
        }
    }

    override fun stopSubscription() {
        webSocket?.close(1000, "Stop")
    }

    override fun subscribeToWatchListUpdate(vararg name: String) {
        subscriptions.addAll(name)
        if (webSocket != null) {
            subscribeAction(webSocket!!)
        }
    }

    override fun unsubscribeFromWatchListUpdate(vararg name: String) {
        subscriptions.removeAll(name)
        if (webSocket != null) {
            subscribeAction(webSocket!!, name, true)
        }
    }

    override fun unsubscribeAllFromWatchListUpdate() {
        if (webSocket != null) {
            subscribeAction(webSocket!!, true)
        }
    }

    private fun subscribeAction(webSocket: WebSocket, unsubscribe: Boolean = false) {
        subscribeAction(webSocket, subscriptions.toTypedArray(), unsubscribe)
        if (unsubscribe) subscriptions.clear()
    }

    @Suppress("SpellCheckingInspection")
    private fun subscribeAction(
        webSocket: WebSocket,
        subscription: Array<out String>,
        unsubscribe: Boolean = false
    ) {
        if (subscription.isEmpty()) return
        val payload = """
        {
           "action": "${if (unsubscribe) "SubRemove" else "SubAdd"}",
           "subs": [${subscription.joinToString(",", transform = { "\"5~CCCAGG~$it~USD\"" })}]
        }
        """
        webSocket.send(payload)
    }

    @Suppress("SpellCheckingInspection")
    @ExperimentalCoroutinesApi
    override fun getWatchListUpdateFlow() = callbackFlow<WatchListUpdate> {
        if (webSocket != null) {
            webSocket!!.close(1000, null)
        }
        webSocket = wsOkHttpClient.newWebSocket(Request.Builder()
            .url("wss://streamer.cryptocompare.com/v2?api_key=$API_KEY")
            .build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    subscribeAction(webSocket, subscriptions.toTypedArray())
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    cancel(response?.message ?: "Error", t)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    channel.close()
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    val adapter = Moshi.Builder().build().adapter(AggregateIndexDto::class.java)
                    val result = kotlin.runCatching { adapter.fromJson(text) }.getOrNull() ?: return
                    val price = result.price ?: return
                    offer(
                        WatchListUpdate(
                            name = result.fromSymbol ?: return,
                            price = price,
                        )
                    )
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    this@CryptoCompareRemoteDataSource.webSocket = null
                    webSocket.close(1000, "bye!")
                }
            })

        awaitClose {
            webSocket?.close(1000, null)
        }
    }

    companion object {
        //TODO HIDE
        @Suppress("SpellCheckingInspection")
        private const val API_KEY =
            "50fd09e620b43f12f2abec94469dd8d8db8011eb4510013d60120880b79456bc"
    }
}

