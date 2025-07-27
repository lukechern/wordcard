package com.x7ree.wordcard.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [WordEntity_7ree::class, ArticleEntity_7ree::class], version = 5, exportSchema = false)
abstract class WordDatabase_7ree : RoomDatabase() {
    
    abstract fun wordDao_7ree(): WordDao_7ree
    abstract fun articleDao_7ree(): ArticleDao_7ree
    
    companion object {
        @Volatile
        private var INSTANCE_7ree: WordDatabase_7ree? = null
        
        // 数据库迁移：从版本1到版本2，添加拼写次数字段
        private val MIGRATION_1_2_7ree = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN spellingCount INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        // 数据库迁移：从版本2到版本3，添加中文释义、音标、词性字段
        private val MIGRATION_2_3_7ree = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN chineseDefinition TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE words ADD COLUMN phonetic TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE words ADD COLUMN partOfSpeech TEXT NOT NULL DEFAULT ''")
            }
        }
        
        // 数据库迁移：从版本3到版本4，添加articles表
        private val MIGRATION_3_4_7ree = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS articles (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        generationTimestamp INTEGER NOT NULL,
                        keyWords TEXT NOT NULL,
                        viewCount INTEGER NOT NULL,
                        apiResult TEXT NOT NULL,
                        englishTitle TEXT NOT NULL,
                        titleTranslation TEXT NOT NULL,
                        englishContent TEXT NOT NULL,
                        chineseContent TEXT NOT NULL,
                        isFavorite INTEGER NOT NULL
                    )
                """.trimIndent())
            }
        }
        
        // 数据库迁移：从版本4到版本5，添加引用次数字段
        private val MIGRATION_4_5_7ree = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE words ADD COLUMN referenceCount INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        fun getDatabase_7ree(context: Context): WordDatabase_7ree {
            return INSTANCE_7ree ?: synchronized(this) {
                val instance_7ree = Room.databaseBuilder(
                    context.applicationContext,
                    WordDatabase_7ree::class.java,
                    "word_database_7ree"
                ).addMigrations(MIGRATION_1_2_7ree, MIGRATION_2_3_7ree, MIGRATION_3_4_7ree, MIGRATION_4_5_7ree).build()
                INSTANCE_7ree = instance_7ree
                instance_7ree
            }
        }
    }
}
