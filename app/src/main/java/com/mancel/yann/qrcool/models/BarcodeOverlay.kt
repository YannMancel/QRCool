package com.mancel.yann.qrcool.models

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.Comparator

/**
 * Created by Yann MANCEL on 15/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.models
 */
sealed class BarcodeOverlay {

    // FIELDS --------------------------------------------------------------------------------------

    abstract val _id: Long
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

    // CLASSES -------------------------------------------------------------------------------------

    /**
     * A [Comparator] of [BarcodeOverlay] subclass.
     */
    class AscendingDateComparator : Comparator<BarcodeOverlay> {
        override fun compare(left: BarcodeOverlay?, right: BarcodeOverlay?): Int {
            // Comparison on the date
            val dateLeft = left?._date?.time ?: 0L
            val dateRight = right?._date?.time ?: 0L

            return dateRight.compareTo(dateLeft)
        }
    }
}

// Annotation for the ProGuard considerations of Navigation component
@Keep
@Entity(tableName = "text_barcode")
@Parcelize
data class TextBarcode(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_barcode") override val _id: Long = 0L,
    @ColumnInfo(name = "raw_value") override val _rawValue: String?,
    @ColumnInfo(name = "type") override val _type: BarcodeType,
    @ColumnInfo(name = "format") override val _format: BarcodeFormat,
    @ColumnInfo(name = "date") override val _date: Date = Date()
) : BarcodeOverlay(), Parcelable

// Annotation for the ProGuard considerations of Navigation component
@Keep
@Entity(tableName = "wifi_barcode")
@Parcelize
data class WifiBarcode(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_barcode") override val _id: Long = 0L,
    @ColumnInfo(name = "raw_value") override val _rawValue: String?,
    @ColumnInfo(name = "type") override val _type: BarcodeType,
    @ColumnInfo(name = "format") override val _format: BarcodeFormat,
    @ColumnInfo(name = "date") override val _date: Date = Date(),
    @ColumnInfo(name = "ssid") val _sSID: String?,
    @ColumnInfo(name = "password") val _password: String?,
    @ColumnInfo(name = "encryption_type") val _encryptionType: WifiType
) : BarcodeOverlay(), Parcelable

// Annotation for the ProGuard considerations of Navigation component
@Keep
@Entity(tableName = "url_barcode")
@Parcelize
data class UrlBarcode(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_barcode") override val _id: Long = 0L,
    @ColumnInfo(name = "raw_value") override val _rawValue: String?,
    @ColumnInfo(name = "type") override val _type: BarcodeType,
    @ColumnInfo(name = "format") override val _format: BarcodeFormat,
    @ColumnInfo(name = "date") override val _date: Date = Date(),
    @ColumnInfo(name = "title") val _title: String?,
    @ColumnInfo(name = "url") val _url: String?
) : BarcodeOverlay(), Parcelable

// Annotation for the ProGuard considerations of Navigation component
@Keep
@Entity(tableName = "sms_barcode")
@Parcelize
data class SMSBarcode(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_barcode") override val _id: Long = 0L,
    @ColumnInfo(name = "raw_value") override val _rawValue: String?,
    @ColumnInfo(name = "type") override val _type: BarcodeType,
    @ColumnInfo(name = "format") override val _format: BarcodeFormat,
    @ColumnInfo(name = "date") override val _date: Date = Date(),
    @ColumnInfo(name = "phone_number") val _phoneNumber: String?,
    @ColumnInfo(name = "message") val _message: String?
) : BarcodeOverlay(), Parcelable

// Annotation for the ProGuard considerations of Navigation component
@Keep
@Entity(tableName = "geo_point_barcode")
@Parcelize
data class GeoPointBarcode(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_barcode") override val _id: Long = 0L,
    @ColumnInfo(name = "raw_value") override val _rawValue: String?,
    @ColumnInfo(name = "type") override val _type: BarcodeType,
    @ColumnInfo(name = "format") override val _format: BarcodeFormat,
    @ColumnInfo(name = "date") override val _date: Date = Date(),
    @ColumnInfo(name = "latitude") val _latitude: Double?,
    @ColumnInfo(name = "longitude") val _longitude: Double?
) : BarcodeOverlay(), Parcelable