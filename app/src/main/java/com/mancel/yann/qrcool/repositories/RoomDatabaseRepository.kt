package com.mancel.yann.qrcool.repositories

import androidx.lifecycle.LiveData
import com.mancel.yann.qrcool.databases.BarcodeDAO
import com.mancel.yann.qrcool.models.*

/**
 * Created by Yann MANCEL on 23/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool.repositories
 *
 * A class which implements [DatabaseRepository].
 */
class RoomDatabaseRepository(
    private val _barcodeDAO: BarcodeDAO
) : DatabaseRepository {

    // METHODS -------------------------------------------------------------------------------------

    // -- Create --

    override suspend fun insertTextBarcodes(vararg barcodes: TextBarcode): List<Long> =
        this._barcodeDAO.insertTextBarcodes(*barcodes)

    override suspend fun insertWifiBarcodes(vararg barcodes: WifiBarcode): List<Long> =
        this._barcodeDAO.insertWifiBarcodes(*barcodes)

    override suspend fun insertUrlBarcodes(vararg barcodes: UrlBarcode): List<Long> =
        this._barcodeDAO.insertUrlBarcodes(*barcodes)

    override suspend fun insertSMSBarcodes(vararg barcodes: SMSBarcode): List<Long> =
        this._barcodeDAO.insertSMSBarcodes(*barcodes)

    override suspend fun insertGeoPointBarcodes(vararg barcodes: GeoPointBarcode): List<Long> =
        this._barcodeDAO.insertGeoPointBarcodes(*barcodes)

    // -- Read --

    override fun getTextBarcodes(): LiveData<List<TextBarcode>> =
        this._barcodeDAO.getTextBarcodes()

    override fun getWifiBarcodes(): LiveData<List<WifiBarcode>> =
        this._barcodeDAO.getWifiBarcodes()

    override fun getUrlBarcodes(): LiveData<List<UrlBarcode>> =
        this._barcodeDAO.getUrlBarcodes()

    override fun getSMSBarcodes(): LiveData<List<SMSBarcode>> =
        this._barcodeDAO.getSMSBarcodes()

    override fun getGeoPointBarcodes(): LiveData<List<GeoPointBarcode>> =
        this._barcodeDAO.getGeoPointBarcodes()

    // -- Delete --

    override suspend fun removeTextBarcodes(vararg barcodes: TextBarcode): Int =
        this._barcodeDAO.removeTextBarcodes(*barcodes)

    override suspend fun removeWifiBarcodes(vararg barcodes: WifiBarcode): Int =
        this._barcodeDAO.removeWifiBarcodes(*barcodes)

    override suspend fun removeUrlBarcodes(vararg barcodes: UrlBarcode): Int =
        this._barcodeDAO.removeUrlBarcodes(*barcodes)

    override suspend fun removeSMSBarcodes(vararg barcodes: SMSBarcode): Int =
        this._barcodeDAO.removeSMSBarcodes(*barcodes)

    override suspend fun removeGeoPointBarcodes(vararg barcodes: GeoPointBarcode): Int =
        this._barcodeDAO.removeGeoPointBarcodes(*barcodes)
}