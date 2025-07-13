package com.x7ree.wordcard.widget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.x7ree.wordcard.MainActivity
import com.x7ree.wordcard.R

class WidgetConfigActivity_7ree : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config_7ree)
        
        val inputText = findViewById<EditText>(R.id.widget_input_config_7ree)
        val queryButton = findViewById<Button>(R.id.widget_query_button_config_7ree)
        
        queryButton.setOnClickListener {
            val queryText = inputText.text.toString().trim()
            
            if (queryText.isNotEmpty()) {
                // 启动MainActivity并传递查询文本
                val intent = Intent(this, MainActivity::class.java).apply {
                    action = WordQueryWidgetProvider_7ree.ACTION_WIDGET_QUERY_7ree
                    putExtra(WordQueryWidgetProvider_7ree.EXTRA_QUERY_TEXT_7ree, queryText)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
                finish()
            }
        }
    }
}