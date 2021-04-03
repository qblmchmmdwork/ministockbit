package com.stockbit.domain.usecase.getwatchlist

import app.cash.turbine.test
import com.stockbit.commonmodel.WatchList
import com.stockbit.data.repository.RepositoryResource
import com.stockbit.data.repository.WatchListRepository
import com.stockbit.domain.Resource
import com.stockbit.domain.di.KoinContext
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import kotlin.time.ExperimentalTime


@ExperimentalTime
class GetWatchListImplTest {

    private lateinit var repositoryMock: WatchListRepository
    private lateinit var sut: GetWatchListImpl

    @Before
    fun setup() {
        repositoryMock = mockk(relaxed = true)
        KoinContext.koinApp = koinApplication {
            modules(module { single { repositoryMock } })
        }
        sut = GetWatchListImpl()
    }

    @Test
    fun `Invoke success should flow correct data`() = runBlocking {
        val dummy = listOf(
            WatchList( "name", "fullname", null, null)
        )
        coEvery { repositoryMock.getWatchList(0, 10) } returns
                RepositoryResource.Success(dummy)
        sut(GetWatchListParam(0, 10)).test {
            assertEquals(Resource.Loading, expectItem())
            assertEquals(Resource.Success(dummy), expectItem())
            expectComplete()
        }
    }

    @Test
    fun `Invoke error should flow correct data`() = runBlocking {
        coEvery { repositoryMock.getWatchList(0, 10) } returns
                RepositoryResource.Error("Err")
        sut(GetWatchListParam(0, 10)).test {
            assertEquals(Resource.Loading, expectItem())
            assertEquals(Resource.Error("Err"), expectItem())
            expectComplete()
        }
    }
}