package com.shm.alphabettracer

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer

object MusicManager {
    private var player: MediaPlayer? = null
    private var currentVolume = 0.5f

    fun init(context: Context) {
        if (player == null) {
            player = MediaPlayer.create(context.applicationContext, R.raw.bg)
            player?.isLooping = true
            player?.setVolume(currentVolume, currentVolume)
            player?.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }
    }

    fun play() { player?.start() }
    fun pause() { if (player?.isPlaying == true) player?.pause() }
    fun isPlaying(): Boolean = player?.isPlaying == true

    fun setVolume(v: Float) {
        currentVolume = v.coerceIn(0f, 1f)
        player?.setVolume(currentVolume, currentVolume)
    }

    fun getVolume(): Float = currentVolume

    fun release() {
        player?.stop()
        player?.release()
        player = null
    }
}
