package com.x7ree.wordcard.ui.help

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.InputStream

fun loadHelpContentFromAssets(context: Context): HelpContent {
    val inputStream: InputStream = context.assets.open("help_content_7ree.json")
    val size: Int = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val json = String(buffer, Charsets.UTF_8)
    
    val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    return jsonParser.decodeFromString<HelpContent>(json)
}
