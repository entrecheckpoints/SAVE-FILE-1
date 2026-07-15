package com.entrecheckpoints.savefile.domain

import kotlin.math.sqrt

object LevelSystem {
    fun levelForXp(xp: Int): Int = (sqrt(xp.coerceAtLeast(0) / 90.0).toInt() + 1)
    fun xpAtLevel(level: Int): Int = ((level.coerceAtLeast(1) - 1) * (level - 1) * 90)
    fun xpForNextLevel(level: Int): Int = level.coerceAtLeast(1) * level * 90

    fun progress(xp: Int): Float {
        val level = levelForXp(xp)
        val start = xpAtLevel(level)
        val end = xpForNextLevel(level)
        return ((xp - start).toFloat() / (end - start).coerceAtLeast(1)).coerceIn(0f, 1f)
    }
}
