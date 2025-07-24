package com.x7ree.wordcard.widget.config

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.x7ree.wordcard.R
import com.x7ree.wordcard.api.OpenAiApiService_7ree
import com.x7ree.wordcard.config.AppConfigManager_7ree
import com.x7ree.wordcard.data.WordDatabase_7ree
import com.x7ree.wordcard.data.WordRepository_7ree
import com.x7ree.wordcard.utils.hideKeyboard_7ree
import com.x7ree.wordcard.widget.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

open class WidgetConfigBaseActivity_7ree : AppCompatActivity() {
    
    protected lateinit var wordRepository_7ree: WordRepository_7ree
    protected lateinit var apiService_7ree: OpenAiApiService_7ree
    protected var currentQueryWord_7ree = ""
    protected lateinit var generalConfig_7ree: com.x7ree.wordcard.config.GeneralConfig_7ree
    
    // 各种管理器
    protected lateinit var buttonManager_7ree: WidgetButtonManager_7ree
    protected lateinit var inputValidator_7ree: WidgetInputValidator_7ree
    protected lateinit var uiStateManager_7ree: WidgetUIStateManager_7ree
    protected lateinit var searchManager_7ree: WidgetSearchManager_7ree
    protected lateinit var ttsManager_7ree: WidgetTTSManager_7ree
    protected lateinit var touchFeedbackManager_7ree: WidgetTouchFeedbackManager_7ree
    protected lateinit var resultButtonManager_7ree: WidgetResultButtonManager_7ree
    protected lateinit var keyboardManager_7ree: WidgetKeyboardManager_7ree
    protected lateinit var overlayManager_7ree: WidgetOverlayManager_7ree
    
    // 公共getter方法，供辅助类访问protected成员
    fun getButtonManager_7reeInstance(): WidgetButtonManager_7ree = buttonManager_7ree
    fun getInputValidator_7reeInstance(): WidgetInputValidator_7ree = inputValidator_7ree
    fun getUiStateManager_7reeInstance(): WidgetUIStateManager_7ree = uiStateManager_7ree
    fun getSearchManager_7reeInstance(): WidgetSearchManager_7ree = searchManager_7ree
    fun getTtsManager_7reeInstance(): WidgetTTSManager_7ree = ttsManager_7ree
    fun getResultButtonManager_7reeInstance(): WidgetResultButtonManager_7ree = resultButtonManager_7ree
    fun getKeyboardManager_7reeInstance(): WidgetKeyboardManager_7ree = keyboardManager_7ree
    fun getOverlayManager_7reeInstance(): WidgetOverlayManager_7ree = overlayManager_7ree
    fun getCurrentQueryWord_7reeValue(): String = currentQueryWord_7ree
    fun getGeneralConfig_7reeInstance(): com.x7ree.wordcard.config.GeneralConfig_7ree = generalConfig_7ree
    fun setCurrentQueryWord_7reeValue(word: String) { currentQueryWord_7ree = word }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 配置窗口
        WidgetWindowManager_7ree.configureWindow_7ree(this)
        // 点击外部区域关闭Activity
        setFinishOnTouchOutside(true)
        
