package com.entrecheckpoints.savefile.domain

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val xp: Int,
    val type: QuestType,
    val target: Int
)

enum class QuestType {
    WRITE_WORDS,
    WRITE_FEELINGS,
    CREATE_NOTE,
    WRITE_GRATITUDE,
    WRITE_UNINTERRUPTED
}
