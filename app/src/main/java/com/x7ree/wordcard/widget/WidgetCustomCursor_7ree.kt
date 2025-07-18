package com.x7ree.wordcard.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat

/**
 * Widget自定义光标组件
 * 为桌面小组件的输入框提供模拟光标效果
 */
class WidgetCustomCursor_7ree @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private val cursorPaint_7ree = Paint().apply {
        color = 0xFF333333.toInt() // 深灰色光标
        strokeWidth = 6f // 增加光标宽度
        isAntiAlias = true
    }
    
    private var isVisible_7ree = true
    private val blinkHandler_7ree = Handler(Looper.getMainLooper())
    private var blinkRunnable_7ree: Runnable? = null
    private var targetEditText_7ree: EditText? = null
    
    private val textBounds_7ree = Rect()
    
    init {
        startBlinking_7ree()
    }
    
    /**
     * 绑定目标输入框
     */
    fun bindEditText_7ree(editText: EditText) {
        targetEditText_7ree = editText
        invalidate()
    }
    
    /**
     * 开始光标闪烁动画
     */
    private fun startBlinking_7ree() {
        stopBlinking_7ree() // 先停止之前的动画
        blinkRunnable_7ree = object : Runnable {
            override fun run() {
                isVisible_7ree = !isVisible_7ree
                invalidate()
                blinkHandler_7ree.postDelayed(this, 800) // 每800ms闪烁一次，减慢闪烁速度
            }
        }
        blinkHandler_7ree.post(blinkRunnable_7ree!!)
    }
    
    /**
     * 停止光标闪烁动画
     */
    private fun stopBlinking_7ree() {
        blinkRunnable_7ree?.let {
            blinkHandler_7ree.removeCallbacks(it)
        }
    }
    
    /**
     * 显示光标
     */
    fun showCursor_7ree() {
        visibility = VISIBLE
        startBlinking_7ree()
    }
    
    /**
     * 隐藏光标
     */
    fun hideCursor_7ree() {
        visibility = GONE
        stopBlinking_7ree()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        if (!isVisible_7ree || targetEditText_7ree == null) {
            return
        }
        
        val editText = targetEditText_7ree!!
        val text = editText.text.toString()
        val textSize = editText.textSize
        val paint = editText.paint
        
        // 计算文本宽度
        paint.getTextBounds(text, 0, text.length, textBounds_7ree)
        val textWidth = if (text.isEmpty()) 0f else textBounds_7ree.width().toFloat()
        
        // 计算光标位置（文本居中时的光标位置，增加与文字的距离）
        val centerX = width / 2f
        val cursorX = centerX + textWidth / 2f + 8f // 增加8dp的距离
        
        // 计算光标高度和垂直位置，增加光标高度
        val cursorHeight = textSize * 1.0f // 增加光标高度
        val centerY = height / 2f
        val cursorTop = centerY - cursorHeight / 2f
        val cursorBottom = centerY + cursorHeight / 2f
        
        // 绘制光标
        canvas.drawLine(cursorX, cursorTop, cursorX, cursorBottom, cursorPaint_7ree)
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopBlinking_7ree()
    }
}