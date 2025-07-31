package com.x7ree.wordcard.utils.httpServer

/**
 * HTML页面提供器
 */
class HtmlPageProvider_7ree {
    
    /**
     * 获取主页面HTML
     */
    fun getMainPageHtml(): String {
        return """
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WordCard 数据管理</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            background: white;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h1 {
            color: #333;
            text-align: center;
            margin-bottom: 30px;
        }
        
        /* Tab导航样式 */
        .tab-nav {
            display: flex;
            border-bottom: 2px solid #e0e0e0;
            margin-bottom: 30px;
        }
        .tab-button {
            background: none;
            border: none;
            padding: 12px 24px;
            cursor: pointer;
            font-size: 16px;
            color: #666;
            border-bottom: 3px solid transparent;
            transition: all 0.3s ease;
        }
        .tab-button:hover {
            color: #007AFF;
            background-color: #f8f9fa;
        }
        .tab-button.active {
            color: #007AFF;
            border-bottom-color: #007AFF;
            font-weight: 600;
        }
        
        /* Tab内容样式 */
        .tab-content {
            display: none;
        }
        .tab-content.active {
            display: block;
        }
        
        .section {
            padding: 20px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            background-color: #fafafa;
        }
        .section h2 {
            color: #555;
            margin-top: 0;
            margin-bottom: 16px;
        }
        button:not(.tab-button) {
            background-color: #007AFF;
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 6px;
            cursor: pointer;
            font-size: 16px;
            margin: 5px;
        }
        button:not(.tab-button):hover {
            background-color: #0056CC;
        }
        button:not(.tab-button):disabled {
            background-color: #ccc;
            cursor: not-allowed;
        }
        .file-input {
            margin: 10px 0;
        }
        .message {
            padding: 10px;
            border-radius: 4px;
            margin: 10px 0;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .loading {
            display: none;
            color: #007AFF;
        }
        textarea {
            width: 100%;
            height: 200px;
            border: 1px solid #ddd;
            border-radius: 4px;
            padding: 10px;
            font-family: monospace;
            font-size: 12px;
            box-sizing: border-box;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>WordCard 数据管理</h1>
        <p id="wordCountSubtitle" style="text-align: center; color: #666; margin-bottom: 20px;">正在加载数据统计...</p>
        
        <!-- Tab导航 -->
        <div class="tab-nav">
            <button class="tab-button active" onclick="switchTab('export')">📤 数据导出</button>
            <button class="tab-button" onclick="switchTab('import')">📥 文件导入</button>
            <button class="tab-button" onclick="switchTab('manual')">📝 手动导入</button>
        </div>
        
        <!-- 数据导出Tab -->
        <div id="export-tab" class="tab-content active">
            <!-- 历史单词导出部分 -->
            <div class="section">
                <h2>历史单词导出</h2>
                <p>导出您的单词查询历史数据为JSON格式文件</p>
                <button onclick="exportData()">历史单词导出</button>
                <div class="loading" id="exportLoading">导出中...</div>
                <div id="exportMessage"></div>
            </div>
            
            <!-- 历史文章导出部分 -->
            <div class="section" style="margin-top: 20px;">
                <h2>历史文章导出</h2>
                <p>导出您的文章单词数据为JSON格式文件</p>
                <button onclick="exportArticleData()">历史文章导出</button>
                <div class="loading" id="exportArticleLoading">导出中...</div>
                <div id="exportArticleMessage"></div>
            </div>
        </div>
        
        <!-- 文件导入Tab -->
        <div id="import-tab" class="tab-content">
            <!-- 历史单词导入部分 -->
            <div class="section">
                <h2>历史单词导入</h2>
                <p>从JSON文件导入单词查询历史数据</p>
                <div class="file-input">
                    <input type="file" id="importFile" accept=".json" />
                </div>
                <button onclick="importData()">历史单词导入</button>
                <div class="loading" id="importLoading">导入中...</div>
                <div id="importMessage"></div>
            </div>
            
            <!-- 历史文章导入部分 -->
            <div class="section" style="margin-top: 20px;">
                <h2>历史文章导入</h2>
                <p>从JSON文件导入文章单词数据</p>
                <div class="file-input">
                    <input type="file" id="importArticleFile" accept=".json" />
                </div>
                <button onclick="importArticleData()">历史文章导入</button>
                <div class="loading" id="importArticleLoading">导入中...</div>
                <div id="importArticleMessage"></div>
            </div>
        </div>
        
        <!-- 手动导入Tab -->
        <div id="manual-tab" class="tab-content">
            <!-- 历史单词手动导入部分 -->
            <div class="section">
                <h2>历史单词手动导入</h2>
                <p>直接粘贴JSON数据进行历史单词导入</p>
                <textarea id="jsonInput" placeholder="请粘贴历史单词JSON数据..."></textarea>
                <br>
                <button onclick="importFromText()">历史单词手动导入</button>
                <div class="loading" id="textImportLoading">导入中...</div>
                <div id="textImportMessage"></div>
            </div>
            
            <!-- 历史文章手动导入部分 -->
            <div class="section" style="margin-top: 20px;">
                <h2>历史文章手动导入</h2>
                <p>直接粘贴JSON数据进行文章单词导入</p>
                <textarea id="jsonArticleInput" placeholder="请粘贴文章单词JSON数据..."></textarea>
                <br>
                <button onclick="importArticleFromText()">历史文章手动导入</button>
                <div class="loading" id="textArticleImportLoading">导入中...</div>
                <div id="textArticleImportMessage"></div>
            </div>
        </div>
    </div>

    <script>
        ${getJavaScript()}
    </script>
</body>
</html>
        """.trimIndent()
    }
    
