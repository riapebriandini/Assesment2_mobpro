package com.riapebriand.assesment2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.riapebriand.assesment2.model.Wishlist

@Database(entities = [Wishlist::class], version = 1, exportSchema = false)
abstract class WishlistDb : RoomDatabase() {
    abstract val dao: WishlistDao

    companion object {

        @Volatile
        private var INSTANCE: WishlistDb? = null

        fun getInstance(context: Context): WishlistDb {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WishlistDb::class.java,
                        "wishlist.db"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE wishlist ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}