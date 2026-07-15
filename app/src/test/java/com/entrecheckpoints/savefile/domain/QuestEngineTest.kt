package com.entrecheckpoints.savefile.domain

import com.entrecheckpoints.savefile.data.local.NoteEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class QuestEngineTest {
    @Test fun createsThreeDailyQuests() {
        assertEquals(3, QuestEngine.questsForDay(12345).size)
    }

    @Test fun detectsWordQuestCompletion() {
        val day = QuestEngine.dayKey()
        val time = LocalDate.ofEpochDay(day).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val note = NoteEntity(
            id = 1,
            title = "Test",
            body = (1..200).joinToString(" ") { "word" },
            mood = "CALM",
            createdAt = time,
            updatedAt = time,
            wordCount = 200
        )
        val quests = QuestEngine.questsForDay(day)
        val completed = QuestEngine.autoCompletedQuestIds(quests, listOf(note), day)
        assertTrue(completed.all { id -> quests.any { it.id == id } })
    }
}
