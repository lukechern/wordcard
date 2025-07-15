package com.x7ree.wordcard.widget

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.x7ree.wordcard.MainActivity
import com.x7ree.wordcard.R
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.data.WordDatabase_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import io.noties.markwon.Markwon
import com.x7ree.wordcard.widget.WidgetMarkdownParser_7ree
import com.x7ree.wordcard.utils.showKeyboardWithDelay_7ree
import com.x7ree.wordcard.utils.hideKeyboard_7ree
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import android.view.MotionEvent
import android.animation.ObjectAnimator
import android.animation.AnimatorSet

class WidgetConfigActivity_7ree : AppCompatActivity(), TextToSpeech.OnInitListener {
    
    private lateinit var wordRepository_7ree: WordRepository_7ree
    private lateinit var apiService_7ree: OpenAiApiService_7ree
    private var tts_7ree: TextToSpeech? = null
    private var isTtsReady_7ree = false
    private var currentQueryWord_7ree = ""
    private val TAG_7ree = "WidgetConfigActivity_7ree"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 隐藏标题栏
        supportActionBar?.hide()
        
        // 设置窗口参数
        window?.let { window ->
            window.attributes?.let { params ->
                params.width = WindowManager.LayoutParams.WRAP_CONTENT
                params.height = WindowManager.LayoutParams.WRAP_CONTENT
                
                // 将窗口向上移动屏幕高度的25%
                val displayMetrics = resources.displayMetrics
                val screenHeight = displayMetrics.heightPixels
                params.y = -(screenHeight * 0.25).toInt()
                
                window.attributes = params
            }
        }
        
        setContentView(R.layout.activity_widget_config_7ree)
        
        // 初始化依赖
        val database = WordDatabase_7ree.getDatabase_7ree(this)
        wordRepository_7ree = WordRepository_7ree(database.wordDao_7ree())
        
        // 初始化API服务并加载用户配置
        apiService_7ree = OpenAiApiService_7ree()
        val configManager_7ree = AppConfigManager_7ree(this)
        val apiConfig_7ree = configManager_7ree.loadApiConfig_7ree()
        val promptConfig_7ree = configManager_7ree.loadPromptConfig_7ree()
        apiService_7ree.updateApiConfig_7ree(apiConfig_7ree)
        apiService_7ree.updatePromptConfig_7ree(promptConfig_7ree)
        
        val inputText = findViewById<EditText>(R.id.widget_input_config_7ree)
        val queryButton = findViewById<Button>(R.id.widget_query_button_config_7ree)
        
        // 1. 自动聚焦到输入框并打开键盘
        inputText.showKeyboardWithDelay_7ree()
        
        // 2. 初始状态按钮为灰色无效
        updateButtonState_7ree(queryButton, false)
        
