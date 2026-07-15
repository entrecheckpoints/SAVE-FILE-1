package com.entrecheckpoints.savefile.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.entrecheckpoints.savefile.R

class FeedbackManager(private val context: Context) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val sounds = mapOf(
        Sound.KEY to soundPool.load(context, R.raw.key_soft, 1),
        Sound.SENTENCE to soundPool.load(context, R.raw.sentence_chime, 1),
        Sound.SAVE to soundPool.load(context, R.raw.save_magic, 1),
        Sound.QUEST to soundPool.load(context, R.raw.quest_complete, 1),
        Sound.LEVEL to soundPool.load(context, R.raw.level_up, 1),
        Sound.MENU to soundPool.load(context, R.raw.menu_move, 1)
    )

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun play(sound: Sound, enabled: Boolean, volume: Float = 1f) {
        if (!enabled) return
        sounds[sound]?.let { id -> soundPool.play(id, volume, volume, 1, 0, 1f) }
    }

    fun tap(enabled: Boolean, strong: Boolean = false) {
        if (!enabled || vibrator?.hasVibrator() != true) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    if (strong) 24L else 10L,
                    if (strong) 90 else 45
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(if (strong) 24L else 10L)
        }
    }

    fun release() = soundPool.release()
}

enum class Sound { KEY, SENTENCE, SAVE, QUEST, LEVEL, MENU }
