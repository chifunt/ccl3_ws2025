package com.chifunt.chromaticharptabs.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TabEntity::class],
    version = 2
)
abstract class TabDatabase : RoomDatabase() {

    abstract fun tabDao(): TabDao

    companion object {
        private const val DATABASE_NAME = "tabs_database"

        @Volatile
        private var Instance: TabDatabase? = null

        fun getDatabase(context: Context): TabDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    TabDatabase::class.java,
                    DATABASE_NAME
                ).addMigrations(MIGRATION_1_2).build()
                Instance = instance
                instance
            }
        }
    }
}

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE tabs_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                artist TEXT NOT NULL,
                key_name TEXT NOT NULL,
                difficulty TEXT NOT NULL,
                tags TEXT NOT NULL,
                content TEXT NOT NULL,
                is_favorite INTEGER NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO tabs_new (
                id, title, artist, key_name, difficulty, tags, content, is_favorite, created_at, updated_at
            )
            SELECT
                id, title, artist, key_name, difficulty, tags, content, is_favorite, created_at, updated_at
            FROM tabs
            """.trimIndent()
        )
        db.execSQL("DROP TABLE tabs")
        db.execSQL("ALTER TABLE tabs_new RENAME TO tabs")
    }
}