        // 3. 限制只能输入英文字母
        inputText.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val filtered = StringBuilder()
            for (i in start until end) {
                val char = source[i]
                // 只允许英文字母 (a-z, A-Z)
                if ((char in 'a'..'z') || (char in 'A'..'Z')) {
                    filtered.append(char)
                }
            }
            if (filtered.length == end - start) {
                null // 接受所有字符
            } else {
                filtered.toString() // 返回过滤后的字符
            }
        })
        
        // 监听文本变化，动态更新按钮状态
        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s?.toString()?.trim() ?: ""
                updateButtonState_7ree(queryButton, text.length >= 3)
            }
        })
        
        // 5. 回车键触发搜索
        inputText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                performSearch_7ree(inputText, queryButton)
                true
            } else {
                false
            }
        }
        
        // 按钮点击事件
        queryButton.setOnClickListener {
            performSearch_7ree(inputText, queryButton)
        }
        
        // 初始化TTS（在API查询之后才初始化，避免影响用户体验）
        // TTS将在查询完成后懒加载初始化
    }
    
    private fun updateButtonState_7ree(button: Button, enabled: Boolean) {
        button.isEnabled = enabled
        if (enabled) {
            button.background = ContextCompat.getDrawable(this, R.drawable.widget_green_button_background_7ree)
        } else {
            button.background = ContextCompat.getDrawable(this, R.drawable.widget_gray_button_background_7ree)
        }
        
        // 创建带图标的文字
        val buttonText = "用AI查询"
        val spannableString = SpannableString(" $buttonText")
        
        // 获取放大镜图标
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_search_magnifier_7ree)
        drawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            // 使用ALIGN_BOTTOM来降低图标位置
            val imageSpan = ImageSpan(it, ImageSpan.ALIGN_BOTTOM)
            spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        
        button.text = spannableString
    }
    
    private fun performSearch_7ree(inputText: EditText, queryButton: Button) {
        // 收起键盘
        inputText.hideKeyboard_7ree()
        
        val queryText = inputText.text.toString().trim()
        
        if (queryText.length >= 3 && queryButton.isEnabled) {
            // 获取主界面的UI元素
            val progressBar = findViewById<ProgressBar>(R.id.widget_progress_bar_7ree)
            val resultText = findViewById<TextView>(R.id.widget_result_text_7ree)
            val wordTitle = findViewById<TextView>(R.id.widget_word_title_7ree)
            val chineseMeaning = findViewById<TextView>(R.id.widget_chinese_meaning_7ree)
            val loadingText = findViewById<TextView>(R.id.widget_loading_text_7ree)
            val resultButtons = findViewById<LinearLayout>(R.id.widget_result_buttons_7ree)
            
            // 立即切换到结果模式
            // 隐藏输入框和查询按钮
            inputText.visibility = View.GONE
            queryButton.visibility = View.GONE
            
            // 显示查询的单词标题
            wordTitle.text = queryText
            wordTitle.visibility = View.VISIBLE
            
            // 显示进度条和提示文字
            progressBar.visibility = View.VISIBLE
            loadingText.visibility = View.VISIBLE
            
            // 隐藏其他元素
            resultText.visibility = View.GONE
            chineseMeaning.visibility = View.GONE
            resultButtons.visibility = View.GONE
            
            // 禁用查询按钮防止重复查询
            queryButton.isEnabled = false
            
            // 执行查询
            lifecycleScope.launch {
                try {
                    // 先查询本地缓存
                    val cachedWord = wordRepository_7ree.getWord_7ree(queryText)
                    
                    if (cachedWord != null) {
                        // 从缓存获取结果
                        val extractedInfo = extractBasicInfo_7ree(cachedWord.apiResult)
                        displayResult_7ree(extractedInfo, queryText, progressBar, resultText, wordTitle, chineseMeaning, inputText, queryButton)
                        
                        // 增加浏览次数
                        wordRepository_7ree.incrementViewCount_7ree(queryText)
                        
                        // 保存当前查询的单词并初始化TTS
                        currentQueryWord_7ree = queryText
                        initializeTtsLazy_7ree()
                    } else {
                        // 发起API请求
                        var fullResult = ""
                        apiService_7ree.queryWordStreamSimple_7ree(queryText).collect { chunk ->
                            fullResult += chunk
                            
                            // 实时更新显示内容
                            val extractedInfo = extractBasicInfo_7ree(fullResult)
                            if (extractedInfo.first.isNotBlank() || extractedInfo.second.isNotBlank()) {
                                displayResult_7ree(extractedInfo, queryText, progressBar, resultText, wordTitle, chineseMeaning, inputText, queryButton)
                            }
                        }
                        
                        // 保存到数据库
                        if (fullResult.isNotBlank() && !fullResult.startsWith("错误:")) {
                            wordRepository_7ree.saveWord_7ree(queryText, fullResult)
                        }
                        
                        // 保存当前查询的单词并初始化TTS
                        currentQueryWord_7ree = queryText
                        initializeTtsLazy_7ree()
                    }
                } catch (e: Exception) {
                    progressBar.visibility = View.GONE
                    loadingText.visibility = View.GONE
                    resultText.text = "查询失败: ${e.localizedMessage}"
                    resultText.visibility = View.VISIBLE
                } finally {
                    // 重新启用查询按钮
                    queryButton.isEnabled = true
                }
            }
        }
    }
    
    private fun displayResult_7ree(
        extractedInfo: Pair<String, String>,
        queryText: String,
        progressBar: ProgressBar,
        resultText: TextView,
        wordTitle: TextView,
        chineseMeaning: TextView,
        inputText: EditText,
        queryButton: Button
    ) {
        progressBar.visibility = View.GONE
        
        // 隐藏加载提示文字
        val loadingText = findViewById<TextView>(R.id.widget_loading_text_7ree)
        loadingText.visibility = View.GONE
        
        // 从Pair中获取主要内容和详细信息
        val mainContent = extractedInfo.first
        val detailInfo = extractedInfo.second
        
        // 解析完整内容获取单词和中文意思
        val parsedContent = WidgetMarkdownParser_7ree.parseBasicInfo_7ree(detailInfo)
        
        // 显示单词标题和中文意思
        wordTitle.text = if (parsedContent.word.isNotEmpty()) parsedContent.word else queryText
        
        // 确保中文词义显示在副标题位置
        if (parsedContent.chineseMeaning.isNotEmpty()) {
            chineseMeaning.text = parsedContent.chineseMeaning
            chineseMeaning.gravity = android.view.Gravity.CENTER
            chineseMeaning.visibility = View.VISIBLE
        } else {
            chineseMeaning.visibility = View.GONE
        }
        
        wordTitle.visibility = View.VISIBLE
        
        // 隐藏输入框和查询按钮
        inputText.visibility = View.GONE
        queryButton.visibility = View.GONE
        
        // 使用完整的模板格式显示内容
        WidgetMarkdownParser_7ree.renderToTextView_7ree(resultText, detailInfo)
        
        resultText.visibility = View.VISIBLE
        
        // 显示结果按钮区域
        val resultButtons = findViewById<LinearLayout>(R.id.widget_result_buttons_7ree)
        resultButtons.visibility = View.VISIBLE
        
        // 设置按钮点击事件
        setupResultButtons_7ree(queryText)
    }
    
    private fun setupResultButtons_7ree(queryText: String) {
        // 朗读按钮 - 朗读当前单词
        val speakButton = findViewById<ImageView>(R.id.widget_speak_button_7ree)
        val speakText = findViewById<TextView>(R.id.widget_speak_text_7ree)
        val speakContainer = findViewById<LinearLayout>(R.id.widget_speak_container_7ree)
        
        // 添加触摸反馈效果
        addTouchFeedback_7ree(speakButton)
        addTextTouchFeedback_7ree(speakText, speakButton)
        
        speakContainer.setOnClickListener {
            speakWord_7ree(currentQueryWord_7ree)
        }
        
        // 单词本按钮 - 进入app单词本栏目
        val wordbookButton = findViewById<ImageView>(R.id.widget_wordbook_button_7ree)
        val wordbookText = findViewById<TextView>(R.id.widget_wordbook_text_7ree)
        val wordbookContainer = findViewById<LinearLayout>(R.id.widget_wordbook_container_7ree)
        
        // 添加触摸反馈效果
        addTouchFeedback_7ree(wordbookButton)
        addTextTouchFeedback_7ree(wordbookText, wordbookButton)
        
        wordbookContainer.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                action = WordQueryWidgetProvider_7ree.ACTION_WIDGET_WORDBOOK_7ree
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()
        }
        
        // 详情页按钮 - 进入app对应单词查询的详情页面
        val detailButton = findViewById<ImageView>(R.id.widget_detail_button_7ree)
        val detailText = findViewById<TextView>(R.id.widget_detail_text_7ree)
        val detailContainer = findViewById<LinearLayout>(R.id.widget_detail_container_7ree)
        
        // 添加触摸反馈效果
        addTouchFeedback_7ree(detailButton)
        addTextTouchFeedback_7ree(detailText, detailButton)
        
        detailContainer.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("query_word", queryText)
                putExtra("show_detail", true)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            startActivity(intent)
            finish()
        }
    }

    
    private fun extractBasicInfo_7ree(fullResult: String): Pair<String, String> {
        if (fullResult.isBlank()) {
            return Pair("", "")
        }
        
        // 使用WidgetMarkdownParser_7ree解析内容
        val parsedContent = WidgetMarkdownParser_7ree.parseBasicInfo_7ree(fullResult)
        
        // 构建主要显示内容（单词和中文意思）
        val mainContent = StringBuilder()
        if (parsedContent.word.isNotEmpty()) {
            mainContent.append(parsedContent.word)
        }
        if (parsedContent.chineseMeaning.isNotEmpty()) {
            if (mainContent.isNotEmpty()) mainContent.append("\n")
            mainContent.append(parsedContent.chineseMeaning)
        }
        
        // 构建完整的格式化内容
        val formattedContent = WidgetMarkdownParser_7ree.formatCompleteContent_7ree(parsedContent)
        
        return Pair(mainContent.toString(), fullResult)
    }
    
    override fun onInit(status: Int) {
        Log.d(TAG_7ree, "onInit: Received status: $status")
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG_7ree, "onInit: TextToSpeech initialized successfully.")
            
            val resultUs_7ree = tts_7ree?.setLanguage(Locale.US)
            Log.d(TAG_7ree, "onInit: setLanguage(Locale.US) result: $resultUs_7ree")
            
            if (resultUs_7ree != null && resultUs_7ree >= TextToSpeech.LANG_AVAILABLE) {
                isTtsReady_7ree = true
                Log.d(TAG_7ree, "onInit: TTS is ready for English.")
            } else {
                Log.w(TAG_7ree, "onInit: English language not supported.")
            }
        } else {
            Log.e(TAG_7ree, "onInit: TextToSpeech initialization failed with status: $status")
        }
    }
    
    private fun initializeTtsLazy_7ree() {
        if (tts_7ree != null) return // 已经初始化过，不再重复初始化
        
        try {
            Log.d(TAG_7ree, "开始懒加载初始化TTS")
            tts_7ree = TextToSpeech(this@WidgetConfigActivity_7ree, this@WidgetConfigActivity_7ree)
            Log.d(TAG_7ree, "TTS懒加载初始化请求已发送")
        } catch (e: Exception) {
            Log.e(TAG_7ree, "TTS懒加载初始化失败: ${e.message}")
        }
    }
    
    private fun speakWord_7ree(word: String) {
        if (tts_7ree != null && isTtsReady_7ree && word.isNotBlank()) {
            tts_7ree?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "widget_word")
            Log.d(TAG_7ree, "speakWord_7ree: 开始朗读单词: \"$word\"")
        } else {
            Log.w(TAG_7ree, "speakWord_7ree: TTS未准备好或单词为空")
        }
    }
    
    private fun addTouchFeedback_7ree(imageView: ImageView) {
             imageView.setOnTouchListener { view, event ->
                 if (event.action == MotionEvent.ACTION_DOWN) {
                     // 只监控第一个触摸动作，按顺序播放动画
                     view.animate()
                         .scaleX(0.7f)
                         .scaleY(0.7f)
                         .setDuration(100)
                         .withEndAction {
                             // 等待200毫秒后还原
                             view.postDelayed({
                                 view.animate()
                                     .scaleX(1.0f)
                                     .scaleY(1.0f)
                                     .setDuration(100)
                                     .start()
                             }, 200)
                         }
                         .start()
                 }
                 false // 返回false让其他点击事件继续处理
             }
         }
         
     private fun addTextTouchFeedback_7ree(textView: TextView, targetImageView: ImageView) {
             textView.setOnTouchListener { _, event ->
                 if (event.action == MotionEvent.ACTION_DOWN) {
                     // 点击文字时缩放对应的图标
                     targetImageView.animate()
                         .scaleX(0.7f)
                         .scaleY(0.7f)
                         .setDuration(100)
                         .withEndAction {
                             // 等待200毫秒后还原
                             targetImageView.postDelayed({
                                 targetImageView.animate()
                                     .scaleX(1.0f)
                                     .scaleY(1.0f)
                                     .setDuration(100)
                                     .start()
                             }, 200)
                         }
                         .start()
                 }
                 false // 返回false让其他点击事件继续处理
             }
         }
    
    override fun onDestroy() {
        super.onDestroy()
        tts_7ree?.let {
            it.stop()
            it.shutdown()
        }
        Log.d(TAG_7ree, "onDestroy: TTS resources released")
    }
}