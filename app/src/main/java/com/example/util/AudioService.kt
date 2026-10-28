package com.example.util

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.PlaybackParams
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

data class AudioRecordingState(
    val isRecording: Boolean = false,
    val isPaused: Boolean = false,
    val elapsedSeconds: Int = 0,
    val amplitudeLevel: Float = 0f, // 0.0 to 1.0 for UI visualizer
    val currentFilePath: String? = null
)

class AudioRecorderManager(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var currentFile: File? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _recordingState = MutableStateFlow(AudioRecordingState())
    val recordingState: StateFlow<AudioRecordingState> = _recordingState.asStateFlow()

    fun startRecording(prefix: String = "sermon"): String? {
        try {
            stopRecordingInternal(discard = true)

            val dir = File(context.filesDir, "audio_records").apply { if (!exists()) mkdirs() }
            val file = File(dir, "${prefix}_${System.currentTimeMillis()}.m4a")
            currentFile = file

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(96000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder

            _recordingState.value = AudioRecordingState(
                isRecording = true,
                isPaused = false,
                elapsedSeconds = 0,
                amplitudeLevel = 0f,
                currentFilePath = file.absolutePath
            )

            startTimerAndAmplitudeTracker()
            return file.absolutePath
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Failed to start recording", e)
            stopRecordingInternal(discard = true)
            return null
        }
    }

    private fun startTimerAndAmplitudeTracker() {
        recordingJob?.cancel()
        recordingJob = scope.launch {
            var counter = 0
            while (isActive && _recordingState.value.isRecording) {
                delay(100)
                if (!_recordingState.value.isPaused) {
                    counter++
                    val seconds = counter / 10
                    val amp = try {
                        mediaRecorder?.maxAmplitude ?: 0
                    } catch (e: Exception) {
                        0
                    }
                    val normalizedAmp = (amp.toFloat() / 32767f).coerceIn(0.05f, 1.0f)
                    _recordingState.value = _recordingState.value.copy(
                        elapsedSeconds = seconds,
                        amplitudeLevel = normalizedAmp
                    )
                }
            }
        }
    }

    fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && _recordingState.value.isRecording && !_recordingState.value.isPaused) {
            try {
                mediaRecorder?.pause()
                _recordingState.value = _recordingState.value.copy(isPaused = true)
            } catch (e: Exception) {
                Log.e("AudioRecorderManager", "Error pausing recorder", e)
            }
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && _recordingState.value.isRecording && _recordingState.value.isPaused) {
            try {
                mediaRecorder?.resume()
                _recordingState.value = _recordingState.value.copy(isPaused = false)
            } catch (e: Exception) {
                Log.e("AudioRecorderManager", "Error resuming recorder", e)
            }
        }
    }

    fun stopRecording(): Pair<String?, Int> {
        val path = currentFile?.absolutePath
        val duration = _recordingState.value.elapsedSeconds
        stopRecordingInternal(discard = false)
        return Pair(path, duration)
    }

    fun cancelRecording() {
        stopRecordingInternal(discard = true)
    }

    private fun stopRecordingInternal(discard: Boolean) {
        recordingJob?.cancel()
        recordingJob = null

        try {
            mediaRecorder?.apply {
                try {
                    stop()
                } catch (e: Exception) {
                    Log.w("AudioRecorderManager", "Stop called in invalid state", e)
                }
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderManager", "Error releasing recorder", e)
        } finally {
            mediaRecorder = null
        }

        if (discard) {
            try {
                currentFile?.let { if (it.exists()) it.delete() }
            } catch (e: Exception) {
                Log.e("AudioRecorderManager", "Error deleting cancelled audio file", e)
            }
            currentFile = null
        }

        _recordingState.value = AudioRecordingState(
            isRecording = false,
            isPaused = false,
            elapsedSeconds = 0,
            amplitudeLevel = 0f,
            currentFilePath = null
        )
    }
}

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val currentPositionMs: Int = 0,
    val totalDurationMs: Int = 0,
    val playingPath: String? = null,
    val speed: Float = 1.0f
)

class AudioPlayerManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    fun play(filePath: String, onComplete: (() -> Unit)? = null) {
        val file = File(filePath)
        if (!file.exists()) {
            Log.w("AudioPlayerManager", "Audio file not found: $filePath")
            return
        }

        // If currently playing the same file and paused, resume
        if (_playerState.value.playingPath == filePath && _playerState.value.isPaused && mediaPlayer != null) {
            resume()
            return
        }

        // Otherwise start new playback
        stop()

        try {
            val player = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                setOnCompletionListener {
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isPaused = false,
                        currentPositionMs = 0
                    )
                    playbackJob?.cancel()
                    onComplete?.invoke()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("AudioPlayerManager", "MediaPlayer error: what=$what extra=$extra")
                    stop()
                    true
                }
            }

            mediaPlayer = player
            val duration = player.duration
            applySpeed(player, _playerState.value.speed)
            player.start()

            _playerState.value = _playerState.value.copy(
                isPlaying = true,
                isPaused = false,
                currentPositionMs = 0,
                totalDurationMs = duration,
                playingPath = filePath
            )

            startProgressTracker()
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error starting playback", e)
            stop()
        }
    }

    private fun startProgressTracker() {
        playbackJob?.cancel()
        playbackJob = scope.launch {
            while (isActive && _playerState.value.isPlaying) {
                delay(150)
                try {
                    val pos = mediaPlayer?.currentPosition ?: 0
                    val dur = mediaPlayer?.duration ?: _playerState.value.totalDurationMs
                    _playerState.value = _playerState.value.copy(
                        currentPositionMs = pos,
                        totalDurationMs = dur
                    )
                } catch (e: Exception) {
                    break
                }
            }
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isPaused = true
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error pausing audio", e)
        }
    }

    fun resume() {
        try {
            mediaPlayer?.let {
                it.start()
                _playerState.value = _playerState.value.copy(
                    isPlaying = true,
                    isPaused = false
                )
                startProgressTracker()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error resuming audio", e)
        }
    }

    fun togglePlayPause(filePath: String) {
        if (_playerState.value.playingPath == filePath) {
            if (_playerState.value.isPlaying) {
                pause()
            } else if (_playerState.value.isPaused) {
                resume()
            } else {
                play(filePath)
            }
        } else {
            play(filePath)
        }
    }

    fun seekTo(positionMs: Int) {
        try {
            mediaPlayer?.seekTo(positionMs)
            _playerState.value = _playerState.value.copy(currentPositionMs = positionMs)
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error seeking audio", e)
        }
    }

    fun setSpeed(speed: Float) {
        _playerState.value = _playerState.value.copy(speed = speed)
        mediaPlayer?.let { player ->
            applySpeed(player, speed)
        }
    }

    private fun applySpeed(player: MediaPlayer, speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val params = PlaybackParams().apply { this.speed = speed }
                player.playbackParams = params
            } catch (e: Exception) {
                Log.w("AudioPlayerManager", "Cannot set playback speed", e)
            }
        }
    }

    fun stop() {
        playbackJob?.cancel()
        playbackJob = null
        try {
            mediaPlayer?.apply {
                if (isPlaying) stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayerManager", "Error stopping player", e)
        } finally {
            mediaPlayer = null
        }
        _playerState.value = _playerState.value.copy(
            isPlaying = false,
            isPaused = false,
            currentPositionMs = 0,
            playingPath = null
        )
    }

    fun release() {
        stop()
    }

    companion object {
        fun formatTime(seconds: Int): String {
            val m = seconds / 60
            val s = seconds % 60
            return String.format("%02d:%02d", m, s)
        }

        fun formatTimeMs(millis: Int): String {
            return formatTime(millis / 1000)
        }
    }
}
