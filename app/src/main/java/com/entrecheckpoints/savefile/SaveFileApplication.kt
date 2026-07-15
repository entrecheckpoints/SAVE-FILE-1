package com.entrecheckpoints.savefile

import android.app.Application
import com.entrecheckpoints.savefile.data.AppPreferences
import com.entrecheckpoints.savefile.data.SaveFileRepository
import com.entrecheckpoints.savefile.data.local.SaveFileDatabase

class SaveFileApplication : Application() {
    val database by lazy { SaveFileDatabase.get(this) }
    val repository by lazy { SaveFileRepository(database.noteDao()) }
    val preferences by lazy { AppPreferences(this) }
}
