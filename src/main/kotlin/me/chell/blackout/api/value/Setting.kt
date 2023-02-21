package me.chell.blackout.api.value

open class Setting<T>(val name: String, value: T) {

    var value = value
        set(newValue) {
            val old = field
            field = newValue
            onValueChanged(old, field)
        }

    open fun onValueChanged(oldValue: T, newValue: T) {}
}