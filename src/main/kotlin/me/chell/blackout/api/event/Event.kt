package me.chell.blackout.api.event

abstract class Event

abstract class EventCancelable(var canceled: Boolean = false) : Event()