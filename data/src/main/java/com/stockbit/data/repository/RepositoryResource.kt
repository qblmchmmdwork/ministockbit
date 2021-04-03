package com.stockbit.data.repository

sealed class RepositoryResource<out T> {
    data class Success<out T>(val data: T) : RepositoryResource<T>()
    data class Error(val message: String, val cause: Throwable? = null) : RepositoryResource<Nothing>()
}