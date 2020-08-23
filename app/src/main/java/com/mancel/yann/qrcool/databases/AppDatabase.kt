package com.mancel.yann.qrcool.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mancel.yann.qrcool.models.*

/**
 * Created by Yann MANCEL on 22/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.databases
 *
 * An abstract [RoomDatabase] subclass.
 */
@Database(
    entities = [
        TextBarcode::class,
        WifiBarcode::class,
        UrlBarcode::class,
        SMSBarcode::class,
        GeoPointBarcode::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // DAOs ----------------------------------------------------------------------------------------

    abstract fun barcodeDAO(): BarcodeDAO

    // METHODS -------------------------------------------------------------------------------------

    companion object {

        private const val DATABASE_NAME = "QRCool_Database"

        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null


        /**
         * Gets the [AppDatabase]
         * @param context a [Context]
         * @return the [AppDatabase]
         */
        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    )
                    .build()

                INSTANCE = instance

                return instance
            }
        }
    }
}