        setContentView(R.layout.activity_widget_config_7ree)
    }
    
    /**
     * 初始化各种管理器 - 优化版本，利用预加载的资源
     */
    protected suspend fun initializeManagers_7ree() {
        withContext(Dispatchers.IO) {
            // 初始化依赖 - 如果已预加载则会很快
            val database = WordDatabase_7ree.getDatabase_7ree(this@WidgetConfigBaseActivity_7ree)
            wordRepository_7ree = WordRepository_7ree(database.wordDao_7ree())
            
            // 初始化API服务并加载用户配置
            apiService_7ree = OpenAiApiService_7ree()
            val configManager_7ree = AppConfigManager_7ree(this@WidgetConfigBaseActivity_7ree)
            val apiConfig_7ree = configManager_7ree.loadApiConfig_7ree()
            val promptConfig_7ree = configManager_7ree.loadPromptConfig_7ree()
            generalConfig_7ree = configManager_7ree.loadGeneralConfig_7ree()
            apiService_7ree.updateApiConfig_7ree(apiConfig_7ree)
            apiService_7ree.updatePromptConfig_7ree(promptConfig_7ree)
        }
        
        // 在主线程初始化UI相关的管理器
        withContext(Dispatchers.Main) {
            // 初始化各种管理器
            buttonManager_7ree = WidgetButtonManager_7ree(this@WidgetConfigBaseActivity_7ree)
            inputValidator_7ree = WidgetInputValidator_7ree(buttonManager_7ree)
            uiStateManager_7ree = WidgetUIStateManager_7ree()
            searchManager_7ree = WidgetSearchManager_7ree(wordRepository_7ree, apiService_7ree)
            ttsManager_7ree = WidgetTTSManager_7ree(this@WidgetConfigBaseActivity_7ree)
            touchFeedbackManager_7ree = WidgetTouchFeedbackManager_7ree()
            resultButtonManager_7ree = WidgetResultButtonManager_7ree(this@WidgetConfigBaseActivity_7ree, ttsManager_7ree, touchFeedbackManager_7ree)
            
            // 初始化键盘管理器
            keyboardManager_7ree = WidgetKeyboardManager_7ree(this@WidgetConfigBaseActivity_7ree)
            keyboardManager_7ree.initialize_7ree()
            
            // 初始化蒙版管理器
            overlayManager_7ree = WidgetOverlayManager_7ree(this@WidgetConfigBaseActivity_7ree)
            overlayManager_7ree.initialize_7ree()
            
            // 提前初始化TTS，确保朗读功能可用
            ttsManager_7ree.initializeTtsLazy_7ree()
        }
    }
    
    /**
     * 记录查询状态下UI元素的坐标和间距信息
     */
    fun logSearchStateUICoordinates_7ree(progressBar: ProgressBar, loadingText: TextView, wordTitle: TextView) {
        // 使用ViewTreeObserver确保布局完成后再获取坐标
        progressBar.viewTreeObserver.addOnGlobalLayoutListener(object : android.view.ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                progressBar.viewTreeObserver.removeOnGlobalLayoutListener(this)
                
                val TAG = "WidgetQueryUI_7ree"
                // Log.d(TAG, "=== 查询状态页面 UI 坐标和间距信息 ===")
                
                // 获取WordTitle的位置和尺寸
                val wordTitleLocation = IntArray(2)
                wordTitle.getLocationOnScreen(wordTitleLocation)
                // Log.d(TAG, "WordTitle - X: ${wordTitleLocation[0]}, Y: ${wordTitleLocation[1]}, Width: ${wordTitle.width}, Height: ${wordTitle.height}")
                // Log.d(TAG, "WordTitle - MarginTop: ${(wordTitle.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.topMargin ?: 0}")
                // Log.d(TAG, "WordTitle - MarginBottom: ${(wordTitle.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0}")
                
                // 获取ProgressBar的位置和尺寸
                val progressBarLocation = IntArray(2)
                progressBar.getLocationOnScreen(progressBarLocation)
                // Log.d(TAG, "ProgressBar - X: ${progressBarLocation[0]}, Y: ${progressBarLocation[1]}, Width: ${progressBar.width}, Height: ${progressBar.height}")
                // Log.d(TAG, "ProgressBar - MarginTop: ${(progressBar.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.topMargin ?: 0}")
                // Log.d(TAG, "ProgressBar - MarginBottom: ${(progressBar.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0}")
                
                // 获取LoadingText的位置和尺寸
                val loadingTextLocation = IntArray(2)
                loadingText.getLocationOnScreen(loadingTextLocation)
                // Log.d(TAG, "LoadingText - X: ${loadingTextLocation[0]}, Y: ${loadingTextLocation[1]}, Width: ${loadingText.width}, Height: ${loadingText.height}")
                // Log.d(TAG, "LoadingText - MarginTop: ${(loadingText.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.topMargin ?: 0}")
                // Log.d(TAG, "LoadingText - MarginBottom: ${(loadingText.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0}")
                
                // 计算WordTitle和ProgressBar之间的距离
                val distanceTitleToProgress = progressBarLocation[1] - (wordTitleLocation[1] + wordTitle.height)
                // Log.d(TAG, "WordTitle与ProgressBar之间的距离: ${distanceTitleToProgress}px")
                
                // 计算ProgressBar和LoadingText之间的距离
                val distanceProgressToText = loadingTextLocation[1] - (progressBarLocation[1] + progressBar.height)
                // Log.d(TAG, "ProgressBar与LoadingText之间的距离: ${distanceProgressToText}px")
                
                // Log.d(TAG, "=== 查询状态 UI 坐标和间距信息记录完成 ===")
            }
        })
    }
    
    /**
     * 记录结果状态下UI元素的坐标和间距信息
     */
    fun logResultStateUICoordinates_7ree(wordTitle: TextView, chineseMeaning: TextView, resultText: TextView, resultButtons: LinearLayout) {
        // 使用ViewTreeObserver确保布局完成后再获取坐标
        wordTitle.viewTreeObserver.addOnGlobalLayoutListener(object : android.view.ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                wordTitle.viewTreeObserver.removeOnGlobalLayoutListener(this)
                
                val TAG = "WidgetResultUI_7ree"
                // Log.d(TAG, "=== 查询结果页面 UI 坐标和间距信息 ===")
                
                // 获取WordTitle的位置和尺寸
                val wordTitleLocation = IntArray(2)
                wordTitle.getLocationOnScreen(wordTitleLocation)
                // Log.d(TAG, "WordTitle - X: ${wordTitleLocation[0]}, Y: ${wordTitleLocation[1]}, Width: ${wordTitle.width}, Height: ${wordTitle.height}")
                // Log.d(TAG, "WordTitle - MarginTop: ${(wordTitle.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.topMargin ?: 0}")
                // Log.d(TAG, "WordTitle - MarginBottom: ${(wordTitle.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0}")
                
                // 获取ChineseMeaning的位置和尺寸
                val chineseMeaningLocation = IntArray(2)
                chineseMeaning.getLocationOnScreen(chineseMeaningLocation)
                // Log.d(TAG, "ChineseMeaning - X: ${chineseMeaningLocation[0]}, Y: ${chineseMeaningLocation[1]}, Width: ${chineseMeaning.width}, Height: ${chineseMeaning.height}")
                // Log.d(TAG, "ChineseMeaning - MarginTop: ${(chineseMeaning.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.topMargin ?: 0}")
                // Log.d(TAG, "ChineseMeaning - MarginBottom: ${(chineseMeaning.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0}")
                
                // 获取ResultText的位置和尺寸
                val resultTextLocation = IntArray(2)
                resultText.getLocationOnScreen(resultTextLocation)
                // Log.d(TAG, "ResultText - X: ${resultTextLocation[0]}, Y: ${resultTextLocation[1]}, Width: ${resultText.width}, Height: ${resultText.height}")
                // Log.d(TAG, "ResultText - MarginTop: ${(resultText.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.topMargin ?: 0}")
                // Log.d(TAG, "ResultText - MarginBottom: ${(resultText.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0}")
                
                // 获取ResultButtons的位置和尺寸
                val resultButtonsLocation = IntArray(2)
                resultButtons.getLocationOnScreen(resultButtonsLocation)
                // Log.d(TAG, "ResultButtons - X: ${resultButtonsLocation[0]}, Y: ${resultButtonsLocation[1]}, Width: ${resultButtons.width}, Height: ${resultButtons.height}")
                // Log.d(TAG, "ResultButtons - MarginTop: ${(resultButtons.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.topMargin ?: 0}")
                // Log.d(TAG, "ResultButtons - MarginBottom: ${(resultButtons.layoutParams as? android.view.ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0}")
                
                // 计算各元素之间的距离
                val distanceTitleToMeaning = chineseMeaningLocation[1] - (wordTitleLocation[1] + wordTitle.height)
                // Log.d(TAG, "WordTitle与ChineseMeaning之间的距离: ${distanceTitleToMeaning}px")
                
                val distanceMeaningToResult = resultTextLocation[1] - (chineseMeaningLocation[1] + chineseMeaning.height)
                // Log.d(TAG, "ChineseMeaning与ResultText之间的距离: ${distanceMeaningToResult}px")
                
                val distanceResultToButtons = resultButtonsLocation[1] - (resultTextLocation[1] + resultText.height)
                // Log.d(TAG, "ResultText与ResultButtons之间的距离: ${distanceResultToButtons}px")
                
                // Log.d(TAG, "=== 查询结果 UI 坐标和间距信息记录完成 ===")
            }
        })
    }
    
    /**
     * 显示搜索结果
     */
    fun displayResult_7ree(
        extractedInfo: Pair<String, String>,
        queryText: String,
        progressBar: ProgressBar,
        resultText: TextView,
        wordTitle: TextView,
        chineseMeaning: TextView,
        inputText: EditText,
        queryButton: Button,
        showButtons: Boolean = true, // 新增参数控制是否显示按钮
        fullContent: String = "" // 新增参数传递完整内容用于判断流式状态
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
        val parsedContent = com.x7ree.wordcard.widget.WidgetMarkdownParser_7ree.parseBasicInfo_7ree(detailInfo)
        
        // 显示单词标题
        wordTitle.text = if (parsedContent.word.isNotEmpty()) parsedContent.word else queryText
        
        // 检查流式输出是否完成
        val isStreamingComplete = uiStateManager_7ree.isContentComplete_7ree(fullContent)
        
        // 更新中文意思显示，传递流式输出状态
        uiStateManager_7ree.updateChineseMeaning_7ree(chineseMeaning, parsedContent.chineseMeaning, isStreamingComplete)
        
        // 使用完整的模板格式显示内容
        com.x7ree.wordcard.widget.WidgetMarkdownParser_7ree.renderToTextView_7ree(resultText, detailInfo)
        
        // 如果需要显示按钮，设置按钮点击事件
        if (showButtons) {
            // 使用解析出的单词或查询文本作为朗读内容
            val wordToSpeak = if (parsedContent.word.isNotEmpty()) parsedContent.word else queryText
            resultButtonManager_7ree.setupResultButtons_7ree(queryText, wordToSpeak)
            
            // 在按钮设置完成后，延迟记录UI坐标以确保布局完成
            resultButtons.post {
                logResultStateUICoordinates_7ree(wordTitle, chineseMeaning, resultText, resultButtons)
            }
        } else {
            // 即使不显示按钮，也记录UI坐标用于调试
            resultText.post {
                logResultStateUICoordinates_7ree(wordTitle, chineseMeaning, resultText, resultButtons)
            }
        }
    }
    
    /**
     * 执行搜索操作
     */
    fun performSearch_7ree(inputText: EditText, queryButton: Button) {
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
            
            // 添加查询状态下的UI坐标和间距日志输出
            logSearchStateUICoordinates_7ree(progressBar, loadingText, wordTitle)
            
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
                                searchResult.isComplete, searchResult.content
                            )
                        }
                        
                        // 如果搜索完成，保存当前查询的单词并检查是否需要自动朗读
                        if (searchResult.isComplete) {
                            currentQueryWord_7ree = queryText
                            
                            // 自动朗读功能 - 当桌面小组件查询完成且配置启用时自动朗读单词
                            if (generalConfig_7ree.autoReadAfterQuery && 
                                queryText.isNotBlank() && 
                                ttsManager_7ree.isTtsReady_7ree()) {
                                // 延迟一小段时间确保结果完全显示
                                kotlinx.coroutines.delay(500)
                                resultButtonManager_7ree.autoSpeakWord_7ree(queryText)
                            }
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
    
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_OUTSIDE) {
            finish()
            return true
        }
        return super.onTouchEvent(event)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        ttsManager_7ree.release_7ree()
        keyboardManager_7ree.release_7ree()
        if (::overlayManager_7ree.isInitialized) {
            overlayManager_7ree.release_7ree()
        }
    }
}
