package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

interface SoundPlayer {
    fun playButtonClick()
    fun playConfirm()
    fun playAttackWhoosh()
    fun playHitImpact()
    fun playMissDodge()
    fun playComboStreak()
    fun playVictory()
    fun playDefeat()
    fun setEnabled(enabled: Boolean)
}

class NoOpSoundPlayer : SoundPlayer {
    override fun playButtonClick() = Unit
    override fun playConfirm() = Unit
    override fun playAttackWhoosh() = Unit
    override fun playHitImpact() = Unit
    override fun playMissDodge() = Unit
    override fun playComboStreak() = Unit
    override fun playVictory() = Unit
    override fun playDefeat() = Unit
    override fun setEnabled(enabled: Boolean) = Unit
}

/**
 * Synthesizes crisp 8-bit/16-bit retro chiptune sound effects via AudioTrack.
 * Fully offline, zero external audio files, and resilient on environments without audio hardware.
 */
class AudioTrackSoundPlayer(
    initialEnabled: Boolean = true
) : SoundPlayer {

    @Volatile
    private var enabled: Boolean = initialEnabled

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val sampleRate = 22050

    override fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    override fun playButtonClick() {
        playAsync {
            playTone(freqStart = 720.0, freqEnd = 960.0, durationMs = 45, wave = WaveType.SQUARE, volume = 0.25)
        }
    }

    override fun playConfirm() {
        playAsync {
            playTone(freqStart = 523.25, freqEnd = 523.25, durationMs = 70, wave = WaveType.SQUARE, volume = 0.28)
            playTone(freqStart = 783.99, freqEnd = 783.99, durationMs = 110, wave = WaveType.SQUARE, volume = 0.30)
        }
    }

    override fun playAttackWhoosh() {
        playAsync {
            playTone(freqStart = 280.0, freqEnd = 840.0, durationMs = 110, wave = WaveType.SQUARE, volume = 0.28)
        }
    }

    override fun playHitImpact() {
        playAsync {
            playNoiseAndPunch(durationMs = 150, volume = 0.42)
        }
    }

    override fun playMissDodge() {
        playAsync {
            playTone(freqStart = 640.0, freqEnd = 220.0, durationMs = 140, wave = WaveType.TRIANGLE, volume = 0.32)
        }
    }

    override fun playComboStreak() {
        playAsync {
            val notes = listOf(523.25, 659.25, 783.99, 1046.50)
            for (freq in notes) {
                playTone(freqStart = freq, freqEnd = freq, durationMs = 85, wave = WaveType.SQUARE, volume = 0.32)
            }
        }
    }

    override fun playVictory() {
        playAsync {
            val fanfare = listOf(
                523.25 to 110,
                659.25 to 110,
                783.99 to 110,
                1046.50 to 260
            )
            for ((freq, dur) in fanfare) {
                playTone(freqStart = freq, freqEnd = freq, durationMs = dur, wave = WaveType.SQUARE, volume = 0.32)
            }
        }
    }

    override fun playDefeat() {
        playAsync {
            val notes = listOf(
                392.00 to 150,
                349.23 to 150,
                311.13 to 150,
                261.63 to 300
            )
            for ((freq, dur) in notes) {
                playTone(freqStart = freq, freqEnd = freq * 0.96, durationMs = dur, wave = WaveType.TRIANGLE, volume = 0.30)
            }
        }
    }

    private fun playAsync(block: () -> Unit) {
        if (!enabled) return
        scope.launch {
            if (!enabled) return@launch
            try {
                block()
            } catch (_: Throwable) {
                // Gracefully ignore audio errors on devices/emulators without audio sink
            }
        }
    }

    private enum class WaveType {
        SQUARE,
        TRIANGLE
    }

    private fun playTone(
        freqStart: Double,
        freqEnd: Double,
        durationMs: Int,
        wave: WaveType,
        volume: Double
    ) {
        val numSamples = (sampleRate * durationMs / 1000.0).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val freq = freqStart + (freqEnd - freqStart) * t
            phase += 2.0 * PI * freq / sampleRate
            val envelope = (1.0 - t).coerceIn(0.0, 1.0)
            val raw = when (wave) {
                WaveType.SQUARE -> if (sin(phase) >= 0.0) 1.0 else -1.0
                WaveType.TRIANGLE -> 2.0 * kotlin.math.abs(2.0 * ((phase / (2.0 * PI)) % 1.0) - 1.0) - 1.0
            }
            buffer[i] = (raw * volume * envelope * Short.MAX_VALUE).toInt().toShort()
        }
        writePcmBuffer(buffer)
    }

    private fun playNoiseAndPunch(durationMs: Int, volume: Double) {
        val numSamples = (sampleRate * durationMs / 1000.0).toInt().coerceAtLeast(1)
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val t = i.toDouble() / numSamples
            val freq = 180.0 * (1.0 - 0.6 * t)
            phase += 2.0 * PI * freq / sampleRate
            val punch = if (sin(phase) >= 0.0) 1.0 else -1.0
            val noise = Random.nextDouble(-1.0, 1.0)
            val mixed = 0.55 * punch + 0.45 * noise
            val envelope = (1.0 - t) * (1.0 - t)
            buffer[i] = (mixed * volume * envelope * Short.MAX_VALUE).toInt().toShort()
        }
        writePcmBuffer(buffer)
    }

    private fun writePcmBuffer(samples: ShortArray) {
        val byteCount = samples.size * 2
        val minBuf = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(byteCount)

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBuf)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        try {
            track.write(samples, 0, samples.size)
            track.play()
            val sleepMs = (samples.size * 1000L / sampleRate) + 20L
            Thread.sleep(sleepMs)
        } finally {
            try {
                track.stop()
            } catch (_: Throwable) {
            }
            track.release()
        }
    }
}
