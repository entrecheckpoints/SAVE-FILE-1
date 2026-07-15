package com.entrecheckpoints.savefile.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LevelSystemTest {
    @Test fun startsAtLevelOne() {
        assertEquals(1, LevelSystem.levelForXp(0))
    }

    @Test fun levelProgressStaysInsideRange() {
        assertTrue(LevelSystem.progress(9999) in 0f..1f)
    }
}
