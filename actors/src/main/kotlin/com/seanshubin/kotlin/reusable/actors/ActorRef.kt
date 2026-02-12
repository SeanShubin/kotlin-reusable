package com.seanshubin.kotlin.reusable.actors

interface ActorRef<T> {
    suspend fun send(msg: T)
    fun trySend(msg: T): Boolean
}

class LocalActorRef<T>(private val actor: Actor<T>) : ActorRef<T> {
    override suspend fun send(msg: T) = actor.send(msg)
    override fun trySend(msg: T) = actor.trySend(msg)
}
