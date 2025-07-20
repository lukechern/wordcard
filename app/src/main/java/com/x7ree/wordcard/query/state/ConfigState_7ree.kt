package com.x7ree.wordcard.query.state

import com.x7ree.wordcard.config.ApiConfig_7ree
import com.x7ree.wordcard.config.GeneralConfig_7ree
import com.x7ree.wordcard.config.PromptConfig_7ree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * 配置状态管理
 */
class ConfigState_7ree {
    
    // 配置状态
    private val _apiConfig_7ree = MutableStateFlow(ApiConfig_7ree())
    val apiConfig_7ree: StateFlow<ApiConfig_7ree> = _apiConfig_7ree
    
    // 提示词配置状态
    private val _promptConfig_7ree = MutableStateFlow(PromptConfig_7ree())
    val promptConfig_7ree: StateFlow<PromptConfig_7ree> = _promptConfig_7ree
    
    // 通用配置状态
    private val _generalConfig_7ree = MutableStateFlow(GeneralConfig_7ree())
    val generalConfig_7ree: StateFlow<GeneralConfig_7ree> = _generalConfig_7ree

    fun updateApiConfig_7ree(config: ApiConfig_7ree) {
        _apiConfig_7ree.value = config
    }

    fun updatePromptConfig_7ree(config: PromptConfig_7ree) {
        _promptConfig_7ree.value = config
    }

    fun updateGeneralConfig_7ree(config: GeneralConfig_7ree) {
        _generalConfig_7ree.value = config
    }
}