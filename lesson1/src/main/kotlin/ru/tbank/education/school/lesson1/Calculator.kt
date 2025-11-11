package ru.tbank.education.school.lesson1

import jdk.dynalink.Operation
/**
 * Метод для вычисления простых арифметических операций.
 */
fun calculate(a: Double, b: Double, operation: OperationType): Double? {
    return when(operation) {
        OperationType.ADD -> a + b
        OperationType.SUBTRACT -> a - b
        OperationType.MULTIPLY -> a * b
        OperationType.DIVIDE -> {
            if (b != 0.0) a / b else null
        }
    }
}

/**
 * Функция вычисления выражения, представленного строкой
 * @return результат вычисления строки или null, если вычисление невозможно
 * @sample "5 * 2".calculate()
 */
@Suppress("ReturnCount")
fun String.calculate(): Double? {
    val p = this.split(" ")
    val a = p[0].toDoubleOrNull() ?: return null
    val b = p[2].toDoubleOrNull() ?: return null
    val operation = when (p[1]) {
        "+" -> OperationType.ADD
        "-" -> OperationType.SUBTRACT
        "*" -> OperationType.MULTIPLY
        "/" -> OperationType.DIVIDE
        else -> null
    }

    return operation?.let { calculate(a, b, it) }
}

fun main() {
    println("5 + 2".calculate())
    println("10 * 3".calculate())
    println("8 / 0".calculate())
    println("5 - 3".calculate())
}
