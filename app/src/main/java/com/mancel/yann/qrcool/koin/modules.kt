package com.mancel.yann.qrcool.koin

import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.mancel.yann.qrcool.databases.AppDatabase
import com.mancel.yann.qrcool.repositories.DatabaseRepository
import com.mancel.yann.qrcool.repositories.RoomDatabaseRepository
import com.mancel.yann.qrcool.viewModels.SharedViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by Yann MANCEL on 24/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.koin
 */

val appModule = module {
    // Database - [Context in argument]
    single { AppDatabase.getDatabase(get()) }

    // DAO
    single { get<AppDatabase>().barcodeDAO() }

    // Repository - [DAO in argument]
    single<DatabaseRepository> { RoomDatabaseRepository(get()) }

    // ViewModel - [Repository in argument]
    viewModel { SharedViewModel(get()) }
}

@VisibleForTesting
val roomTestModule = module {
    // Database - [Context in argument]
    single {
        Room.inMemoryDatabaseBuilder(
            get(),
            AppDatabase::class.java
        )
        .allowMainThreadQueries()
        .build()
    }

    // DAO
    single { get<AppDatabase>().barcodeDAO() }

    // Repository - [DAO in argument]
    single<DatabaseRepository> { RoomDatabaseRepository(get()) }
}

