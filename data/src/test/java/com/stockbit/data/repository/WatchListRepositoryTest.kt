package com.stockbit.data.repository

import com.stockbit.commonmodel.WatchList
import com.stockbit.data.source.remote.RemoteResource
import com.stockbit.data.source.remote.WatchListRemoteDataSource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.SocketTimeoutException


internal class WatchListRepositoryTest {
    private lateinit var remoteDataSourceMock: WatchListRemoteDataSource
    private lateinit var sut: WatchListRepository

    @Before
    fun setup() {
        remoteDataSourceMock = mockk(relaxed = true)
        sut = WatchListRepository(remoteDataSourceMock)
    }

    @Test
    fun `getWatchList success, should return correct data`() {
        val dummy = listOf(
            WatchList("name", "fullname", null, null)
        )
        coEvery { remoteDataSourceMock.getWatchList(0, 10) } returns RemoteResource.Success(dummy)

        val res = runBlocking { sut.getWatchList(0, 10) } as RepositoryResource.Success

        coVerify(exactly = 1) { remoteDataSourceMock.getWatchList(0, 10) }
        assertEquals(dummy, res.data)
    }

    @Test
    fun `getWatchList failed, should return correct data`() {
        coEvery { remoteDataSourceMock.getWatchList(0, 10) } returns
                RemoteResource.Failed(401, "Unauthorized")

        val res = runBlocking { sut.getWatchList(0, 10) } as RepositoryResource.Error

        coVerify(exactly = 1) { remoteDataSourceMock.getWatchList(0, 10) }
        assertEquals("Unauthorized", res.message)
    }

    @Test
    fun `getById error, should return correct data`() {
        coEvery { remoteDataSourceMock.getWatchList(0, 10) } returns
                RemoteResource.Error("Timeout", SocketTimeoutException())

        val res = runBlocking { sut.getWatchList(0, 10) } as RepositoryResource.Error

        coVerify(exactly = 1) { remoteDataSourceMock.getWatchList(0, 10) }
        assertEquals("Timeout", res.message)
    }
}