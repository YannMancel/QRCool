package com.mancel.yann.qrcool

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mancel.yann.qrcool.databases.AppDatabase
import com.mancel.yann.qrcool.databases.BarcodeDAO
import com.mancel.yann.qrcool.koin.roomTestModule
import com.mancel.yann.qrcool.models.*
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.dsl.koinApplication
import java.io.IOException
import java.util.*

/**
 * Created by Yann MANCEL on 22/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool
 *
 * An android test on [AppDatabase] and its DAO.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    // FIELDS --------------------------------------------------------------------------------------

    private lateinit var _koin: Koin
    private lateinit var _database: AppDatabase
    private lateinit var _barcodeDAO: BarcodeDAO

    // METHODS -------------------------------------------------------------------------------------

    @Before
    @Throws(Exception::class)
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()

         this._koin =
             koinApplication {
                androidContext(context)
                modules(roomTestModule)
            }.koin

        this._database = this._koin.get()
        this._barcodeDAO = this._koin.get()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        this._database.close()
        this._koin.close()
    }

    @Test
    fun insert_then_get_then_remove_textBarcode_shouldBeSuccess() = runBlocking {
        val barcode = TextBarcode(
            _rawValue = "raw value",
            _type = BarcodeOverlay.BarcodeType.TYPE_TEXT,
            _format = BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D,
            _date = Date(5L)
        )

        // BEFORE: Add barcode
        val ids = this@AppDatabaseTest._barcodeDAO.insertTextBarcodes(barcode)

        // TEST
        assertEquals(1, ids.size)
        assertEquals(1L, ids[0])

        // THEN: Retrieve barcode
        val barcodes: List<TextBarcode>
        val flow = this@AppDatabaseTest._barcodeDAO.getTextBarcodes()

        /*
            // With LiveData:

                // A JUnit rule that configures LiveData to execute each task synchronously
                @get:Rule
                val rule = InstantTaskExecutorRule()

                barcodes = LiveDataTestUtil.getValue(flow.asLiveData())

            // With launch(), collect() and join():

                val job = launch {
                    flow.take(1)
                        .collect {
                            barcodes = it
                        }
                }
                job.join()
         */

        val deferredBarcodes = async {
            flow.take(1)
                .single()
        }
        barcodes = deferredBarcodes.await()

        // TEST
        assertEquals(1, barcodes.size)
        assertEquals(1L, barcodes[0]._id)
        assertEquals(barcode._rawValue, barcodes[0]._rawValue)
        assertEquals(barcode._type, barcodes[0]._type)
        assertEquals(barcode._format, barcodes[0]._format)
        assertEquals(barcode._date, barcodes[0]._date)

        // THEN: Remove barcode
        val deletedRows = this@AppDatabaseTest._barcodeDAO.removeTextBarcodes(barcodes[0])

        // TEST
        assertEquals(1, deletedRows)
    }

    @Test
    fun insert_then_get_then_remove_wifiBarcode_shouldBeSuccess() = runBlocking {
        val barcode = WifiBarcode(
            _rawValue = "raw value",
            _type = BarcodeOverlay.BarcodeType.TYPE_TEXT,
            _format = BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D,
            _date = Date(5L),
            _sSID = "ssid",
            _password = "password",
            _encryptionType = BarcodeOverlay.WifiType.TYPE_OPEN
        )

        // BEFORE: Add barcode
        val ids = this@AppDatabaseTest._barcodeDAO.insertWifiBarcodes(barcode)

        // TEST
        assertEquals(1, ids.size)
        assertEquals(1L, ids[0])

        // THEN: Retrieve barcode
        val flow = this@AppDatabaseTest._barcodeDAO.getWifiBarcodes()

        val deferredBarcodes = async {
            flow.take(1)
                .single()
        }
        val barcodes = deferredBarcodes.await()

        // TEST
        assertEquals(1, barcodes.size)
        assertEquals(1L, barcodes[0]._id)
        assertEquals(barcode._rawValue, barcodes[0]._rawValue)
        assertEquals(barcode._type, barcodes[0]._type)
        assertEquals(barcode._format, barcodes[0]._format)
        assertEquals(barcode._date, barcodes[0]._date)
        assertEquals(barcode._sSID, barcodes[0]._sSID)
        assertEquals(barcode._password, barcodes[0]._password)
        assertEquals(barcode._encryptionType, barcodes[0]._encryptionType)

        // THEN: Remove barcode
        val deletedRows = this@AppDatabaseTest._barcodeDAO.removeWifiBarcodes(barcodes[0])

        // TEST
        assertEquals(1, deletedRows)
    }

    @Test
    fun insert_then_get_then_remove_urlBarcode_shouldBeSuccess() = runBlocking {
        val barcode = UrlBarcode(
            _rawValue = "raw value",
            _type = BarcodeOverlay.BarcodeType.TYPE_TEXT,
            _format = BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D,
            _date = Date(5L),
            _title = "title",
            _url = "url"
        )

        // BEFORE: Add barcode
        val ids = this@AppDatabaseTest._barcodeDAO.insertUrlBarcodes(barcode)

        // TEST
        assertEquals(1, ids.size)
        assertEquals(1L, ids[0])

        // THEN: Retrieve barcode
        val flow = this@AppDatabaseTest._barcodeDAO.getUrlBarcodes()

        val deferredBarcodes = async {
            flow.take(1)
                .single()
        }
        val barcodes = deferredBarcodes.await()

        // TEST
        assertEquals(1, barcodes.size)
        assertEquals(1L, barcodes[0]._id)
        assertEquals(barcode._rawValue, barcodes[0]._rawValue)
        assertEquals(barcode._type, barcodes[0]._type)
        assertEquals(barcode._format, barcodes[0]._format)
        assertEquals(barcode._date, barcodes[0]._date)
        assertEquals(barcode._title, barcodes[0]._title)
        assertEquals(barcode._url, barcodes[0]._url)

        // THEN: Remove barcode
        val deletedRows = this@AppDatabaseTest._barcodeDAO.removeUrlBarcodes(barcodes[0])

        // TEST
        assertEquals(1, deletedRows)
    }

    @Test
    fun insert_then_get_then_remove_smsBarcode_shouldBeSuccess() = runBlocking {
        val barcode = SMSBarcode(
            _rawValue = "raw value",
            _type = BarcodeOverlay.BarcodeType.TYPE_TEXT,
            _format = BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D,
            _date = Date(5L),
            _phoneNumber = "phone number",
            _message = "message"
        )

        // BEFORE: Add barcode
        val ids = this@AppDatabaseTest._barcodeDAO.insertSMSBarcodes(barcode)

        // TEST
        assertEquals(1, ids.size)
        assertEquals(1L, ids[0])

        // THEN: Retrieve barcode
        val flow = this@AppDatabaseTest._barcodeDAO.getSMSBarcodes()

        val deferredBarcodes = async {
            flow.take(1)
                .single()
        }
        val barcodes = deferredBarcodes.await()

        // TEST
        assertEquals(1, barcodes.size)
        assertEquals(1L, barcodes[0]._id)
        assertEquals(barcode._rawValue, barcodes[0]._rawValue)
        assertEquals(barcode._type, barcodes[0]._type)
        assertEquals(barcode._format, barcodes[0]._format)
        assertEquals(barcode._date, barcodes[0]._date)
        assertEquals(barcode._phoneNumber, barcodes[0]._phoneNumber)
        assertEquals(barcode._message, barcodes[0]._message)

        // THEN: Remove barcode
        val deletedRows = this@AppDatabaseTest._barcodeDAO.removeSMSBarcodes(barcodes[0])

        // TEST
        assertEquals(1, deletedRows)
    }

    @Test
    fun insert_then_get_then_remove_geoPointBarcode_shouldBeSuccess() = runBlocking {
        val barcode = GeoPointBarcode(
            _rawValue = "raw value",
            _type = BarcodeOverlay.BarcodeType.TYPE_TEXT,
            _format = BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D,
            _date = Date(5L),
            _latitude = 1.0,
            _longitude = 2.0
        )

        // BEFORE: Add barcode
        val ids = this@AppDatabaseTest._barcodeDAO.insertGeoPointBarcodes(barcode)

        // TEST
        assertEquals(1, ids.size)
        assertEquals(1L, ids[0])

        // THEN: Retrieve barcode
        val flow = this@AppDatabaseTest._barcodeDAO.getGeoPointBarcodes()

        val deferredBarcodes = async {
            flow.take(1)
                .single()
        }
        val barcodes = deferredBarcodes.await()

        // TEST
        assertEquals(1, barcodes.size)
        assertEquals(1L, barcodes[0]._id)
        assertEquals(barcode._rawValue, barcodes[0]._rawValue)
        assertEquals(barcode._type, barcodes[0]._type)
        assertEquals(barcode._format, barcodes[0]._format)
        assertEquals(barcode._date, barcodes[0]._date)
        assertEquals(barcode._latitude, barcodes[0]._latitude)
        assertEquals(barcode._longitude, barcodes[0]._longitude)

        // THEN: Remove barcode
        val deletedRows = this@AppDatabaseTest._barcodeDAO.removeGeoPointBarcodes(barcodes[0])

        // TEST
        assertEquals(1, deletedRows)
    }
}