package com.x7ree.wordcard.api.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * API客户端配置类
 */
object ApiClient_7ree {
    val json_7ree = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    val client_7ree = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json_7ree)
        }
    }
}
