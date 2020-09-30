package com.mancel.yann.qrcool.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Yann MANCEL on 01/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.utils
 */
object MessageTools {

    fun showMessageWithSnackbar(
        view: View,
        message: String,
        textButton: String? = null,
        actionOnClick: View.OnClickListener? = null
    ) {
        Snackbar
            .make(view, message, Snackbar.LENGTH_SHORT)
            .setAction(textButton, actionOnClick)
            .show()
    }
}