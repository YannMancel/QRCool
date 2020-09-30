package com.mancel.yann.qrcool.utils

import android.util.Log
import com.mancel.yann.qrcool.BuildConfig

/**
 * Created by Yann MANCEL on 29/09/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.utils
 */

fun <T: Any> T.logCoroutineOnDebug(msg: String? = null) {
    if (BuildConfig.DEBUG)
        Log.d(
            this.javaClass.simpleName,
            "Running on: [${Thread.currentThread().name}] ${msg ?: ""}"
        )
}