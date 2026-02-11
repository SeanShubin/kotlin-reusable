package com.seanshubin.kotlin.reusable.parallel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

class ConcurrentParallelExecutor : ParallelExecutor {
    override fun <T, R> execute(items: List<T>, operation: (T) -> R): List<R> {
        return runBlocking(Dispatchers.Default) {
            items.map { item ->
                async {
                    operation(item)
                }
            }.awaitAll()
        }
    }
}
