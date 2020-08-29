package com.mancel.yann.qrcool.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mancel.yann.qrcool.models.*
import kotlinx.coroutines.flow.Flow

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
    suspend fun insertTextBarcodes(vararg barcodes: TextBarcode): List<Long>

    @Insert
    suspend fun insertWifiBarcodes(vararg barcodes: WifiBarcode): List<Long>

    @Insert
    suspend fun insertUrlBarcodes(vararg barcodes: UrlBarcode): List<Long>

    @Insert
    suspend fun insertSMSBarcodes(vararg barcodes: SMSBarcode): List<Long>

    @Insert
    suspend fun insertGeoPointBarcodes(vararg barcodes: GeoPointBarcode): List<Long>

    // -- Read --

    @Query("""
        SELECT *
        FROM text_barcode
    """)
    fun getTextBarcodes(): Flow<List<TextBarcode>>

    @Query("""
        SELECT *
        FROM wifi_barcode
    """)
    fun getWifiBarcodes(): Flow<List<WifiBarcode>>

    @Query("""
        SELECT *
        FROM url_barcode
    """)
    fun getUrlBarcodes(): Flow<List<UrlBarcode>>

    @Query("""
        SELECT *
        FROM sms_barcode
    """)
    fun getSMSBarcodes(): Flow<List<SMSBarcode>>

    @Query("""
        SELECT *
        FROM geo_point_barcode
    """)
    fun getGeoPointBarcodes(): Flow<List<GeoPointBarcode>>

    // -- Delete --

    @Delete
    suspend fun removeTextBarcodes(vararg barcodes: TextBarcode): Int

    @Delete
    suspend fun removeWifiBarcodes(vararg barcodes: WifiBarcode): Int

    @Delete
    suspend fun removeUrlBarcodes(vararg barcodes: UrlBarcode): Int

    @Delete
    suspend fun removeSMSBarcodes(vararg barcodes: SMSBarcode): Int

    @Delete
    suspend fun removeGeoPointBarcodes(vararg barcodes: GeoPointBarcode): Int
}