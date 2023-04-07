package me.chell.blackout.api.setting

import me.chell.blackout.api.util.Description
import java.util.function.Predicate

open class Setting<T>(val name: String, value: T, val min: T? = null, val max: T? = null, val visible: Predicate<Any?> = Predicate{true}): Description {
;
    var value = value
        set(newValue) {
            val old = field
            field = newValue
            onValueChanged(old, field)
        }

    open fun onValueChanged(oldValue: T, newValue: T) {}

    override var description = "No description."
}