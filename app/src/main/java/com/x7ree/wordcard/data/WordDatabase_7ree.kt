package com.x7ree.wordcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [WordEntity_7ree::class], version = 3, exportSchema = false)
abstract class WordDatabase_7ree : RoomDatabase() {
    
    abstract fun wordDao_7ree(): WordDao_7ree
    
    companion object {
        @Volatile
        private var INSTANCE_7ree: WordDatabase_7ree? = null
        
        // 数据库迁移：从版本1到版本2，添加拼写次数字段
        private val MIGRATION_1_2_7ree = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN spellingCount INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        // 数据库迁移：从版本2到版本3，添加中文释义、音标、词性字段
        private val MIGRATION_2_3_7ree = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN chineseDefinition TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE words ADD COLUMN phonetic TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE words ADD COLUMN partOfSpeech TEXT NOT NULL DEFAULT ''")
            }
        }
        
        fun getDatabase_7ree(context: Context): WordDatabase_7ree {
            return INSTANCE_7ree ?: synchronized(this) {
                val instance_7ree = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase_7ree::class.java,
                    "word_database_7ree"
                ).addMigrations(MIGRATION_1_2_7ree, MIGRATION_2_3_7ree).build()
                INSTANCE_7ree = instance_7ree
                instance_7ree
            }
        }
    }
}