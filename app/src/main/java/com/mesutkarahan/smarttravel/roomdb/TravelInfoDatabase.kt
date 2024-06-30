package com.mesutkarahan.smarttravel.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mesutkarahan.smarttravel.model.TravelInfoEntity

@Database(entities = [TravelInfoEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TravelInfoDatabase : RoomDatabase() {

    abstract fun travelInfoDao(): TravelInfoDao

    companion object {
        @Volatile
        private var INSTANCE: TravelInfoDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Schema değişikliklerini buraya yazın
                // database.execSQL("ALTER TABLE travel_info ADD COLUMN latitude REAL")
                // database.execSQL("ALTER TABLE travel_info ADD COLUMN longitude REAL")
                database.execSQL("ALTER TABLE travel_info ADD COLUMN latitude REAL DEFAULT 0.0 NOT NULL")
                database.execSQL("ALTER TABLE travel_info ADD COLUMN longitude REAL DEFAULT 0.0 NOT NULL")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE travel_info ADD COLUMN travelDate INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): TravelInfoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TravelInfoDatabase::class.java,
                    "travel_info_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration() // Gerekli ise veritabanını sıfırlama seçeneği
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
