package com.x7ree.wordcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WordEntity_7ree::class], version = 1, exportSchema = false)
abstract class WordDatabase_7ree : RoomDatabase() {
    
    abstract fun wordDao_7ree(): WordDao_7ree
    
    companion object {
        @Volatile
        private var INSTANCE_7ree: WordDatabase_7ree? = null
        
        fun getDatabase_7ree(context: Context): WordDatabase_7ree {
            return INSTANCE_7ree ?: synchronized(this) {
                val instance_7ree = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase_7ree::class.java,
                    "word_database_7ree"
                ).build()
                INSTANCE_7ree = instance_7ree
                instance_7ree
            }
        }
    }
} 