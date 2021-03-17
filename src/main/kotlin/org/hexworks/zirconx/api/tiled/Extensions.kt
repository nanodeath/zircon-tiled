package org.hexworks.zirconx.api.tiled

fun Any.numberToDouble(): Double =
    when (this) {
        is Double -> this
        is Int -> toDouble()
        is String -> toDouble()
        else -> throw IllegalArgumentException("Not a double: $this)")
    }

fun Any.numberToLong(): Long =
    when (this) {
        is Long -> this
        is Int -> toLong()
        is String -> toLong()
        else -> throw IllegalArgumentException("Not a long: $this)")
    }