package com.mancel.yann.qrcool.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Yann MANCEL on 22/07/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.models
 */
@Parcelize
data class QRCode(
    val _rawValue: String
) : Parcelable