package com.mancel.yann.qrcool.utils

import android.content.Context
import com.mancel.yann.qrcool.R
import com.mancel.yann.qrcool.models.*

/**
 * Created by Yann MANCEL on 17/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.utils
 */
object BarcodeTools {

    /** Updates the data in structured format */
    fun getStructuredDataOfBarcode(context: Context, barcode: BarcodeOverlay) : List<String?> {
        return when (barcode) {
            is TextBarcode -> {
                listOf(
                    null,
                    context.getString(
                        R.string.raw_value_to_text,
                        barcode._rawValue ?: context.getString(R.string.no_raw_value)
                    ),
                    null
                )
            }

            is WifiBarcode -> {
                listOf(
                    context.getString(
                        R.string.ssid_of_wifi,
                        barcode._sSID ?: "-"
                    ),
                    context.getString(
                        R.string.password_of_wifi,
                        barcode._password ?: "-"
                    ),
                    context.getString(
                        R.string.encryption_type_of_wifi,
                        barcode._encryptionType
                    )
                )
            }

            is UrlBarcode -> {
                listOf(
                    if (barcode._title.isNullOrEmpty())
                        null
                    else
                        context.getString(
                            R.string.title_of_url,
                            barcode._title
                        ),
                    context.getString(
                        R.string.url_of_url,
                        barcode._url ?: "-"
                    ),
                    null
                )
            }

            is SMSBarcode -> {
                listOf(
                    context.getString(
                        R.string.phone_number_of_sms,
                        barcode._phoneNumber ?: "-"
                    ),
                    context.getString(
                        R.string.message_of_sms,
                        barcode._message ?: "-"
                    ),
                    null
                )
            }

            is GeoPointBarcode -> {
                listOf(
                    context.getString(
                        R.string.latitude_of_geo_point,
                        "${barcode._latitude ?: "-"}"
                    ),
                    context.getString(
                        R.string.longitude_of_geo_point,
                        "${barcode._longitude ?: "-"}"
                    ),
                    null
                )
            }
        }
    }

    /** Combine data from several LiveData */
    fun <T : BarcodeOverlay> combineDataFromSeveralLiveData(
        currentData: List<BarcodeOverlay>?,
        newData: List<T>,
        cls: Class<T>
    ) : List<BarcodeOverlay> {
        // Current data is null
        if (currentData.isNullOrEmpty()) {
            return mutableListOf<BarcodeOverlay>().apply {
                addAll(newData)
            }
        }

        // Current data with the SAME CLASS of new data
        val currentDataWithSameInstance = currentData.filterIsInstance(cls) as List<BarcodeOverlay>

        // Filter to have only barcodes that are really new
        /*
            currentDataOfSameInstance    newData
                B                           B
                B                           B
                B                           B
                                           [B]
        */
        val dataToAdd = newData.filterNot { newBarcode ->
            currentDataWithSameInstance.any { currentBarcode ->
                currentBarcode._rawValue == newBarcode._rawValue
            }
        }

        // Filter to have only barcodes that have been removed
        /*
            currentDataOfSameInstance    newData
                B                           B
                B                          [ ]
                B                           B
        */
        val dataToRemove = currentDataWithSameInstance.filterNot { currentBarcode ->
            newData.any { newBarcode ->
                newBarcode._rawValue == currentBarcode._rawValue
            }
        }

        return mutableListOf<BarcodeOverlay>().apply {
            addAll(currentData)
            addAll(dataToAdd)
            removeAll(dataToRemove)
        }
    }

    /**
     * Creates a new [BarcodeOverlay] from a copy of a [BarcodeOverlay] in argument.
     * The only difference is the id equals to 0 for the new [BarcodeOverlay]
     */
    fun createNewBarcodeFromCopy(barcode: BarcodeOverlay) : BarcodeOverlay {
        return when (barcode) {
            is TextBarcode -> { barcode.copy(_id = 0L) }
            is WifiBarcode -> { barcode.copy(_id = 0L) }
            is UrlBarcode -> { barcode.copy(_id = 0L) }
            is SMSBarcode -> { barcode.copy(_id = 0L) }
            is GeoPointBarcode -> { barcode.copy(_id = 0L) }
        }
    }
}