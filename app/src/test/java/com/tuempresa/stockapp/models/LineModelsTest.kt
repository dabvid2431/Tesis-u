package com.tuempresa.stockapp.models

import org.junit.Assert.assertEquals
import org.junit.Test

class LineModelsTest {

    @Test
    fun purchaseLine_subtotal_isQuantityTimesPrice() {
        val line = PurchaseLine(quantity = 3, price = 2.5)
        assertEquals(7.5, line.subtotal, 0.0001)
    }

    @Test
    fun saleLine_subtotal_isQuantityTimesPrice() {
        val line = SaleLine(quantity = 4, price = 1.25)
        assertEquals(5.0, line.subtotal, 0.0001)
    }
}