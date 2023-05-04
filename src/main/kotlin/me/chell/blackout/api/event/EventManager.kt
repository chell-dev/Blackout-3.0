package me.chell.blackout.api.event

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.jvmErasure

object EventManager {
    private val registered = ConcurrentHashMap<KClass<out Event>, MutableList<Pair<Any, KFunction<*>>>>()

    fun register(obj: Any) {
        for (f in obj::class.declaredFunctions) {
            if (f.annotations.none { it is EventHandler }) continue

            this.registered.getOrPut(f.eventType, ::CopyOnWriteArrayList).add(Pair(obj, f))
        }
    }

    fun unregister(obj: Any) {
        for (f in obj::class.declaredFunctions) {
            if (f.annotations.none { it is EventHandler }) continue

            this.registered[f.eventType]?.removeIf { it.second == f }
        }
    }

    fun post(event: Event) {
        for (pair in (event::class.superclasses + event::class).flatMap {
            this.registered[it] ?: listOf()
        }) pair.second.call(pair.first, event)
    }
}

private val <T> KFunction<T>.eventType
    get() = this.parameters[1].type.jvmErasure as KClass<out Event>
