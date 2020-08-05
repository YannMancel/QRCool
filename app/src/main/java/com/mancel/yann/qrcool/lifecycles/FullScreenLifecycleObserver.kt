package com.mancel.yann.qrcool.lifecycles

import android.app.Activity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * Created by Yann MANCEL on 05/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.lifecycles
 *
 * A [LifecycleObserver] subclass. It is a Lifecycle-aware component.
 */
class FullScreenLifecycleObserver(
    private val _lifecycle: Lifecycle,
    private val _activity: Activity
) : LifecycleObserver {

    // FIELDS --------------------------------------------------------------------------------------

    private var _oldSystemUiVisibility: Int = 0

    // METHODS -------------------------------------------------------------------------------------

    /**
     * This method will be called when the event of lifecycle is [Lifecycle.Event.ON_RESUME].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (this._lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            // Old configuration
            this._oldSystemUiVisibility = this._activity.window.decorView.systemUiVisibility

            // Show in full screen and hide action bar
            this._activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            (this._activity as AppCompatActivity).supportActionBar?.hide()
        }
    }

    /**
     * This method will be called when the event of lifecycle is [Lifecycle.Event.ON_PAUSE].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        // Reset configuration
        this._activity.window.decorView.systemUiVisibility = this._oldSystemUiVisibility

        // Show action bar
        (this._activity as AppCompatActivity).supportActionBar?.show()
    }
}