package com.example.a210135_nicolechengkee_drnazatul_project2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [UserProfileEntity::class, SavedJobEntity::class, ApplicationEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun savedJobDao(): SavedJobDao
    abstract fun applicationDao(): ApplicationDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // Migration from version 1 to 2: adds location column to user_profile
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE user_profile ADD COLUMN location TEXT NOT NULL DEFAULT 'Selangor, Malaysia'"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jobconnect_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also { INSTANCE = it }
            }
    }
}