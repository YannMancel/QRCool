package com.mancel.yann.qrcool.databases

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mancel.yann.qrcool.models.*

/**
 * Created by Yann MANCEL on 22/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.databases
 */
@Dao
interface BarcodeDAO {

    // METHODS -------------------------------------------------------------------------------------

    // -- Create --

    @Insert
    fun insertTextBarcodes(vararg barcodes: TextBarcode): List<Long>

    @Insert
    fun insertWifiBarcodes(vararg barcodes: WifiBarcode): List<Long>

    @Insert
    fun insertUrlBarcodes(vararg barcodes: UrlBarcode): List<Long>

    @Insert
    fun insertSMSBarcodes(vararg barcodes: SMSBarcode): List<Long>

    @Insert
    fun insertGeoPointBarcodes(vararg barcodes: GeoPointBarcode): List<Long>

    // -- Read --

    @Query("""
        SELECT *
        FROM text_barcode
    """)
    fun getTextBarcodes(): LiveData<List<TextBarcode>>

    @Query("""
        SELECT *
        FROM wifi_barcode
    """)
    fun getWifiBarcodes(): LiveData<List<WifiBarcode>>

    @Query("""
        SELECT *
        FROM url_barcode
    """)
    fun getUrlBarcodes(): LiveData<List<UrlBarcode>>

    @Query("""
        SELECT *
        FROM sms_barcode
    """)
    fun getSMSBarcodes(): LiveData<List<SMSBarcode>>

    @Query("""
        SELECT *
        FROM geo_point_barcode
    """)
    fun getGeoPointBarcodes(): LiveData<List<GeoPointBarcode>>
}