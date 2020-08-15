package com.mancel.yann.qrcool.views.activities

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Yann MANCEL on 21/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.views.activities
 *
 * An abstract [AppCompatActivity] subclass.
 */
abstract class BaseActivity : AppCompatActivity() {

    // METHODS -------------------------------------------------------------------------------------

    /**
     * Gets the integer value of the activity layout
     * @return an integer that corresponds to the activity layout
     */
    @LayoutRes
    protected abstract fun getActivityLayout(): Int

    /**
     * Calls this method on [AppCompatActivity]#onCreate method
     */
    protected abstract fun doOnCreate()

    // -- AppCompatActivity --

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(this.getActivityLayout())
        this.doOnCreate()
    }
}