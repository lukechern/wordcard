package com.x7ree.wordcard.test.speech

import android.util.Log

/**
 * Speech API URL 构建器
 */
class SpeechUrlBuilder {
    
    companion object {
        private const val TAG = "SpeechUrlBuilder"
    }
    
    /**
     * 构建可能的Azure Speech API URL列表
     * 优先使用区域自动生成标准终结点，然后尝试用户提供的终结点
     */
    fun buildPossibleUrls(endpoint: String, region: String): List<String> {
        val cleanEndpoint = endpoint.let { 
            if (it.endsWith("/")) it.dropLast(1) else it 
        }
        
        val urls = mutableListOf<String>()
        
        // 优先级1: 根据区域自动生成标准的 Speech Service 终结点
        if (region.isNotBlank()) {
            val standardEndpoint = "https://$region.tts.speech.microsoft.com/cognitiveservices/v1"
            urls.add(standardEndpoint)
        }
        
        // 优先级2: 如果用户提供了终结点，尝试各种可能的格式
        if (cleanEndpoint.isNotBlank()) {
            // 从用户终结点中提取区域信息
            val extractedRegion = extractRegionFromEndpoint(cleanEndpoint)
            
            // 如果用户提供的是标准的 Speech Service 终结点
            if (cleanEndpoint.contains("tts.speech.microsoft.com")) {
                urls.add("$cleanEndpoint/cognitiveservices/v1")
            }
            
            // 如果用户提供的是通用的认知服务终结点，转换为正确的 Speech Service URL
            if (cleanEndpoint.contains("api.cognitive.microsoft.com") && extractedRegion.isNotEmpty()) {
                urls.add("https://$extractedRegion.tts.speech.microsoft.com/cognitiveservices/v1")
            }
            
            // 尝试用户提供的原始终结点的各种变体
            urls.addAll(listOf(
                "$cleanEndpoint/cognitiveservices/v1",
                cleanEndpoint,
                "$cleanEndpoint/tts/cognitiveservices/v1",
                "$cleanEndpoint/speech/cognitiveservices/v1"
            ))
        }
        
        // 去重并返回
        return urls.distinct()
    }
    
    /**
     * 从终结点中提取区域信息
     */
    private fun extractRegionFromEndpoint(endpoint: String): String {
        return try {
            when {
                // 从 eastasia.api.cognitive.microsoft.com 中提取 eastasia
                endpoint.contains("api.cognitive.microsoft.com") -> {
                    val parts = endpoint.split(".")
                    if (parts.size >= 3) {
                        parts[0].removePrefix("https://")
                    } else ""
                }
                // 从 eastasia.tts.speech.microsoft.com 中提取 eastasia
                endpoint.contains("tts.speech.microsoft.com") -> {
                    val parts = endpoint.split(".")
                    if (parts.size >= 4) {
                        parts[0].removePrefix("https://")
                    } else ""
                }
                else -> ""
            }
        } catch (e: Exception) {
            Log.w(TAG, "无法从终结点提取区域信息: $endpoint", e)
            ""
        }
    }
}
