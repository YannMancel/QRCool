package com.mancel.yann.qrcool.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Yann MANCEL on 15/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.models
 */
sealed class BarcodeOverlay {

    // FIELDS --------------------------------------------------------------------------------------

    abstract val _rawValue: String?
    abstract val _type: BarcodeType
    abstract val _format: BarcodeFormat
    abstract val _date: Date

    // ENUMS ---------------------------------------------------------------------------------------

    enum class BarcodeFormat {
        FORMAT_UNKNOWN,
        FORMAT_BARCODE_1D,
        FORMAT_BARCODE_2D
    }

    enum class BarcodeType {
        TYPE_TEXT,
        TYPE_WIFI,
        TYPE_URL,
        TYPE_SMS,
        TYPE_GEO
    }

    enum class WifiType {
        TYPE_OPEN,
        TYPE_WPA,
        TYPE_WEP
    }
}

@Parcelize
data class TextBarcode(
    override val _rawValue: String?,
    override val _type: BarcodeType,
    override val _format: BarcodeFormat,
    override val _date: Date = Date()
) : BarcodeOverlay(), Parcelable

@Parcelize
data class WifiBarcode(
    override val _rawValue: String?,
    override val _type: BarcodeType,
    override val _format: BarcodeFormat,
    override val _date: Date = Date(),
    val _sSID: String?,
    val _password: String?,
    val _encryptionType: WifiType
) : BarcodeOverlay(), Parcelable

@Parcelize
data class UrlBarcode(
    override val _rawValue: String?,
    override val _type: BarcodeType,
    override val _format: BarcodeFormat,
    override val _date: Date = Date(),
    val _title: String?,
    val _url: String?
) : BarcodeOverlay(), Parcelable

@Parcelize
data class SMSBarcode(
    override val _rawValue: String?,
    override val _type: BarcodeType,
    override val _format: BarcodeFormat,
    override val _date: Date = Date(),
    val _phoneNumber: String?,
    val _message: String?
) : BarcodeOverlay(), Parcelable

@Parcelize
data class GeoPointBarcode(
    override val _rawValue: String?,
    override val _type: BarcodeType,
    override val _format: BarcodeFormat,
    override val _date: Date = Date(),
    val _latitude: Double?,
    val _longitude: Double?
) : BarcodeOverlay(), Parcelable