package com.stockbit.domain

import android.content.Context
import androidx.startup.Initializer
import com.stockbit.domain.di.KoinContext
import com.stockbit.domain.di.libModule
import org.koin.dsl.koinApplication

@Suppress("unused")
class Initializer : Initializer<Unit> {
    override fun create(context: Context) {
        KoinContext.koinApp = koinApplication {
            modules(
                libModule
            )
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}