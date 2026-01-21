package com.chifunt.chromaticharptabs.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin

class SineTonePlayer {
    private val sampleRate = 44100
    private val lock = Any()
    @Volatile private var isPlaying = false
    @Volatile private var frequencyHz = 440.0
    private var audioTrack: AudioTrack? = null
    private var playThread: Thread? = null

    fun start(frequencyHz: Double) {
        synchronized(lock) {
            this.frequencyHz = frequencyHz
            if (isPlaying) {
                // Update pitch while continuing playback.
                return
            }
            isPlaying = true
            val minBuffer = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )
            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBuffer)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            audioTrack?.play()
            val track = audioTrack ?: return
            playThread = thread(start = true, name = "SineTonePlayer") {
                renderLoop(track, minBuffer)
            }
        }
    }

    fun stop() {
        val track: AudioTrack?
        val thread: Thread?
        synchronized(lock) {
            if (!isPlaying) {
                return
            }
            isPlaying = false
            track = audioTrack
            thread = playThread
            audioTrack = null
            playThread = null
        }
        thread?.join(100)
        track?.release()
    }

    fun release() {
        stop()
    }

    private fun renderLoop(track: AudioTrack, bufferSize: Int) {
        val buffer = ShortArray(bufferSize / 2)
        var phase = 0.0
        val amplitude = Short.MAX_VALUE * 0.2

        while (isPlaying) {
            val freq = frequencyHz
            val step = 2.0 * PI * freq / sampleRate
            for (i in buffer.indices) {
                buffer[i] = (sin(phase) * amplitude).toInt().toShort()
                phase += step
                if (phase > 2.0 * PI) {
                    phase -= 2.0 * PI
                }
            }
            try {
                track.write(buffer, 0, buffer.size)
            } catch (_: IllegalStateException) {
                break
            }
        }
        try {
            track.stop()
        } catch (_: IllegalStateException) {
            // Best-effort shutdown.
        }
    }
}
