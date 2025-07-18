package com.x7ree.wordcard.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.x7ree.wordcard.R
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.data.WordDatabase_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import kotlinx.coroutines.launch
import com.x7ree.wordcard.utils.showKeyboardWithDelay_7ree
import com.x7ree.wordcard.utils.hideKeyboard_7ree
import android.view.MotionEvent

class WidgetConfigActivity_7ree : AppCompatActivity() {
    
    private lateinit var wordRepository_7ree: WordRepository_7ree
    private lateinit var apiService_7ree: OpenAiApiService_7ree
    private var currentQueryWord_7ree = ""
    
    // 各种管理器
    private lateinit var buttonManager_7ree: WidgetButtonManager_7ree
    private lateinit var inputValidator_7ree: WidgetInputValidator_7ree
    private lateinit var uiStateManager_7ree: WidgetUIStateManager_7ree
    private lateinit var searchManager_7ree: WidgetSearchManager_7ree
    private lateinit var ttsManager_7ree: WidgetTTSManager_7ree
    private lateinit var touchFeedbackManager_7ree: WidgetTouchFeedbackManager_7ree
    private lateinit var resultButtonManager_7ree: WidgetResultButtonManager_7ree
    private lateinit var keyboardManager_7ree: WidgetKeyboardManager_7ree
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 配置窗口
        WidgetWindowManager_7ree.configureWindow_7ree(this)
        // 点击外部区域关闭Activity
        setFinishOnTouchOutside(true)
        
        setContentView(R.layout.activity_widget_config_7ree)
        
