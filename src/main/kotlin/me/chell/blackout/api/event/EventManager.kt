package me.chell.blackout.api.event

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.jvmErasure

class EventManager {

    private val registered = mutableMapOf<KClass<out Event>, MutableList<Pair<Any, KFunction<*>>>>()

    @Suppress("unchecked_cast")
    fun register(obj: Any) {
        for(f in obj::class.declaredFunctions) {

            if(f.annotations.none { it is EventHandler }) continue

            val event = f.parameters[1].type.jvmErasure as KClass<out Event>

            if(registered.containsKey(event)) {
                registered[event]!!.add(Pair(obj, f))
            } else {
                registered[event] = mutableListOf(Pair(obj, f))
            }
        }
    }

    @Suppress("unchecked_cast")
    fun unregister(obj: Any) {
        for(f in obj::class.declaredFunctions) {

            if (f.annotations.none { it is EventHandler }) continue

            val event = f.parameters[1].type.jvmErasure as KClass<out Event>

            registered[event]?.remove(Pair(obj, f))
        }
    }

    fun post(event: Event) {
        val list = registered[event::class]?.toList() ?: return

        for(pair in list) {
            pair.second.call(pair.first, event)
        }
    }

}