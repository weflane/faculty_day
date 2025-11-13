package ru.tbank.education.school.lesson1

/**
 * Сумма четных чисел.
 */
fun sumEvenNumbers(numbers: Array<Int>): Int {
    var sum = 0
    for (n in numbers) {
        if (n % 2 == 0) {
            sum += n
        }
    }
    return sum
}

fun main() {
    val a = arrayOf(2, 4)
    val x = arrayOf(1, 3, 5)
    val y = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    println(sumEvenNumbers(a))
    println(sumEvenNumbers(x))
    println(sumEvenNumbers(y))
}
