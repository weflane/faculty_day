package ru.tbank.education.school.lesson8.practise

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Сценарии для тестирования:
 *
 * 1. Позитивные сценарии (happy path):
 *    - Обычный случай: basePrice = 1000, discount = 10%, tax = 20% → проверить корректность формулы.
 *    - Без скидки: discountPercent = 0 → итог = basePrice + налог.
 *    - Без налога: taxPercent = 0 → итог = basePrice минус скидка.
 *    - Без скидки и без налога: итог = basePrice.
 *
 * 2. Негативные сценарии (исключения):
 *    - Отрицательная цена: basePrice < 0 → IllegalArgumentException.
 *    - Скидка вне диапазона: discountPercent < 0 или > 100 → IllegalArgumentException.
 *    - Налог вне диапазона: taxPercent < 0 или > 30 → IllegalArgumentException.
 */

class PriceCalculatorTest {

    fun calculateFinalPrice(
        basePrice: Double,
        discountPercent: Int,
        taxPercent: Int
    ): Double {
        require(value = basePrice >= 0) { "Base price must be >= 0" }
        require(value = discountPercent in 0..100) { "Discount must be 0..100" }
        require(value = taxPercent in 0..30) { "Tax must be 0..30" }

        val priceAfterDiscount = basePrice * (1 - discountPercent / 100.0)
        val taxAmount = priceAfterDiscount * (taxPercent / 100.0)

        val finalPrice = priceAfterDiscount + taxAmount

        return finalPrice.coerceAtLeast(minimumValue = 0.0)
    }

    @Test
    fun base() {
        val result = calculateFinalPrice(1000.0, 10, 20)
        assertEquals(1080.0, result)
    }

    @Test
    fun nodiscount() {
        val result = calculateFinalPrice(1000.0, 0, 20)
        assertEquals(1200.0, result)
    }

    @Test
    fun notax() {
        val result = calculateFinalPrice(1000.0, 10, 0)
        assertEquals(900.0, result)
    }

    @Test
    fun onlybase() {
        val result = calculateFinalPrice(1000.0, 0, 0)
        assertEquals(1000.0, result)
    }

}