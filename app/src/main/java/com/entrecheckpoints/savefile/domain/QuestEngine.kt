package com.entrecheckpoints.savefile.domain

import com.entrecheckpoints.savefile.data.local.NoteEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object QuestEngine {
    private val catalog = listOf(
        DailyQuest("feelings", "CHECK YOUR HEART", "Escribe al menos 35 palabras sobre cómo te sientes.", 45, QuestType.WRITE_FEELINGS, 35),
        DailyQuest("gratitude", "SMALL TREASURE", "Guarda una cosa pequeña que agradeces hoy.", 35, QuestType.WRITE_GRATITUDE, 1),
        DailyQuest("words_80", "FIRST COMBO", "Escribe 80 palabras en una entrada.", 40, QuestType.WRITE_WORDS, 80),
        DailyQuest("words_160", "DEEP SAVE", "Llega a 160 palabras sin preocuparte por editar.", 70, QuestType.WRITE_WORDS, 160),
        DailyQuest("new_note", "NEW SLOT", "Crea una entrada nueva.", 30, QuestType.CREATE_NOTE, 1),
        DailyQuest("flow", "NO BACKSPACE ZONE", "Escribe una idea completa de corrido.", 50, QuestType.WRITE_UNINTERRUPTED, 60),
        DailyQuest("memory", "MEMORY CARD", "Describe un recuerdo que aún conservas.", 55, QuestType.WRITE_WORDS, 100)
    )

    fun dayKey(now: Long = System.currentTimeMillis()): Long =
        LocalDate.ofInstant(Instant.ofEpochMilli(now), ZoneId.systemDefault()).toEpochDay()

    fun questsForDay(day: Long): List<DailyQuest> {
        val start = Math.floorMod(day.toInt() * 31, catalog.size)
        return List(3) { catalog[(start + it * 2) % catalog.size] }.distinctBy { it.id }
    }

    fun autoCompletedQuestIds(
        quests: List<DailyQuest>,
        notes: List<NoteEntity>,
        day: Long
    ): Set<String> {
        val todayNotes = notes.filter { noteDay(it.createdAt) == day || noteDay(it.updatedAt) == day }
        return quests.filter { quest ->
            when (quest.type) {
                QuestType.CREATE_NOTE -> todayNotes.isNotEmpty()
                QuestType.WRITE_WORDS,
                QuestType.WRITE_FEELINGS,
                QuestType.WRITE_UNINTERRUPTED -> todayNotes.any { it.wordCount >= quest.target }
                QuestType.WRITE_GRATITUDE -> todayNotes.any {
                    val text = (it.title + " " + it.body).lowercase()
                    listOf("gracias", "agradezco", "agradecido", "agradecida", "valoro").any(text::contains)
                }
            }
        }.mapTo(mutableSetOf()) { it.id }
    }

    private fun noteDay(time: Long): Long =
        LocalDate.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).toEpochDay()
}
