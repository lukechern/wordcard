package com.x7ree.wordcard.utils.CustomKeyboard

import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import android.media.AudioAttributes
import android.media.ToneGenerator
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.SoundEffectConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 键盘反馈管理器
 * 负责处理震动和音效反馈
 */
class KeyboardFeedbackManager_7ree(private val context: Context) {
    private val vibrator: Vibrator? by lazy {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    private val audioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    
    // 使用SoundPool来播放按键音效
    private val soundPool: SoundPool by lazy {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        SoundPool.Builder()
            .setMaxStreams(1)
            .setAudioAttributes(audioAttributes)
            .build()
    }
    
    // 按键音效资源ID（使用系统默认音效）
    private var clickSoundId: Int = -1
    
    init {
        // 初始化时加载系统按键音效
        try {
            // 这里我们使用一个简单的方法来模拟按键音效
            // 实际项目中可以添加自定义音效文件到res/raw目录
        } catch (e: Exception) {
            // Log.e("KeyboardFeedback", "Failed to load click sound", e)
        }
    }
    
    /**
     * 触发震动反馈
     */
    fun triggerHapticFeedback_7ree() {
        try {
            vibrator?.let { vib ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    // 降低30%震动力度：50ms -> 35ms
                    vib.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vib.vibrate(35)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 播放点击音效
     */
    fun playClickSound_7ree() {
        // 在后台线程中执行音效播放
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 检查系统音效是否开启
                val soundEffectsEnabled = android.provider.Settings.System.getInt(
                    context.contentResolver,
                    android.provider.Settings.System.SOUND_EFFECTS_ENABLED,
                    1
                ) == 1
                
                // 检查各种音量
                val systemVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)
                val maxSystemVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)
                val mediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                val notificationVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
                
                
                
                var soundPlayed = false
                
                // 方法1：使用AudioManager播放系统音效（主线程）
                if (soundEffectsEnabled) {
                    try {
                        android.os.Handler(android.os.Looper.getMainLooper()).post {
                            try {
                                audioManager.playSoundEffect(
                                    SoundEffectConstants.CLICK,
                                    1.0f // 最大音量
                                )
                                
                            } catch (e: Exception) {
                                
                            }
                        }
                        soundPlayed = true
                    } catch (e: Exception) {
                        
                    }
                }
                
                // 方法2：使用ToneGenerator（备用方案）
                if (!soundPlayed || systemVolume == 0) {
                    try {
                        // 使用NOTIFICATION流，因为它通常有音量
                        val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
                        
                        // 延迟释放资源
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            try {
                                toneGenerator.release()
                            } catch (e: Exception) {
                                
                            }
                        }, 150)
                        
                        
                        soundPlayed = true
                    } catch (e: Exception) {
                        
                    }
                }
                
                // 方法3：使用MUSIC流的ToneGenerator（最后备用方案）
                if (!soundPlayed && mediaVolume > 0) {
                    try {
                        val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
                        toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 60)
                        
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            try {
                                toneGenerator.release()
                            } catch (e: Exception) {
                                
                            }
                        }, 100)
                        
                        
                    } catch (e: Exception) {
                        
                    }
                }
                
            } catch (e: Exception) {
                
                e.printStackTrace()
            }
        }
    }
    
    /**
     * 释放资源
     */
    fun release_7ree() {
        try {
            soundPool.release()
        } catch (e: Exception) {
            
        }
    }
}

/**
 * 记住键盘反馈管理器
 */
@Composable
fun rememberKeyboardFeedbackManager_7ree(): KeyboardFeedbackManager_7ree {
    val context = LocalContext.current
    return remember { KeyboardFeedbackManager_7ree(context) }
}