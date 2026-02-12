package com.seanshubin.kotlin.reusable.actors

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class ActorSystem(
    private val name: String,
    context: CoroutineContext = Dispatchers.Default
) {
    private val scope = CoroutineScope(context + SupervisorJob())
    private val actors = mutableMapOf<String, Actor<*>>()

    fun <T> spawn(
        name: String,
        factory: (CoroutineScope) -> Actor<T>
    ): ActorRef<T> {
        val actor = factory(scope)
        actors[name] = actor
        return LocalActorRef(actor)
    }

    fun <T> actorOf(name: String): ActorRef<T>? {
        @Suppress("UNCHECKED_CAST")
        return actors[name]?.let { LocalActorRef(it as Actor<T>) }
    }

    fun shutdown() {
        scope.cancel()
        actors.clear()
    }
}
