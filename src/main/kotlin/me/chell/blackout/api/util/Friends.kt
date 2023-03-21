package me.chell.blackout.api.util

import net.minecraft.entity.player.PlayerEntity

val friends = mutableListOf<String>()

fun PlayerEntity.isFriend() = friends.contains(name.string)

fun isFriend(name: String) = friends.contains(name)