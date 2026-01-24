package com.chifunt.chromaticharptabs.ui.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class MicrophonePitchDetector(
    private val sampleRate: Int = 44100,
    private val minFrequency: Float = 200f,
    private val maxFrequency: Float = 2500f,
    private val amplitudeThreshold: Float = 0.02f,
    private val onPitchDetected: (Float?) -> Unit
) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var audioRecord: AudioRecord? = null
    private var worker: Thread? = null

    @Volatile
    private var running = false

    fun start() {
        if (running) return
        val minBufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
            return
        }
        val bufferSize = max(minBufferSize, 2048)
        try {
            val record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize
            )
            if (record.state != AudioRecord.STATE_INITIALIZED) {
                record.release()
                return
            }
            audioRecord = record
            record.startRecording()
            running = true

            worker = Thread {
                val buffer = ShortArray(bufferSize / 2)
                while (running) {
                    val read = record.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        val pitch = detectPitch(buffer, read, sampleRate, minFrequency, maxFrequency, amplitudeThreshold)
                        mainHandler.post { onPitchDetected(pitch) }
                    }
                }
            }.apply { start() }
        } catch (_: SecurityException) {
            onPitchDetected(null)
        }
    }

    fun stop() {
        running = false
        worker?.join(200)
        worker = null
        val record = audioRecord
        audioRecord = null
        if (record != null) {
            try {
                record.stop()
            } catch (_: IllegalStateException) {
                // Best-effort shutdown.
            }
            record.release()
        }
    }

    private fun detectPitch(
        buffer: ShortArray,
        read: Int,
        sampleRate: Int,
        minFrequency: Float,
        maxFrequency: Float,
        amplitudeThreshold: Float
    ): Float? {
        if (read <= 0) return null
        var sum = 0.0
        for (i in 0 until read) {
            val sample = buffer[i].toDouble() / Short.MAX_VALUE
            sum += sample * sample
        }
        val rms = sqrt(sum / read)
        if (rms < amplitudeThreshold) return null

        val minLag = (sampleRate / maxFrequency).toInt().coerceAtLeast(1)
        val maxLag = (sampleRate / minFrequency).toInt().coerceAtLeast(minLag + 1)
        var bestLag = -1
        var bestCorrelation = 0f
        for (lag in minLag..maxLag) {
            var correlation = 0f
            var i = 0
            val limit = read - lag
            while (i < limit) {
                val a = buffer[i].toFloat()
                val b = buffer[i + lag].toFloat()
                correlation += a * b
                i++
            }
            if (correlation > bestCorrelation) {
                bestCorrelation = correlation
                bestLag = lag
            }
        }
        if (bestLag <= 0 || abs(bestCorrelation) < 1e-6f) return null
        return sampleRate.toFloat() / bestLag.toFloat()
    }
}
