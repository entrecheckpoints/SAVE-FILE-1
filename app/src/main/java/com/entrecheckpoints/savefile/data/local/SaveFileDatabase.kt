package com.entrecheckpoints.savefile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NoteEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SaveFileDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile private var INSTANCE: SaveFileDatabase? = null

        fun get(context: Context): SaveFileDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                SaveFileDatabase::class.java,
                "save-file.db"
            ).build().also { INSTANCE = it }
        }
    }
}
