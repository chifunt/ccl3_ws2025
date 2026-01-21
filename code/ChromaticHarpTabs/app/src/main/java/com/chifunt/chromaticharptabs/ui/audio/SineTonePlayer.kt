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
    @Volatile private var running = false
    @Volatile private var frequencyHz = 440.0
    @Volatile private var targetAmplitude = 0.0
    private var audioTrack: AudioTrack? = null
    private var playThread: Thread? = null

    fun start(frequencyHz: Double) {
        synchronized(lock) {
            this.frequencyHz = frequencyHz
            if (!running) {
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
                running = true
                playThread = thread(start = true, name = "SineTonePlayer") {
                    renderLoop(track, minBuffer)
                }
            }
            targetAmplitude = 1.0
        }
    }

    fun stop() {
        synchronized(lock) {
            if (!running) {
                return
            }
            targetAmplitude = 0.0
        }
    }

    fun release() {
        val thread: Thread?
        val track: AudioTrack?
        synchronized(lock) {
            running = false
            thread = playThread
            track = audioTrack
            playThread = null
            audioTrack = null
            targetAmplitude = 0.0
        }
        thread?.join(200)
        track?.release()
    }

    private fun renderLoop(track: AudioTrack, bufferSize: Int) {
        val buffer = ShortArray(bufferSize / 2)
        var phase = 0.0
        val baseAmplitude = Short.MAX_VALUE * 0.2
        var currentAmplitude = 0.0
        val slew = 0.01

        while (running) {
            val freq = frequencyHz
            val step = 2.0 * PI * freq / sampleRate
            val target = targetAmplitude
            for (i in buffer.indices) {
                currentAmplitude += (target - currentAmplitude) * slew
                buffer[i] = (sin(phase) * baseAmplitude * currentAmplitude).toInt().toShort()
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
