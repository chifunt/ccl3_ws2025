package com.chifunt.chromaticharptabs.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TabEntity::class],
    version = 1
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
                ).build()
                Instance = instance
                instance
            }
        }
    }
}
