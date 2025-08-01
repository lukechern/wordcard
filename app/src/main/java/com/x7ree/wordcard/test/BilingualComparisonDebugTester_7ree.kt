package com.x7ree.wordcard.test

import android.util.Log
import com.x7ree.wordcard.article.ArticleMarkdownParser_7ree

/**
 * 中英对照功能调试测试器
 * 专门用于调试英文和中文内容提取问题
 */
class BilingualComparisonDebugTester_7ree {
    
    companion object {
        private const val TAG = "BilingualDebugTester"
        
        /**
         * 测试英文和中文内容提取的详细调试
         */
        fun testContentExtractionDebug() {
            Log.d(TAG, "==================== 开始调试测试 ====================")
            
            val parser = ArticleMarkdownParser_7ree()
            
            // 模拟一个简单的API返回内容
            val simpleApiResponse = """
### 英文标题
Test Article

### 中文标题
测试文章

### 重点单词
test, simple

### 中英文章对照
[英文]{This is a **test** sentence.}

[中文]{这是一个**测试**句子。}

[英文]{It is very **simple**.}

[中文]{它非常**简单**。}
            """.trimIndent()
            
            Log.d(TAG, "测试用的API返回内容:")
            Log.d(TAG, "---BEGIN TEST CONTENT---")
            Log.d(TAG, simpleApiResponse)
            Log.d(TAG, "---END TEST CONTENT---")
            
            try {
                val result = parser.parseArticleMarkdown(simpleApiResponse)
                
                Log.d(TAG, "==================== 测试结果验证 ====================")
                Log.d(TAG, "英文内容: '${result.englishContent}'")
                Log.d(TAG, "中文内容: '${result.chineseContent}'")
                Log.d(TAG, "英文内容是否为空: ${result.englishContent.isEmpty()}")
                Log.d(TAG, "中文内容是否为空: ${result.chineseContent.isEmpty()}")
                Log.d(TAG, "英文内容是否为默认值: ${result.englishContent == "暂无英文内容"}")
                Log.d(TAG, "中文内容是否为默认值: ${result.chineseContent == "暂无中文内容"}")
                
                if (result.englishContent == "暂无英文内容" || result.chineseContent == "暂无中文内容") {
                    Log.e(TAG, "❌ 内容提取失败，返回了默认值")
                    Log.e(TAG, "可能的问题：")
                    Log.e(TAG, "1. 正则表达式匹配失败")
                    Log.e(TAG, "2. 中英对照格式不正确")
                    Log.e(TAG, "3. 解析逻辑有问题")
                } else {
                    Log.d(TAG, "✅ 内容提取成功")
                    
                    // 验证内容是否正确
                    val expectedEnglish = "This is a **test** sentence. It is very **simple**."
                    val expectedChinese = "这是一个**测试**句子。它非常**简单**。"
                    
                    if (result.englishContent == expectedEnglish) {
                        Log.d(TAG, "✅ 英文内容拼接正确")
                    } else {
                        Log.e(TAG, "❌ 英文内容拼接错误")
                        Log.e(TAG, "期望: '$expectedEnglish'")
                        Log.e(TAG, "实际: '${result.englishContent}'")
                    }
                    
                    if (result.chineseContent == expectedChinese) {
                        Log.d(TAG, "✅ 中文内容拼接正确")
                    } else {
                        Log.e(TAG, "❌ 中文内容拼接错误")
                        Log.e(TAG, "期望: '$expectedChinese'")
                        Log.e(TAG, "实际: '${result.chineseContent}'")
                    }
                }
                
                Log.d(TAG, "==================== 调试测试完成 ====================")
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ 测试失败: ${e.message}", e)
            }
        }
        
        /**
         * 测试正则表达式匹配
         */
        fun testRegexMatching() {
            Log.d(TAG, "==================== 测试正则表达式匹配 ====================")
            
            val testContent = """
[英文]{This is a **test** sentence.}

[中文]{这是一个**测试**句子。}

[英文]{It is very **simple**.}

[中文]{它非常**简单**。}
            """.trimIndent()
            
            Log.d(TAG, "测试内容:")
            Log.d(TAG, testContent)
            
            // 测试英文正则表达式
            val englishRegex = Regex("\\[英文\\]\\{([^}]+)\\}")
            val englishMatches = englishRegex.findAll(testContent)
            
            Log.d(TAG, "英文正则匹配结果:")
            englishMatches.forEachIndexed { index, match ->
                Log.d(TAG, "匹配 $index: '${match.groupValues[1]}'")
            }
            
            // 测试中文正则表达式
            val chineseRegex = Regex("\\[中文\\]\\{([^}]+)\\}")
            val chineseMatches = chineseRegex.findAll(testContent)
            
            Log.d(TAG, "中文正则匹配结果:")
            chineseMatches.forEachIndexed { index, match ->
                Log.d(TAG, "匹配 $index: '${match.groupValues[1]}'")
            }
            
            Log.d(TAG, "==================== 正则表达式测试完成 ====================")
        }
        
        /**
         * 运行所有调试测试
         */
        fun runAllDebugTests() {
            Log.d(TAG, "🚀 开始运行调试测试")
            
            testRegexMatching()
            testContentExtractionDebug()
            
            Log.d(TAG, "🎉 调试测试完成")
        }
    }
}