package me.chell.blackout.api.util

fun StringBuilder.setString(text: String): StringBuilder = this.replace(0, this.length, text)