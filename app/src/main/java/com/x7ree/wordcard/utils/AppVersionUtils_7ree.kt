package com.x7ree.wordcard.utils

import android.content.Context
import android.content.pm.PackageManager

/**
 * 应用版本信息工具类
 */
object AppVersionUtils_7ree {
    
    /**
     * 获取应用版本名称
     */
    fun getVersionName(context: Context): String {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }
    }
    
    /**
     * 获取应用版本代码
     */
    fun getVersionCode(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            0L
        }
    }
    
    /**
     * 获取格式化的版本信息
     */
    fun getFormattedVersion(context: Context): String {
        val versionName = getVersionName(context)
        return "v$versionName"
    }
}