    /**
     * 获取JavaScript代码
     */
    private fun getJavaScript(): String {
        return """
        // Tab切换功能
        function switchTab(tabName) {
            // 隐藏所有tab内容
            const tabContents = document.querySelectorAll('.tab-content');
            tabContents.forEach(content => content.classList.remove('active'));
            
            // 移除所有tab按钮的active状态
            const tabButtons = document.querySelectorAll('.tab-button');
            tabButtons.forEach(button => button.classList.remove('active'));
            
            // 显示选中的tab内容
            document.getElementById(tabName + '-tab').classList.add('active');
            
            // 激活对应的tab按钮
            event.target.classList.add('active');
        }
        
        function showMessage(elementId, message, isError = false) {
            const element = document.getElementById(elementId);
            element.innerHTML = '<div class="message ' + (isError ? 'error' : 'success') + '">' + message + '</div>';
        }
        
        function showLoading(elementId, show = true) {
            document.getElementById(elementId).style.display = show ? 'block' : 'none';
        }
        
        async function exportData() {
            showLoading('exportLoading', true);
            document.getElementById('exportMessage').innerHTML = '';
            
            try {
                const response = await fetch('/export');
                if (response.ok) {
                    const data = await response.text();
                    
                    // 创建下载链接
                    const blob = new Blob([data], { type: 'application/json' });
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    // 生成手机所在时区的时间格式：WordCard_7ree_Data_Export_20250724_164139.json
                    const now = new Date();
                    const dateStr = now.getFullYear() + 
                                  String(now.getMonth() + 1).padStart(2, '0') + 
                                  String(now.getDate()).padStart(2, '0');
                    const timeStr = String(now.getHours()).padStart(2, '0') + 
                                  String(now.getMinutes()).padStart(2, '0') + 
                                  String(now.getSeconds()).padStart(2, '0');
                    a.download = 'WordCard_WordData_Export_7ree_' + dateStr + '_' + timeStr + '.json';
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                    
                    showMessage('exportMessage', '历史单词导出成功！文件已开始下载。');
                } else {
                    showMessage('exportMessage', '历史单词导出失败：' + response.statusText, true);
                }
            } catch (error) {
                showMessage('exportMessage', '历史单词导出失败：' + error.message, true);
            } finally {
                showLoading('exportLoading', false);
            }
        }
        
        async function exportArticleData() {
            showLoading('exportArticleLoading', true);
            document.getElementById('exportArticleMessage').innerHTML = '';
            
            try {
                const response = await fetch('/export-article');
                if (response.ok) {
                    const data = await response.text();
                    
                    // 创建下载链接
                    const blob = new Blob([data], { type: 'application/json' });
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    // 生成手机所在时区的时间格式：WordCard_7ree_Article_Export_20250724_164139.json
                    const now = new Date();
                    const dateStr = now.getFullYear() + 
                                  String(now.getMonth() + 1).padStart(2, '0') + 
                                  String(now.getDate()).padStart(2, '0');
                    const timeStr = String(now.getHours()).padStart(2, '0') + 
                                  String(now.getMinutes()).padStart(2, '0') + 
                                  String(now.getSeconds()).padStart(2, '0');
                    a.download = 'WordCard_ArticleData_Export_7ree_' + dateStr + '_' + timeStr + '.json';
                    document.body.appendChild(a);
                    a.click();
                    document.body.removeChild(a);
                    window.URL.revokeObjectURL(url);
                    
                    showMessage('exportArticleMessage', '历史文章导出成功！文件已开始下载。');
                } else {
                    showMessage('exportArticleMessage', '历史文章导出失败：' + response.statusText, true);
                }
            } catch (error) {
                showMessage('exportArticleMessage', '历史文章导出失败：' + error.message, true);
            } finally {
                showLoading('exportArticleLoading', false);
            }
        }
        
        async function importData() {
            const fileInput = document.getElementById('importFile');
            const file = fileInput.files[0];
            
            if (!file) {
                showMessage('importMessage', '请选择要导入的文件', true);
                return;
            }
            
            showLoading('importLoading', true);
            document.getElementById('importMessage').innerHTML = '';
            
            try {
                const text = await file.text();
                await performImport(text, 'importMessage', 'importLoading');
            } catch (error) {
                showMessage('importMessage', '读取文件失败：' + error.message, true);
                showLoading('importLoading', false);
            }
        }
        
        async function importFromText() {
            const jsonInput = document.getElementById('jsonInput');
            const jsonText = jsonInput.value.trim();
            
            if (!jsonText) {
                showMessage('textImportMessage', '请输入JSON数据', true);
                return;
            }
            
            showLoading('textImportLoading', true);
            document.getElementById('textImportMessage').innerHTML = '';
            
            await performImport(jsonText, 'textImportMessage', 'textImportLoading');
        }
        
        async function importArticleData() {
            const fileInput = document.getElementById('importArticleFile');
            const file = fileInput.files[0];
            
            if (!file) {
                showMessage('importArticleMessage', '请选择要导入的文件', true);
                return;
            }
            
            showLoading('importArticleLoading', true);
            document.getElementById('importArticleMessage').innerHTML = '';
            
            try {
                const text = await file.text();
                await performArticleImport(text, 'importArticleMessage', 'importArticleLoading');
            } catch (error) {
                showMessage('importArticleMessage', '读取文件失败：' + error.message, true);
                showLoading('importArticleLoading', false);
            }
        }
        
        async function importArticleFromText() {
            const jsonInput = document.getElementById('jsonArticleInput');
            const jsonText = jsonInput.value.trim();
            
            if (!jsonText) {
                showMessage('textArticleImportMessage', '请输入JSON数据', true);
                return;
            }
            
            showLoading('textArticleImportLoading', true);
            document.getElementById('textArticleImportMessage').innerHTML = '';
            
            await performArticleImport(jsonText, 'textArticleImportMessage', 'textArticleImportLoading');
        }
        
        // 页面加载时获取单词和文章数量
        async function loadWordCount() {
            try {
                const [wordResponse, articleResponse] = await Promise.all([
                    fetch('/wordcount'),
                    fetch('/articlecount')
                ]);
                
                let wordCount = '--';
                let articleCount = '--';
                
                if (wordResponse.ok) {
                    const wordResult = await wordResponse.json();
                    wordCount = wordResult.count;
                }
                
                if (articleResponse.ok) {
                    const articleResult = await articleResponse.json();
                    articleCount = articleResult.count;
                }
                
                document.getElementById('wordCountSubtitle').textContent = 
                    'APP共存储了' + wordCount + '条单词记录和' + articleCount + '篇文章记录';
            } catch (error) {
                document.getElementById('wordCountSubtitle').textContent = 'APP共存储了--条单词记录和--篇文章记录';
            }
        }
        
        // 页面加载完成后获取单词数量
        window.addEventListener('load', loadWordCount);
        
        async function performImport(jsonText, messageElementId, loadingElementId) {
            try {
                // 验证JSON格式
                JSON.parse(jsonText);
                
                const response = await fetch('/import', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: jsonText
                });
                
                if (response.ok) {
                    const result = await response.json();
                    
                    if (result.success) {
                        showMessage(messageElementId, result.message);
                        // 清空文件选择字段
                        if (messageElementId === 'importMessage') {
                            document.getElementById('importFile').value = '';
                        } else if (messageElementId === 'textImportMessage') {
                            document.getElementById('jsonInput').value = '';
                        }
                        // 导入成功后刷新单词数量
                        loadWordCount();
                    } else {
                        showMessage(messageElementId, result.message, true);
                    }
                } else {
                    // 处理HTTP错误状态
                    const errorText = await response.text();
                    try {
                        const errorResult = JSON.parse(errorText);
                        showMessage(messageElementId, errorResult.message || '导入失败', true);
                    } catch (e) {
                        showMessage(messageElementId, '导入失败：服务器错误 ' + response.status, true);
                    }
                }
            } catch (error) {
                if (error instanceof SyntaxError) {
                    showMessage(messageElementId, '无效的JSON格式', true);
                } else {
                    showMessage(messageElementId, '导入失败：' + error.message, true);
                }
            } finally {
                showLoading(loadingElementId, false);
            }
        }
        
        async function performArticleImport(jsonText, messageElementId, loadingElementId) {
            try {
                // 验证JSON格式
                JSON.parse(jsonText);
                
                const response = await fetch('/import-article', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: jsonText
                });
                
                if (response.ok) {
                    const result = await response.json();
                    
                    if (result.success) {
                        showMessage(messageElementId, result.message);
                        // 清空文件选择字段
                        if (messageElementId === 'importArticleMessage') {
                            document.getElementById('importArticleFile').value = '';
                        } else if (messageElementId === 'textArticleImportMessage') {
                            document.getElementById('jsonArticleInput').value = '';
                        }
                        // 导入成功后刷新单词数量
                        loadWordCount();
                    } else {
                        showMessage(messageElementId, result.message, true);
                    }
                } else {
                    // 处理HTTP错误状态
                    const errorText = await response.text();
                    try {
                        const errorResult = JSON.parse(errorText);
                        showMessage(messageElementId, errorResult.message || '历史文章导入失败', true);
                    } catch (e) {
                        showMessage(messageElementId, '历史文章导入失败：服务器错误 ' + response.status, true);
                    }
                }
            } catch (error) {
                if (error instanceof SyntaxError) {
                    showMessage(messageElementId, '无效的JSON格式', true);
                } else {
                    showMessage(messageElementId, '历史文章导入失败：' + error.message, true);
                }
            } finally {
                showLoading(loadingElementId, false);
            }
        }
        """.trimIndent()
    }
}