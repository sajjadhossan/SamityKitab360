package com.example.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database definition for Samity Kitab 360 offline & on-device storage.
 */
@Database(
    entities = [MemberEntity::class, DepositEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SamityRoomDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao
    abstract fun depositDao(): DepositDao

    companion object {
        @Volatile
        private var INSTANCE: SamityRoomDatabase? = null

        fun getInstance(context: Context): SamityRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SamityRoomDatabase::class.java,
                    "samity_kitab_360_room.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        fun createInMemory(context: Context): SamityRoomDatabase {
            return Room.inMemoryDatabaseBuilder(
                context.applicationContext,
                SamityRoomDatabase::class.java
            )
            .allowMainThreadQueries()
            .build()
        }
    }
}
