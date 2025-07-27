package com.x7ree.wordcard.ui.DashBoard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.x7ree.wordcard.query.WordQueryViewModel_7ree
import com.x7ree.wordcard.ui.HelpScreen_7ree

@Composable
fun ConfigPage_7ree(
    wordQueryViewModel_7ree: WordQueryViewModel_7ree,
    onImportFile_7ree: () -> Unit
) {
    var selectedTab_7ree by remember { mutableStateOf(SettingsTab_7ree.HELP) }
    
    // 通用配置UI状态
    val generalConfig_7ree by wordQueryViewModel_7ree.generalConfig_7ree.collectAsState()
    var selectedKeyboardType_7ree by remember { mutableStateOf(generalConfig_7ree.keyboardType) }
    var autoReadAfterQuery_7ree by remember { mutableStateOf(generalConfig_7ree.autoReadAfterQuery) }
    var autoReadOnSpellingCard_7ree by remember { mutableStateOf(generalConfig_7ree.autoReadOnSpellingCard) }
    var selectedTtsEngine_7ree by remember { mutableStateOf(generalConfig_7ree.ttsEngine) }
    
    // API配置UI状态
    val apiConfig_7ree by wordQueryViewModel_7ree.apiConfig_7ree.collectAsState()
    var localApi1Name_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi1.apiName) }
    var localApi1Key_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi1.apiKey) }
    var localApi1Url_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi1.apiUrl) }
    var localApi1Model_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi1.modelName) }
    var localApi1Enabled_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi1.isEnabled) }
    var localApi2Name_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi2.apiName) }
    var localApi2Key_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi2.apiKey) }
    var localApi2Url_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi2.apiUrl) }
    var localApi2Model_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi2.modelName) }
    var localApi2Enabled_7ree by remember { mutableStateOf(apiConfig_7ree.translationApi2.isEnabled) }
    var azureSpeechRegion_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechRegion) }
    var azureSpeechApiKey_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechApiKey) }
    var azureSpeechEndpoint_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechEndpoint) }
    var azureSpeechVoice_7ree by remember { mutableStateOf(apiConfig_7ree.azureSpeechVoice) }
    
    // 提示词配置UI状态
    val promptConfig_7ree by wordQueryViewModel_7ree.promptConfig_7ree.collectAsState()
    var queryPrompt_7ree by remember { mutableStateOf(promptConfig_7ree.queryPrompt_7ree) }
    var outputTemplate_7ree by remember { mutableStateOf(promptConfig_7ree.outputTemplate_7ree) }
    var articleGenerationPrompt_7ree by remember { mutableStateOf(promptConfig_7ree.articleGenerationPrompt_7ree) }
    var articleOutputTemplate_7ree by remember { mutableStateOf(promptConfig_7ree.articleOutputTemplate_7ree) }
    
    // 同步初始状态
    LaunchedEffect(generalConfig_7ree) {
        selectedKeyboardType_7ree = generalConfig_7ree.keyboardType
        autoReadAfterQuery_7ree = generalConfig_7ree.autoReadAfterQuery
        autoReadOnSpellingCard_7ree = generalConfig_7ree.autoReadOnSpellingCard
        selectedTtsEngine_7ree = generalConfig_7ree.ttsEngine
    }
    
    LaunchedEffect(apiConfig_7ree) {
        localApi1Name_7ree = apiConfig_7ree.translationApi1.apiName
        localApi1Key_7ree = apiConfig_7ree.translationApi1.apiKey
        localApi1Url_7ree = apiConfig_7ree.translationApi1.apiUrl
        localApi1Model_7ree = apiConfig_7ree.translationApi1.modelName
        localApi1Enabled_7ree = apiConfig_7ree.translationApi1.isEnabled
        localApi2Name_7ree = apiConfig_7ree.translationApi2.apiName
        localApi2Key_7ree = apiConfig_7ree.translationApi2.apiKey
        localApi2Url_7ree = apiConfig_7ree.translationApi2.apiUrl
        localApi2Model_7ree = apiConfig_7ree.translationApi2.modelName
        localApi2Enabled_7ree = apiConfig_7ree.translationApi2.isEnabled
        azureSpeechRegion_7ree = apiConfig_7ree.azureSpeechRegion
        azureSpeechApiKey_7ree = apiConfig_7ree.azureSpeechApiKey
        azureSpeechEndpoint_7ree = apiConfig_7ree.azureSpeechEndpoint
        azureSpeechVoice_7ree = apiConfig_7ree.azureSpeechVoice
    }
    
    LaunchedEffect(promptConfig_7ree) {
        queryPrompt_7ree = promptConfig_7ree.queryPrompt_7ree
        outputTemplate_7ree = promptConfig_7ree.outputTemplate_7ree
        articleGenerationPrompt_7ree = promptConfig_7ree.articleGenerationPrompt_7ree
        articleOutputTemplate_7ree = promptConfig_7ree.articleOutputTemplate_7ree
    }
    
    // API配置状态监控日志
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        TabRow(selectedTabIndex = selectedTab_7ree.ordinal) {
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.HELP,
                onClick = { selectedTab_7ree = SettingsTab_7ree.HELP },
                text = { Text("帮助", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.GENERAL,
                onClick = { selectedTab_7ree = SettingsTab_7ree.GENERAL },
                text = { Text("通用", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.API_CONFIG,
                onClick = { selectedTab_7ree = SettingsTab_7ree.API_CONFIG },
                text = { Text("API", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.PROMPT_CONFIG,
                onClick = { selectedTab_7ree = SettingsTab_7ree.PROMPT_CONFIG },
                text = { Text("提示词", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
            Tab(
                selected = selectedTab_7ree == SettingsTab_7ree.DATA_MANAGEMENT,
                onClick = { selectedTab_7ree = SettingsTab_7ree.DATA_MANAGEMENT },
                text = { Text("数据", fontSize = 13.sp) },
                modifier = Modifier.padding(0.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab_7ree) {
                SettingsTab_7ree.HELP -> {
                    HelpScreen_7ree()
                }
                SettingsTab_7ree.GENERAL -> {
                    GeneralConfigTab_7ree(
                        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                        selectedKeyboardType_7ree = selectedKeyboardType_7ree,
                        onKeyboardTypeChange = { selectedKeyboardType_7ree = it },
                        autoReadAfterQuery_7ree = autoReadAfterQuery_7ree,
                        onAutoReadAfterQueryChange = { autoReadAfterQuery_7ree = it },
                        autoReadOnSpellingCard_7ree = autoReadOnSpellingCard_7ree,
                        onAutoReadOnSpellingCardChange = { autoReadOnSpellingCard_7ree = it },
                        selectedTtsEngine_7ree = selectedTtsEngine_7ree,
                        onTtsEngineChange = { selectedTtsEngine_7ree = it }
                    )
                }
                SettingsTab_7ree.API_CONFIG -> {
                    ApiConfigTab_7ree(
                        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                        localApi1Name_7ree = localApi1Name_7ree,
                        onApi1NameChange = { localApi1Name_7ree = it },
                        localApi1Key_7ree = localApi1Key_7ree,
                        onApi1KeyChange = { localApi1Key_7ree = it },
                        localApi1Url_7ree = localApi1Url_7ree,
                        onApi1UrlChange = { localApi1Url_7ree = it },
                        localApi1Model_7ree = localApi1Model_7ree,
                        onApi1ModelChange = { localApi1Model_7ree = it },
                        localApi1Enabled_7ree = localApi1Enabled_7ree,
                        onApi1EnabledChange = { localApi1Enabled_7ree = it },
                        localApi2Name_7ree = localApi2Name_7ree,
                        onApi2NameChange = { localApi2Name_7ree = it },
                        localApi2Key_7ree = localApi2Key_7ree,
                        onApi2KeyChange = { localApi2Key_7ree = it },
                        localApi2Url_7ree = localApi2Url_7ree,
                        onApi2UrlChange = { localApi2Url_7ree = it },
                        localApi2Model_7ree = localApi2Model_7ree,
                        onApi2ModelChange = { localApi2Model_7ree = it },
                        localApi2Enabled_7ree = localApi2Enabled_7ree,
                        onApi2EnabledChange = { localApi2Enabled_7ree = it },
                        azureSpeechRegion_7ree = azureSpeechRegion_7ree,
                        onAzureSpeechRegionChange = { azureSpeechRegion_7ree = it },
                        azureSpeechApiKey_7ree = azureSpeechApiKey_7ree,
                        onAzureSpeechApiKeyChange = { azureSpeechApiKey_7ree = it },
                        azureSpeechEndpoint_7ree = azureSpeechEndpoint_7ree,
                        onAzureSpeechEndpointChange = { azureSpeechEndpoint_7ree = it },
                        azureSpeechVoice_7ree = azureSpeechVoice_7ree,
                        onAzureSpeechVoiceChange = { azureSpeechVoice_7ree = it }
                    )
                }
                SettingsTab_7ree.PROMPT_CONFIG -> {
                    PromptConfigTab_7ree(
                        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                        queryPrompt_7ree = queryPrompt_7ree,
                        onQueryPromptChange = { queryPrompt_7ree = it },
                        outputTemplate_7ree = outputTemplate_7ree,
                        onOutputTemplateChange = { outputTemplate_7ree = it },
                        articleGenerationPrompt_7ree = articleGenerationPrompt_7ree,
                        onArticleGenerationPromptChange = { articleGenerationPrompt_7ree = it },
                        articleOutputTemplate_7ree = articleOutputTemplate_7ree,
                        onArticleOutputTemplateChange = { articleOutputTemplate_7ree = it }
                    )
                }
                SettingsTab_7ree.DATA_MANAGEMENT -> {
                    DataManagementTab_7ree(
                        wordQueryViewModel_7ree = wordQueryViewModel_7ree,
                        onImportFile_7ree = onImportFile_7ree
                    )
                }
            }
        }
        
        // 只有在需要保存按钮的标签页才显示保存按钮
        if (selectedTab_7ree == SettingsTab_7ree.GENERAL || 
            selectedTab_7ree == SettingsTab_7ree.API_CONFIG || 
            selectedTab_7ree == SettingsTab_7ree.PROMPT_CONFIG) {
            Button(
                onClick = {
                    // 根据当前标签页调用相应的保存方法
                    when (selectedTab_7ree) {
                        SettingsTab_7ree.GENERAL -> {
                            // 通用配置保存
                            wordQueryViewModel_7ree.saveGeneralConfig_7ree(
                                keyboardType = selectedKeyboardType_7ree,
                                autoReadAfterQuery = autoReadAfterQuery_7ree,
                                autoReadOnSpellingCard = autoReadOnSpellingCard_7ree,
                                ttsEngine = selectedTtsEngine_7ree
                            )
                        }
                        SettingsTab_7ree.API_CONFIG -> {
                            // API配置保存 - 添加详细日志
                            
                            wordQueryViewModel_7ree.saveTranslationApiConfig_7ree(
                                api1Name = localApi1Name_7ree,
                                api1Key = localApi1Key_7ree,
                                api1Url = localApi1Url_7ree,
                                api1Model = localApi1Model_7ree,
                                api1Enabled = localApi1Enabled_7ree,
                                api2Name = localApi2Name_7ree,
                                api2Key = localApi2Key_7ree,
                                api2Url = localApi2Url_7ree,
                                api2Model = localApi2Model_7ree,
                                api2Enabled = localApi2Enabled_7ree
                            )
                            
                            // 保存Azure Speech配置 - 不覆盖翻译API配置
                            wordQueryViewModel_7ree.saveAzureSpeechConfig_7ree(
                                azureSpeechRegion = azureSpeechRegion_7ree,
                                azureSpeechApiKey = azureSpeechApiKey_7ree,
                                azureSpeechEndpoint = azureSpeechEndpoint_7ree,
                                azureSpeechVoice = azureSpeechVoice_7ree
                            )
                        }
                        SettingsTab_7ree.PROMPT_CONFIG -> {
                            // 提示词配置保存
                            wordQueryViewModel_7ree.savePromptConfig_7ree(
                                queryPrompt = queryPrompt_7ree,
                                outputTemplate = outputTemplate_7ree,
                                articleGenerationPrompt = articleGenerationPrompt_7ree,
                                articleOutputTemplate = articleOutputTemplate_7ree
                            )
                        }
                        else -> {}
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("保存配置")
            }
        }
    }
}
