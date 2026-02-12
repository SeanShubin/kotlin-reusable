package com.seanshubin.kotlin.reusable.actors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

abstract class Actor<T>(private val scope: CoroutineScope) {
    private val mailbox = Channel<T>(Channel.UNLIMITED)

    init {
        scope.launch {
            for (msg in mailbox) {
                try {
                    onMessage(msg)
                } catch (e: Exception) {
                    onError(e, msg)
                }
            }
        }
    }

    protected abstract suspend fun onMessage(msg: T)

    protected open fun onError(e: Exception, msg: T) {
        System.err.println("Error processing message $msg: ${e.message}")
    }

    suspend fun send(msg: T) {
        mailbox.send(msg)
    }

    fun trySend(msg: T): Boolean {
        return mailbox.trySend(msg).isSuccess
    }
}
