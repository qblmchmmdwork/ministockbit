package com.stockbit.ministockbit.util

class Consumable<T>(private var data: T) {
    @Suppress("MemberVisibilityCanBePrivate")
    var isConsumed: Boolean = false
        private set

    @Suppress("MemberVisibilityCanBePrivate")
    fun consume(): T? {
        return if (!isConsumed) {
            isConsumed = true; data
        } else null
    }

    fun consume(block: (T)->Unit) {
        block(consume()?:return)
    }

    fun peek(): T = data
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Consumable<*>

        if (data != other.data) return false
        if (isConsumed != other.isConsumed) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data?.hashCode() ?: 0
        result = 31 * result + isConsumed.hashCode()
        return result
    }

    override fun toString(): String {
        return "Consumable(data=$data, isConsumed=$isConsumed)"
    }


}

val<T> T.asConsumable : Consumable<T> get() = Consumable(this)