package com.onlinechessgame.app.chess.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * High-performance SoundManager for Chess 3D.
 * Generates and plays audio clips for:
 * - Piece movement (wooden board impact)
 * - Piece capturing (dual-impact knock)
 * - Checkmate (majestic victory chime arpeggio)
 * - Check alert (warning alert tone)
 * - UI clicks (subtle crisp button tap)
 *
 * All audio clips are generated in-memory with PCM synthesis for zero-latency,
 * offline-ready, 100% dependable playback without external asset files.
 */
class SoundManager(context: Context) {

    private val audioScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var isSoundEnabled: Boolean = true

    companion object {
        private const val SAMPLE_RATE = 44100

        @Volatile
        private var INSTANCE: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // Pre-synthesized PCM buffers (ShortArray 16-bit PCM mono)
    private val moveBuffer: ShortArray by lazy { generateMoveSound() }
    private val captureBuffer: ShortArray by lazy { generateCaptureSound() }
    private val checkmateBuffer: ShortArray by lazy { generateCheckmateSound() }
    private val checkBuffer: ShortArray by lazy { generateCheckSound() }
    private val clickBuffer: ShortArray by lazy { generateClickSound() }

    fun setSoundEnabled(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    fun isSoundEnabled(): Boolean = isSoundEnabled

    /**
     * Plays a crisp wooden piece placement sound.
     */
    fun playMoveSound() {
        if (!isSoundEnabled) return
        playPcmBuffer(moveBuffer)
    }

    /**
     * Plays a sharp dual-impact piece capture sound.
     */
    fun playCaptureSound() {
        if (!isSoundEnabled) return
        playPcmBuffer(captureBuffer)
    }

    /**
     * Plays a triumphant ascending checkmate / victory fanfare chime.
     */
    fun playCheckmateSound() {
        if (!isSoundEnabled) return
        playPcmBuffer(checkmateBuffer)
    }

    /**
     * Plays a check warning sound.
     */
    fun playCheckSound() {
        if (!isSoundEnabled) return
        playPcmBuffer(checkBuffer)
    }

    /**
     * Plays a subtle, polished UI click / tap sound.
     */
    fun playClickSound() {
        if (!isSoundEnabled) return
        playPcmBuffer(clickBuffer)
    }

    private fun playPcmBuffer(buffer: ShortArray) {
        audioScope.launch {
            try {
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
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.play()

                val durationMs = ((buffer.size.toDouble() / SAMPLE_RATE) * 1000).toLong() + 50
                delay(durationMs)

                track.stop()
                track.release()
            } catch (_: Exception) {
            }
        }
    }

    // =========================================================================
    // AUDIO SYNTHESIS GENERATORS (16-bit PCM @ 44.1kHz)
    // =========================================================================

    /**
     * Wooden Chess Piece Placement: Thud + sharp transient tap.
     */
    private fun generateMoveSound(): ShortArray {
        val durationSec = 0.12
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val envThud = exp(-t * 40.0)
            val envClick = exp(-t * 85.0)

            // Low resonance (160 Hz) + mid-wood impact (420 Hz) + transient click (1100 Hz)
            val wave = 0.55 * sin(2.0 * PI * 160.0 * t) * envThud +
                    0.30 * sin(2.0 * PI * 420.0 * t) * envThud +
                    0.25 * sin(2.0 * PI * 1100.0 * t) * envClick

            val sample = (wave * Short.MAX_VALUE * 0.85).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = sample.toShort()
        }
        return buffer
    }

    /**
     * Piece Capture: Double impact knock (first piece strikes, second displaced).
     */
    private fun generateCaptureSound(): ShortArray {
        val durationSec = 0.18
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        val secondImpactTime = 0.038

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE

            // First impact
            val env1 = exp(-t * 45.0)
            val wave1 = 0.5 * sin(2.0 * PI * 220.0 * t) * env1 +
                    0.35 * sin(2.0 * PI * 780.0 * t) * env1

            // Second heavier impact
            val t2 = t - secondImpactTime
            val wave2 = if (t2 >= 0) {
                val env2 = exp(-t2 * 35.0)
                0.6 * sin(2.0 * PI * 140.0 * t2) * env2 +
                        0.4 * sin(2.0 * PI * 520.0 * t2) * env2
            } else 0.0

            val combined = (wave1 * 0.6 + wave2 * 0.7)
            val sample = (combined * Short.MAX_VALUE * 0.90).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = sample.toShort()
        }
        return buffer
    }

    /**
     * Checkmate / Victory Chime: Majestic 4-note ascending arpeggio (C5 -> E5 -> G5 -> C6).
     */
    private fun generateCheckmateSound(): ShortArray {
        val durationSec = 1.1
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        // Notes: C5 (523.25Hz), E5 (659.25Hz), G5 (783.99Hz), C6 (1046.50Hz)
        val noteStarts = doubleArrayOf(0.0, 0.16, 0.32, 0.48)
        val noteFreqs = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
        val noteDecays = doubleArrayOf(5.0, 5.0, 5.0, 3.2)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            var waveSum = 0.0

            for (n in 0..3) {
                val noteT = t - noteStarts[n]
                if (noteT >= 0) {
                    val env = exp(-noteT * noteDecays[n])
                    val freq = noteFreqs[n]
                    // Fundamental + subtle harmonic overtone (sparkle)
                    val noteWave = (sin(2.0 * PI * freq * noteT) + 0.35 * sin(2.0 * PI * freq * 2.0 * noteT)) * env
                    waveSum += noteWave * 0.35
                }
            }

            val sample = (waveSum * Short.MAX_VALUE * 0.85).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = sample.toShort()
        }
        return buffer
    }

    /**
     * Check Alert: Dual quick warning pulses (740 Hz & 880 Hz).
     */
    private fun generateCheckSound(): ShortArray {
        val durationSec = 0.22
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE

            val env1 = exp(-t * 30.0)
            val wave1 = sin(2.0 * PI * 740.0 * t) * env1

            val t2 = t - 0.09
            val wave2 = if (t2 >= 0) {
                val env2 = exp(-t2 * 25.0)
                sin(2.0 * PI * 880.0 * t2) * env2
            } else 0.0

            val combined = (wave1 * 0.5 + wave2 * 0.6)
            val sample = (combined * Short.MAX_VALUE * 0.75).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = sample.toShort()
        }
        return buffer
    }

    /**
     * UI Click: High-frequency subtle tap (1800 Hz fast decay).
     */
    private fun generateClickSound(): ShortArray {
        val durationSec = 0.04
        val numSamples = (SAMPLE_RATE * durationSec).toInt()
        val buffer = ShortArray(numSamples)

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val env = exp(-t * 110.0)
            val wave = 0.6 * sin(2.0 * PI * 1800.0 * t) * env +
                    0.4 * sin(2.0 * PI * 900.0 * t) * env

            val sample = (wave * Short.MAX_VALUE * 0.55).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
            buffer[i] = sample.toShort()
        }
        return buffer
    }
}
