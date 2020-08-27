package com.mancel.yann.qrcool

import com.mancel.yann.qrcool.models.BarcodeOverlay
import com.mancel.yann.qrcool.models.TextBarcode
import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * Created by Yann MANCEL on 19/08/2020.
 * Name of the project: QRCool
 * Name of the package: com.mancel.yann.qrcool
 *
 * Tests on [BarcodeOverlay].
 * It is a sealed class and its children are data classes.
 */
class BarcodeOverlayTest {

    // METHODS -------------------------------------------------------------------------------------

    @Test
    fun `equal is correct for data class from sealed class`() {
        val barcode1 = TextBarcode(
            _id = 1L,
            _rawValue = "same_raw_value",
            _type = BarcodeOverlay.BarcodeType.TYPE_TEXT,
            _format = BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D,
            _date = Date(5L)
        )

        // Equals to barcode 1
        val barcode2 = TextBarcode(
            _id = 1L,
            _rawValue = "same_raw_value",
            _type = BarcodeOverlay.BarcodeType.TYPE_TEXT,
            _format = BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D,
            _date = Date(5L)
        )

        // Not equals to barcode 1 (id is different)
        val barcode3 = TextBarcode(
            _id = 2L,
            _rawValue = "same_raw_value",
            _type = BarcodeOverlay.BarcodeType.TYPE_TEXT,
            _format = BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D,
            _date = Date(5L)
        )

        // TEST
        assertEquals(barcode1, barcode2)
        assertNotSame(barcode1, barcode3)
    }

    @Test
    fun `copy is correct for data class from sealed class`() {
        val barcode = TextBarcode(
            _id = 1L,
            _rawValue = "same_raw_value",
            _type = BarcodeOverlay.BarcodeType.TYPE_TEXT,
            _format = BarcodeOverlay.BarcodeFormat.FORMAT_BARCODE_1D,
            _date = Date(5L)
        )

        // THEN: Copy
        val newBarcode = barcode.copy(_id = 0L)

        // TEST
        assertNotSame(barcode, newBarcode)
        assertNotEquals(barcode._id, newBarcode._id)
        assertEquals(barcode._rawValue, newBarcode._rawValue)
        assertEquals(barcode._format, newBarcode._format)
        assertEquals(barcode._type, newBarcode._type)
        assertEquals(barcode._date, newBarcode._date)
    }
}