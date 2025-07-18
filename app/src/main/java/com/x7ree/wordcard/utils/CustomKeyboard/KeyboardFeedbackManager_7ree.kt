package com.x7ree.wordcard.utils.CustomKeyboard

import android.content.Context
import android.media.AudioManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.SoundEffectConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

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
        try {
            // 播放系统按键音效
            audioManager.playSoundEffect(SoundEffectConstants.CLICK)
        } catch (e: Exception) {
            e.printStackTrace()
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