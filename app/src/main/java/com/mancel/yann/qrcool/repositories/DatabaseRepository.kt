package com.mancel.yann.qrcool.repositories

import androidx.lifecycle.LiveData
import com.mancel.yann.qrcool.models.*

/**
 * Created by Yann MANCEL on 23/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.repositories
 */
interface DatabaseRepository {

    // METHODS -------------------------------------------------------------------------------------

    // -- Create --

    suspend fun insertTextBarcodes(vararg barcodes: TextBarcode): List<Long>
    suspend fun insertWifiBarcodes(vararg barcodes: WifiBarcode): List<Long>
    suspend fun insertUrlBarcodes(vararg barcodes: UrlBarcode): List<Long>
    suspend fun insertSMSBarcodes(vararg barcodes: SMSBarcode): List<Long>
    suspend fun insertGeoPointBarcodes(vararg barcodes: GeoPointBarcode): List<Long>

    // -- Read --

    fun getTextBarcodes(): LiveData<List<TextBarcode>>
    fun getWifiBarcodes(): LiveData<List<WifiBarcode>>
    fun getUrlBarcodes(): LiveData<List<UrlBarcode>>
    fun getSMSBarcodes(): LiveData<List<SMSBarcode>>
    fun getGeoPointBarcodes(): LiveData<List<GeoPointBarcode>>

    // -- Delete --

    suspend fun removeTextBarcodes(vararg barcodes: TextBarcode): Int
    suspend fun removeWifiBarcodes(vararg barcodes: WifiBarcode): Int
    suspend fun removeUrlBarcodes(vararg barcodes: UrlBarcode): Int
    suspend fun removeSMSBarcodes(vararg barcodes: SMSBarcode): Int
    suspend fun removeGeoPointBarcodes(vararg barcodes: GeoPointBarcode): Int
}