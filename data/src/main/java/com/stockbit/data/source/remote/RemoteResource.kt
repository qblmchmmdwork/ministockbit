package com.stockbit.data.source.remote

sealed class RemoteResource<out T> {
    data class Success<T>(val data: T): RemoteResource<T>()
    data class Failed(val statusCode: Int, val message: String): RemoteResource<Nothing>()
    data class Error(val message: String, val cause: Throwable? = null): RemoteResource<Nothing>()
}