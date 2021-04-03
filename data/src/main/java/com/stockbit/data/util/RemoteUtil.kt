package com.stockbit.data.util

import com.squareup.moshi.JsonDataException
import com.stockbit.data.source.remote.RemoteResource
import retrofit2.HttpException

suspend fun <T> safeApiCall(errorMessage: String, call: suspend () -> T): RemoteResource<T> {
    return try {
        RemoteResource.Success(call())
    } catch (e: HttpException) {
        RemoteResource.Failed(e.code(), e.message())
    } catch (e: JsonDataException) {
        RemoteResource.Error("$errorMessage: Bad Response", e)
    } catch (e: Exception) {
        RemoteResource.Error(errorMessage, e)
    }
}

