package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.util.math.BlockPos

data class PlayerBreakBlockEvent(val blockPos: BlockPos, val phase: Phase): Event() {
    enum class Phase {
        Pre, Post
    }
}