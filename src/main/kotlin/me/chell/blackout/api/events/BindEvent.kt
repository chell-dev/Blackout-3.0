package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import me.chell.blackout.api.setting.Bind

class BindEvent(val bind: Bind): Event()