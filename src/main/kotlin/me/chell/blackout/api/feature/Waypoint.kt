package me.chell.blackout.api.feature

import me.chell.blackout.api.util.Color
import net.minecraft.util.math.Vec3d

class Waypoint(var name: String, var pos: Vec3d, val server: String, var color: Color, var render: Boolean)

val waypoints = mutableListOf<Waypoint>()