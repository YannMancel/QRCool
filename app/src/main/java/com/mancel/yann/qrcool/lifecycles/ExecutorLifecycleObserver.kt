package com.mancel.yann.qrcool.lifecycles

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Yann MANCEL on 05/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.lifecycles
 *
 * A [LifecycleObserver] subclass. It is a Lifecycle-aware component.
 */
class ExecutorLifecycleObserver : LifecycleObserver {

    // FIELDS --------------------------------------------------------------------------------------

    val _executor: ExecutorService = Executors.newSingleThreadExecutor()

    // METHODS -------------------------------------------------------------------------------------

    /**
     * This method will be called when the event of lifecycle is [Lifecycle.Event.ON_DESTROY].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        this._executor.shutdown()
    }
}