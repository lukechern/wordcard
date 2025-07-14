package com.x7ree.wordcard.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.x7ree.wordcard.MainActivity
import com.x7ree.wordcard.R

class WordQueryWidgetProvider_7ree : AppWidgetProvider() {

    companion object {
        const val ACTION_WIDGET_QUERY_7ree = "com.x7ree.wordcard.WIDGET_QUERY"
        const val ACTION_WIDGET_WORDBOOK_7ree = "com.x7ree.wordcard.WIDGET_WORDBOOK"
        const val EXTRA_QUERY_TEXT_7ree = "query_text"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget_7ree(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget_7ree(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_word_query_7ree)

        // 设置查询按钮的Intent
        val queryIntent = Intent(context, WidgetConfigActivity_7ree::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val queryPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            queryIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 设置单词本按钮的Intent
        val wordbookIntent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_WIDGET_WORDBOOK_7ree
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val wordbookPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId + 1000, // 使用不同的requestCode避免冲突
            wordbookIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 设置点击事件
        views.setOnClickPendingIntent(R.id.widget_container_7ree, queryPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_query_button_7ree, queryPendingIntent)
        views.setOnClickPendingIntent(R.id.widget_wordbook_button_7ree, wordbookPendingIntent)

        // 更新小组件
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onEnabled(context: Context) {
        // 第一个小组件被添加时调用
    }

    override fun onDisabled(context: Context) {
        // 最后一个小组件被移除时调用
    }
}