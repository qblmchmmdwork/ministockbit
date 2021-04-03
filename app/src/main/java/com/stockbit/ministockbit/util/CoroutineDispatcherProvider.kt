package com.stockbit.ministockbit.util

import kotlinx.coroutines.CoroutineDispatcher

data class CoroutineDispatcherProvider(val main: CoroutineDispatcher, val io: CoroutineDispatcher)