package com.chifunt.chromaticharptabs.ui.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.sin

class SineTonePlayer {
    private val sampleRate = 44100
    private val tableSize = 2048
    private val lock = Any()
    @Volatile private var running = false
    @Volatile private var frequencyHz = 440.0
    @Volatile private var targetAmplitude = 0.0
    private var audioTrack: AudioTrack? = null
    private var playThread: Thread? = null
    private val wavetable = buildWavetable()
    private val noiseSeed = java.util.Random()

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
                    .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
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
        val attackSlew = 0.2
        val releaseSlew = 0.03

        while (running) {
            val freq = frequencyHz
            val step = freq * tableSize / sampleRate
            val target = targetAmplitude
            for (i in buffer.indices) {
                val slew = if (target > currentAmplitude) attackSlew else releaseSlew
                currentAmplitude += (target - currentAmplitude) * slew
                val sample = wavetableSample(phase)
                val noise = (noiseSeed.nextDouble() * 2.0 - 1.0) * 0.03
                buffer[i] = ((sample + noise) * baseAmplitude * currentAmplitude).toInt().toShort()
                phase += step
                if (phase >= tableSize) {
                    phase -= tableSize
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

    private fun wavetableSample(phase: Double): Double {
        val index = phase.toInt()
        val nextIndex = if (index + 1 >= tableSize) 0 else index + 1
        val frac = phase - index
        val a = wavetable[index]
        val b = wavetable[nextIndex]
        return a + (b - a) * frac
    }

    private fun buildWavetable(): DoubleArray {
        val table = DoubleArray(tableSize)
        for (i in 0 until tableSize) {
            val t = 2.0 * PI * i / tableSize
            var value = 0.0
            // Fundamental + harmonics for a reed-like timbre.
            value += sin(t) * 1.0
            value += sin(2.0 * t) * 0.35
            value += sin(3.0 * t) * 0.25
            value += sin(4.0 * t) * 0.18
            value += sin(5.0 * t) * 0.12
            value += sin(6.0 * t) * 0.08
            table[i] = value
        }
        // Normalize to [-1, 1]
        val max = table.maxOf { kotlin.math.abs(it) }.coerceAtLeast(1e-9)
        for (i in table.indices) {
            table[i] /= max
        }
        return table
    }
}
