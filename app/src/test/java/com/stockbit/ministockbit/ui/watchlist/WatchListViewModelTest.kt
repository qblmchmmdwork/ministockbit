package com.stockbit.ministockbit.ui.watchlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.stockbit.commonmodel.WatchList
import com.stockbit.commonmodel.WatchListUpdate
import com.stockbit.domain.Resource
import com.stockbit.domain.usecase.getwatchlist.GetWatchList
import com.stockbit.domain.usecase.getwatchlist.GetWatchListParam
import com.stockbit.domain.usecase.getwatchlistupdate.GetWatchListUpdate
import com.stockbit.domain.usecase.watchlistupdatesubscriptionaction.WatchListUpdateSubscriptionAction
import com.stockbit.domain.usecase.watchlistupdatesubscriptionaction.WatchListUpdateSubscriptionParam.*
import com.stockbit.ministockbit.ui.watchlist.list.WatchListItemUiModel
import com.stockbit.ministockbit.ui.watchlist.list.WatchListItemUiModel.WatchList.PriceChangeState
import com.stockbit.ministockbit.util.CoroutineDispatcherProvider
import com.stockbit.ministockbit.util.asConsumable
import io.mockk.coVerifyAll
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
class WatchListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testCoroutineDispatcher = TestCoroutineDispatcher()
    private val coroutineDispatcherProvider =
        CoroutineDispatcherProvider(testCoroutineDispatcher, testCoroutineDispatcher)
    private lateinit var getMock: GetWatchList
    private lateinit var getUpdateMock: GetWatchListUpdate
    private lateinit var updateSubscriptionMock: WatchListUpdateSubscriptionAction
    private lateinit var sut: WatchListViewModel
    private lateinit var observerMock: Observer<WatchListState>
    private lateinit var dummyUpdateFlow: Channel<WatchListUpdate>

    @Before
    fun setup() {
        Dispatchers.setMain(testCoroutineDispatcher)
        getMock = mockk()
        getUpdateMock = mockk()
        updateSubscriptionMock = mockk(relaxed = true)
        dummyUpdateFlow = Channel()
        every { getUpdateMock(any()) } returns dummyUpdateFlow.consumeAsFlow()
        every { updateSubscriptionMock(any()) } returns Unit
        sut = WatchListViewModel(
            coroutineDispatcherProvider,
            getMock,
            getUpdateMock,
            updateSubscriptionMock
        )
        observerMock = mockk(relaxed = true)
        sut.state.observeForever(observerMock)
    }

    @Test
    fun `when load success with less data PER_PAGE_SIZE, should notify with correct state and load on second time should be ignored`() {
        // return data less then PER_PAGE_SIZE
        every { getMock(any()) } returns flowOf(
            Resource.Success(
                listOf(WatchList("a", "aaa", 200.0, 100.0))
            )
        )

        sut.load()
        sut.load() // second load should be ignored, because returned data is less than page size which indicates has reach end

        coVerifyAll {
            // Subscribe on init
            getUpdateMock(any())
            // Get on load
            getMock(GetWatchListParam(0, 10))
            // Subscribe on load success
            updateSubscriptionMock(Subscribe(listOf("a")))
        }

        verifyAll {
            // initial state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // loading state
            observerMock.onChanged(
                WatchListState(
                    loading = true,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // load success state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data =
                    listOf(
                        WatchListItemUiModel.WatchList(
                            "a",
                            "aaa",
                            "200",
                            "100(100%)",
                            PriceChangeState.Increase,
                            priceOpen24Hour = 100.0
                        ),
                    ).asConsumable
                )
            )
        }
    }

    @Test
    fun `when load success with enough data PER_PAGE_SIZE, should notify with correct state and load on second time should be increase page`() {
        val dummy = List(19) {
            WatchList("$it", "$it$it", 200.0, 100.0)
        }
        // return data enough PER_PAGE_SIZE
        every { getMock(any()) } returns flowOf(Resource.Success(dummy.take(10)))

        sut.load()

        // return data less PER_PAGE_SIZE
        every { getMock(any()) } returns flowOf(Resource.Success(dummy.takeLast(9)))
        sut.load() // second load should load with page increased

        coVerifyAll {
            // Subscribe on init
            getUpdateMock(any())
            // Get on load
            getMock(GetWatchListParam(0, 10))
            // Subscribe on load success
            updateSubscriptionMock(
                Subscribe(
                    dummy.take(10).map { it.name })
            )
            // Get second load
            getMock(GetWatchListParam(1, 10))
            // Second subscribe on load success
            updateSubscriptionMock(
                Subscribe(
                    dummy.takeLast(9).map { it.name })
            )
        }

        verifyAll {
            // initial state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // loading state
            observerMock.onChanged(
                WatchListState(
                    loading = true,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // load success state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = buildList {
                        addAll(dummy.take(10).map { WatchListItemUiModel.WatchList.from(it) })
                        add(WatchListItemUiModel.Loading)
                    }.asConsumable
                )
            )
            // second loading state
            observerMock.onChanged(
                WatchListState(
                    loading = true,
                    error = null,
                    data = buildList {
                        addAll(dummy.take(10).map { WatchListItemUiModel.WatchList.from(it) })
                        add(WatchListItemUiModel.Loading)
                    }.asConsumable
                )
            )
            // second load success state should not have loading in the end because we reach the end
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = dummy.map { WatchListItemUiModel.WatchList.from(it) }.asConsumable
                )
            )
        }
    }

    @Test
    fun `when load success with duplicate data, should notify with correct state and not contains duplicate data`() {
        val dummy = List(10) {
            WatchList("$it", "$it$it", 200.0, 100.0)
        }

        every { getMock(any()) } returns flowOf(Resource.Success(dummy))

        sut.load()

        // return data with duplicate
        sut.load()

        coVerifyAll {
            // Subscribe on init
            getUpdateMock(any())
            // Get on load
            getMock(GetWatchListParam(0, 10))
            // Subscribe on load success
            updateSubscriptionMock(
                Subscribe(
                    dummy.map { it.name })
            )
            // Get second load
            getMock(GetWatchListParam(1, 10))
            // Second subscribe on load success
            updateSubscriptionMock(
                Subscribe(
                    dummy.map { it.name })
            )
        }

        verifyAll {
            // initial state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // loading state
            observerMock.onChanged(
                WatchListState(
                    loading = true,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // load success state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = buildList {
                        addAll(dummy.map { WatchListItemUiModel.WatchList.from(it) })
                        add(WatchListItemUiModel.Loading)
                    }.asConsumable
                )
            )
            // second loading state
            observerMock.onChanged(
                WatchListState(
                    loading = true,
                    error = null,
                    data = buildList {
                        addAll(dummy.map { WatchListItemUiModel.WatchList.from(it) })
                        add(WatchListItemUiModel.Loading)
                    }.asConsumable
                )
            )
            // second load success state should not have duplicate
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = buildList {
                        addAll(dummy.map { WatchListItemUiModel.WatchList.from(it) })
                        add(WatchListItemUiModel.Loading)
                    }.asConsumable
                )
            )
        }
    }

    @Test
    fun `when load success and receive update from websocket, should notify with correct state and should changed updated data from websocket`() {
        val dummy = WatchList("a", "aa", 200.0, 100.0)

        every { getMock(any()) } returns flowOf(Resource.Success(listOf(dummy)))

        sut.load()

        // simulate websocket update
        runBlocking { dummyUpdateFlow.send(WatchListUpdate("a", 300.0)) }

        coVerifyAll {
            // Subscribe on init
            getUpdateMock(any())
            // Get on load
            getMock(GetWatchListParam(0, 10))
            // Subscribe on load success
            updateSubscriptionMock(
                Subscribe(listOf("a"))
            )
        }

        verifyAll {
            // initial state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // loading state
            observerMock.onChanged(
                WatchListState(
                    loading = true,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // load success state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = listOf(WatchListItemUiModel.WatchList.from(dummy)).asConsumable
                )
            )
            // update from websocket state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = listOf(
                        WatchListItemUiModel.WatchList(
                            "a",
                            "aa",
                            "300",
                            "200(200%)",
                            PriceChangeState.Increase,
                            100.0
                        )
                    ).asConsumable
                )
            )
        }
    }

    @Test
    fun `when load error, should notify with correct state`() {
        every { getMock(any()) } returns flowOf(Resource.Error("Timeout"))

        sut.load()

        coVerifyAll {
            // Subscribe on init
            getUpdateMock(any())
            // Get on load
            getMock(GetWatchListParam(0, 10))
        }

        verifyAll {
            // initial state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // loading state
            observerMock.onChanged(
                WatchListState(
                    loading = true,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // load error state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = "Timeout".asConsumable,
                    data = listOf(WatchListItemUiModel.Error).asConsumable
                )
            )
        }
    }

    @Test
    fun `when load success with loadMore false, should notify with correct state and unsubscribe all`() {
        // return data less then PER_PAGE_SIZE
        every { getMock(any()) } returns flowOf(
            Resource.Success(
                listOf(WatchList("a", "aaa", 200.0, 100.0))
            )
        )

        sut.load(false)

        coVerifyAll {
            // Subscribe on init
            getUpdateMock(any())
            // Unsubscribe all
            updateSubscriptionMock(UnsubscribeAll)
            // Get on load
            getMock(GetWatchListParam(0, 10))
            // Subscribe on load success
            updateSubscriptionMock(Subscribe(listOf("a")))
        }

        verifyAll {
            // initial state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // loading state
            observerMock.onChanged(
                WatchListState(
                    loading = true,
                    error = null,
                    data = listOf(WatchListItemUiModel.Loading).asConsumable
                )
            )
            // load success state
            observerMock.onChanged(
                WatchListState(
                    loading = false,
                    error = null,
                    data =
                    listOf(
                        WatchListItemUiModel.WatchList(
                            "a",
                            "aaa",
                            "200",
                            "100(100%)",
                            PriceChangeState.Increase,
                            priceOpen24Hour = 100.0
                        ),
                    ).asConsumable
                )
            )
        }
    }
}