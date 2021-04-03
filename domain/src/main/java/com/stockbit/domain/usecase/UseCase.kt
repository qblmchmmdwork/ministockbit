package com.stockbit.domain.usecase

interface UseCase<P,T> {
    operator fun invoke(data: P) : T
}