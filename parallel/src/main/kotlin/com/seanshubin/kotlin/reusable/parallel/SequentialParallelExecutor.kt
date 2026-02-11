package com.seanshubin.kotlin.reusable.parallel

class SequentialParallelExecutor : ParallelExecutor {
    override fun <T, R> execute(items: List<T>, operation: (T) -> R): List<R> {
        return items.map(operation)
    }
}
