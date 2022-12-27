package org.nixdork.klog.adapters.usecase

/**
 * A -> Arguments
 * R -> Result
 */
interface UseCase<A, R> {
    suspend operator fun invoke(arguments: A): R
}

/**
 * R -> Result
 */
interface UnitUseCase<R> : UseCase<Unit, R> {
    suspend operator fun invoke(): R
    override suspend operator fun invoke(arguments: Unit): R = invoke()
}
