package me.chell.blackout.api.events

import me.chell.blackout.api.event.Event
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult

// player right-clicks a block
abstract class PlayerInteractBlockEvent(val blockHitResult: BlockHitResult, val hand: Hand): Event() {

    class Pre(blockHitResult: BlockHitResult, hand: Hand, var canceled: Boolean): PlayerInteractBlockEvent(blockHitResult, hand)
    class Post(blockHitResult: BlockHitResult, hand: Hand): PlayerInteractBlockEvent(blockHitResult, hand)

}