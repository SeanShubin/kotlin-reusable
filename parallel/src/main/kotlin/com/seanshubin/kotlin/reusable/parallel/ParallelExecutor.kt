package com.seanshubin.kotlin.reusable.parallel

interface ParallelExecutor {
    fun <T, R> execute(items: List<T>, operation: (T) -> R): List<R>
}
