package com.x7ree.wordcard.query.data

import android.net.Uri
import com.x7ree.wordcard.data.DataExportImportManager_7ree
import com.x7ree.wordcard.query.manager.DataManager_7ree

/**
 * 数据导入导出功能模块
 */
class DataHandler_7ree(
    private val dataManager_7ree: DataManager_7ree,
    private val dataExportImportManager_7ree: DataExportImportManager_7ree
) {
    
    /**
     * 导出历史数据
     */
    fun exportHistoryData_7ree() {
        dataManager_7ree.exportHistoryData_7ree()
    }
    
    /**
     * 导入历史数据
     */
    fun importHistoryData_7ree(uri: Uri) {
        dataManager_7ree.importHistoryData_7ree(uri)
    }
    
    /**
     * 导出文章数据
     */
    fun exportArticleData_7ree() {
        dataManager_7ree.exportArticleData_7ree()
    }
    
    /**
     * 导入文章数据
     */
    fun importArticleData_7ree(uri: Uri) {
        dataManager_7ree.importArticleData_7ree(uri)
    }
    
    /**
     * 获取数据导出导入管理器
     */
    fun getDataExportImportManager(): DataExportImportManager_7ree {
        return dataExportImportManager_7ree
    }
}
