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

    /**
     * Updates the data in structured format
     */
    fun getStructuredDataOfBarcode(context: Context, barcode: BarcodeOverlay) : String {
        return when (barcode) {
            is TextBarcode -> {
                context.getString(
                    R.string.raw_value_to_text,
                    barcode._rawValue ?: context.getString(R.string.no_raw_value)
                )
            }

            is WifiBarcode -> {
                context.getString(
                    R.string.raw_value_to_wifi,
                    barcode._sSID ?: "-",
                    barcode._password ?: "-",
                    barcode._encryptionType
                )
            }

            is UrlBarcode -> {
                context.getString(
                    R.string.raw_value_to_url,
                    barcode._title ?: "-",
                    barcode._url ?: "-"
                )
            }

            is SMSBarcode -> {
                context.getString(
                    R.string.raw_value_to_sms,
                    barcode._phoneNumber ?: "-",
                    barcode._message ?: "-"
                )
            }

            is GeoPointBarcode -> {
                context.getString(
                    R.string.raw_value_to_geo,
                    "${barcode._latitude ?: "-"}",
                    "${barcode._longitude ?: "-"}"
                )
            }
        }
    }
}