        // 初始化管理器
        initializeManagers_7ree()
        
        
        // 设置UI
        setupUI_7ree()
    }
    
    /**
     * 初始化各种管理器
     */
    private fun initializeManagers_7ree() {
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
        
        // 初始化各种管理器
        buttonManager_7ree = WidgetButtonManager_7ree(this)
        inputValidator_7ree = WidgetInputValidator_7ree(buttonManager_7ree)
        uiStateManager_7ree = WidgetUIStateManager_7ree()
        searchManager_7ree = WidgetSearchManager_7ree(wordRepository_7ree, apiService_7ree)
        ttsManager_7ree = WidgetTTSManager_7ree(this)
        touchFeedbackManager_7ree = WidgetTouchFeedbackManager_7ree()
        resultButtonManager_7ree = WidgetResultButtonManager_7ree(this, ttsManager_7ree, touchFeedbackManager_7ree)
        
        // 初始化键盘管理器
        keyboardManager_7ree = WidgetKeyboardManager_7ree(this)
        keyboardManager_7ree.initialize_7ree()
        
        // 提前初始化TTS，确保朗读功能可用
        ttsManager_7ree.initializeTtsLazy_7ree()
    }
    
    /**
     * 设置UI
     */
    private fun setupUI_7ree() {
        val inputText = findViewById<EditText>(R.id.widget_input_config_7ree)
        val queryButton = findViewById<Button>(R.id.widget_query_button_config_7ree)
        val customKeyboardContainer = findViewById<LinearLayout>(R.id.widget_custom_keyboard_container_7ree)
        val closeButton = findViewById<ImageView>(R.id.widget_close_button_7ree)

        // 设置关闭按钮点击事件
        closeButton.setOnClickListener {
            // 隐藏键盘
            keyboardManager_7ree.hideKeyboard_7ree()
            inputText.hideKeyboard_7ree()
            // 关闭Activity
            finish()
        }
        
        // 绑定键盘管理器到输入框
        keyboardManager_7ree.bindInputText_7ree(inputText, { _ -> }, {
            performSearch_7ree(inputText, queryButton)
        })
        
        // 自动弹出键盘
        inputText.requestFocus()
        keyboardManager_7ree.showCustomKeyboard_7ree()
        
        // 2. 初始状态按钮为灰色无效
        buttonManager_7ree.updateButtonState_7ree(queryButton, false)
        
        // 3. 设置输入验证
        inputValidator_7ree.setupInputFilter_7ree(inputText)
        inputValidator_7ree.setupTextWatcher_7ree(inputText, queryButton)
        inputValidator_7ree.setupEnterKeyListener_7ree(inputText) {
            performSearch_7ree(inputText, queryButton)
        }
        
        // 按钮点击事件
        queryButton.setOnClickListener {
            performSearch_7ree(inputText, queryButton)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_OUTSIDE) {
            finish()
            return true
        }
        return super.onTouchEvent(event)
    }
    

    
    private fun performSearch_7ree(inputText: EditText, queryButton: Button) {
        // 收起键盘（包括自定义键盘）
        keyboardManager_7ree.hideKeyboard_7ree()
        inputText.hideKeyboard_7ree()
        
        val queryText = inputText.text.toString().trim()
        
        if (inputValidator_7ree.isValidInput_7ree(queryText) && queryButton.isEnabled) {
            // 获取主界面的UI元素
            val progressBar = findViewById<ProgressBar>(R.id.widget_progress_bar_7ree)
            val resultText = findViewById<TextView>(R.id.widget_result_text_7ree)
            val wordTitle = findViewById<TextView>(R.id.widget_word_title_7ree)
            val chineseMeaning = findViewById<TextView>(R.id.widget_chinese_meaning_7ree)
            val loadingText = findViewById<TextView>(R.id.widget_loading_text_7ree)
            val resultButtons = findViewById<LinearLayout>(R.id.widget_result_buttons_7ree)
            
            // 切换到搜索状态
            uiStateManager_7ree.switchToSearchState_7ree(
                inputText, queryButton, progressBar, resultText, wordTitle, 
                chineseMeaning, loadingText, resultButtons, queryText
            )
            
            // 禁用查询按钮防止重复查询
            queryButton.isEnabled = false
            
            // 执行查询
            lifecycleScope.launch {
                try {
                    searchManager_7ree.searchWord_7ree(queryText).collect { searchResult ->
                        val extractedInfo = searchManager_7ree.extractBasicInfo_7ree(searchResult.content)
                        
                        if (extractedInfo.first.isNotBlank() || extractedInfo.second.isNotBlank()) {
                            displayResult_7ree(
                                extractedInfo, queryText, progressBar, resultText, 
                                wordTitle, chineseMeaning, inputText, queryButton, 
                                searchResult.isComplete
                            )
                        }
                        
                        // 如果搜索完成，保存当前查询的单词
                        if (searchResult.isComplete) {
                            currentQueryWord_7ree = queryText
                        }
                    }
                } catch (e: Exception) {
                    uiStateManager_7ree.showErrorState_7ree(
                        progressBar, loadingText, resultText, 
                        "查询失败: ${e.localizedMessage}"
                    )
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
        queryButton: Button,
        showButtons: Boolean = true // 新增参数控制是否显示按钮
    ) {
        val loadingText = findViewById<TextView>(R.id.widget_loading_text_7ree)
        val resultButtons = findViewById<LinearLayout>(R.id.widget_result_buttons_7ree)
        
        // 切换到结果显示状态
        uiStateManager_7ree.switchToResultState_7ree(
            progressBar, loadingText, resultText, wordTitle, chineseMeaning,
            inputText, queryButton, resultButtons, showButtons
        )
        
        // 从Pair中获取详细信息
        val detailInfo = extractedInfo.second
        
        // 解析完整内容获取单词和中文意思
        val parsedContent = WidgetMarkdownParser_7ree.parseBasicInfo_7ree(detailInfo)
        
        // 显示单词标题
        wordTitle.text = if (parsedContent.word.isNotEmpty()) parsedContent.word else queryText
        
        // 更新中文意思显示
        uiStateManager_7ree.updateChineseMeaning_7ree(chineseMeaning, parsedContent.chineseMeaning)
        
        // 使用完整的模板格式显示内容
        WidgetMarkdownParser_7ree.renderToTextView_7ree(resultText, detailInfo)
        
        // 如果需要显示按钮，设置按钮点击事件
        if (showButtons) {
            // 使用解析出的单词或查询文本作为朗读内容
            val wordToSpeak = if (parsedContent.word.isNotEmpty()) parsedContent.word else queryText
            resultButtonManager_7ree.setupResultButtons_7ree(queryText, wordToSpeak)
        }
    }
    


    

    

    
    override fun onDestroy() {
        super.onDestroy()
        ttsManager_7ree.release_7ree()
        keyboardManager_7ree.release_7ree()
    }
}