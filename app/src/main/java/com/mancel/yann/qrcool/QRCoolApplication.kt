package com.mancel.yann.qrcool

import android.app.Application
import com.mancel.yann.qrcool.koin.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Created by Yann MANCEL on 24/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool
 *
 * An [Application] subclass.
 */
class QRCoolApplication : Application() {

    // METHODS -------------------------------------------------------------------------------------

    // -- Application --

    override fun onCreate() {
        super.onCreate()

        // Koin: Dependency injection framework
        startKoin {
            androidContext(this@QRCoolApplication)
            modules(appModule)
        }
    }
}