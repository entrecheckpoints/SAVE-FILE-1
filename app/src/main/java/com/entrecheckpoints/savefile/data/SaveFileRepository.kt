package com.entrecheckpoints.savefile.data

import com.entrecheckpoints.savefile.data.local.NoteDao
import com.entrecheckpoints.savefile.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

class SaveFileRepository(private val noteDao: NoteDao) {
    val notes: Flow<List<NoteEntity>> = noteDao.observeAll()

    suspend fun getNote(id: Long): NoteEntity? = noteDao.getById(id)

    suspend fun saveNote(
        id: Long?,
        title: String,
        body: String,
        mood: String,
        createdAt: Long?
    ): NoteEntity {
        val now = System.currentTimeMillis()
        val normalizedTitle = title.trim().ifBlank { autoTitle(body) }
        val words = countWords(body)
        val existing = id?.let { noteDao.getById(it) }
        val note = NoteEntity(
            id = existing?.id ?: 0,
            title = normalizedTitle,
            body = body.trimEnd(),
            mood = mood,
            createdAt = existing?.createdAt ?: createdAt ?: now,
            updatedAt = now,
            wordCount = words,
            favorite = existing?.favorite ?: false
        )
        val savedId = if (existing == null) noteDao.insert(note) else {
            noteDao.update(note)
            note.id
        }
        return note.copy(id = savedId)
    }

    suspend fun delete(note: NoteEntity) = noteDao.delete(note)

    private fun autoTitle(body: String): String {
        val first = body.lineSequence().firstOrNull { it.isNotBlank() }?.trim().orEmpty()
        return first.take(40).ifBlank { "Entrada sin título" }
    }

    companion object {
        fun countWords(text: String): Int = text
            .trim()
            .split(Regex("\\s+"))
            .count { it.isNotBlank() }
    }
}
