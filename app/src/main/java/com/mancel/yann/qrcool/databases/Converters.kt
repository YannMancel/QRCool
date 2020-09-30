package com.mancel.yann.qrcool.databases

import androidx.room.TypeConverter
import com.mancel.yann.qrcool.models.BarcodeOverlay
import java.util.*

/**
 * Created by Yann MANCEL on 22/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.databases
 */
class Converters {

    // METHODS -------------------------------------------------------------------------------------

    // -- Date --

    @TypeConverter
    fun dateFromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    // -- BarcodeOverlay.BarcodeType --

    @TypeConverter
    fun typeFromEnumName(enumName: String?): BarcodeOverlay.BarcodeType? {
        return when (enumName) {
            BarcodeOverlay.BarcodeType.TYPE_TEXT.name -> BarcodeOverlay.BarcodeType.TYPE_TEXT
            BarcodeOverlay.BarcodeType.TYPE_WIFI.name -> BarcodeOverlay.BarcodeType.TYPE_WIFI
            BarcodeOverlay.BarcodeType.TYPE_URL.name -> BarcodeOverlay.BarcodeType.TYPE_URL
            BarcodeOverlay.BarcodeType.TYPE_SMS.name -> BarcodeOverlay.BarcodeType.TYPE_SMS
            BarcodeOverlay.BarcodeType.TYPE_GEO.name -> BarcodeOverlay.BarcodeType.TYPE_GEO
            else -> null
        }
    }

    @TypeConverter
    fun typeToEnumName(barcodeType: BarcodeOverlay.BarcodeType?): String? {
        return when (barcodeType) {
            BarcodeOverlay.BarcodeType.TYPE_TEXT -> BarcodeOverlay.BarcodeType.TYPE_TEXT.name
            BarcodeOverlay.BarcodeType.TYPE_WIFI -> BarcodeOverlay.BarcodeType.TYPE_WIFI.name
            BarcodeOverlay.BarcodeType.TYPE_URL -> BarcodeOverlay.BarcodeType.TYPE_URL.name
            BarcodeOverlay.BarcodeType.TYPE_SMS -> BarcodeOverlay.BarcodeType.TYPE_SMS.name
            BarcodeOverlay.BarcodeType.TYPE_GEO -> BarcodeOverlay.BarcodeType.TYPE_GEO.name
            else -> null
        }
    }

    // -- BarcodeOverlay.BarcodeFormat --

    @TypeConverter
    fun formatFromEnumName(enumName: String?): BarcodeOverlay.BarcodeFormat? {
        return when (enumName) {
            BarcodeOverlay.BarcodeFormat.FORMAT_UNKNOWN.name -> BarcodeOverlay.BarcodeFormat.FORMAT_UNKNOWN
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D.name -> BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_2D.name -> BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_2D
            else -> null
        }
    }

    @TypeConverter
    fun formatToEnumName(barcodeFormat: BarcodeOverlay.BarcodeFormat?): String? {
        return when (barcodeFormat) {
            BarcodeOverlay.BarcodeFormat.FORMAT_UNKNOWN -> BarcodeOverlay.BarcodeFormat.FORMAT_UNKNOWN.name
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D -> BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D.name
            BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_2D -> BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_2D.name
            else -> null
        }
    }

    // -- BarcodeOverlay.WifiType --

    @TypeConverter
    fun wifiTypeFromEnumName(enumName: String?): BarcodeOverlay.WifiType? {
        return when (enumName) {
            BarcodeOverlay.WifiType.TYPE_OPEN.name -> BarcodeOverlay.WifiType.TYPE_OPEN
            BarcodeOverlay.WifiType.TYPE_WPA.name -> BarcodeOverlay.WifiType.TYPE_WPA
            BarcodeOverlay.WifiType.TYPE_WEP.name -> BarcodeOverlay.WifiType.TYPE_WEP
            else -> null
        }
    }

    @TypeConverter
    fun wifiTypeToEnumName(wifiType: BarcodeOverlay.WifiType?): String? {
        return when (wifiType) {
            BarcodeOverlay.WifiType.TYPE_OPEN -> BarcodeOverlay.WifiType.TYPE_OPEN.name
            BarcodeOverlay.WifiType.TYPE_WPA -> BarcodeOverlay.WifiType.TYPE_WPA.name
            BarcodeOverlay.WifiType.TYPE_WEP -> BarcodeOverlay.WifiType.TYPE_WEP.name
            else -> null
        }
    }
}