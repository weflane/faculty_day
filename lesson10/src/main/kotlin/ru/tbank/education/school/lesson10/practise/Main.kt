import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.LocalDateTime

fun main() {
    task1()
    println()
    task2()
    println()
    task3()
    println()
    task4()
    println()
    task5()
    println()
    task6()
    println()
    task7()
    println()
    task8()
    println()
    //дз
    main1()
    println()
}

/*
1) Строки + регулярные выражения
["Name: Ivan, score=17", ...]
Извлечь имя и score, собрать пары, вывести победителя.
*/
fun task1() {
    val lines = listOf(
        "Name: Ivan, score=17",
        "Name: Olga, score=23",
        "Name: Max, score=5"
    )

    val re = Regex("""^Name:\s*([A-Za-z]+)\s*,\s*score=(\d+)\s*$""")

    val pairs: List<Pair<String, Int>> = lines.mapNotNull { s ->
        val m = re.find(s) ?: return@mapNotNull null
        val name = m.groupValues[1]
        val score = m.groupValues[2].toInt()
        name to score
    }

    println("Task 1 pairs: $pairs")

    val best = pairs.maxByOrNull { it.second }
    if (best != null) {
        println("Task 1 best: ${best.first} (${best.second})")
    } else {
        println("Task 1: no valid lines")
    }
}

/*
2) Даты + коллекции
["2026-01-22", ...]
Преобразовать в даты, отсортировать, посчитать сколько в январе 2026.
*/
fun task2() {
    val dateStrings = listOf(
        "2026-01-22",
        "2026-02-01",
        "2025-12-31",
        "2026-01-05"
    )

    val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    val dates = dateStrings.map { LocalDate.parse(it, fmt) }.sorted()

    println("Task 2 sorted dates: ${dates.joinToString { it.format(fmt) }}")

    val countJan2026 = dates.count { it.year == 2026 && it.month == Month.JANUARY }
    println("Task 2 count in Jan 2026: $countJan2026")
}

/*
3) Коллекции + строки
"apple orange apple banana orange apple"
Частоты слов, вывести слова с частотой > 1 по алфавиту.
*/
fun task3() {
    val text = "apple orange apple banana orange apple"

    val words = text.trim().split(Regex("""\s+""")).filter { it.isNotEmpty() }

    val freq = mutableMapOf<String, Int>()
    for (w in words) {
        freq[w] = (freq[w] ?: 0) + 1
    }

    println("Task 3 freq: $freq")

    val repeated = freq
        .filter { (_, c) -> c > 1 }
        .keys
        .sorted()

    println("Task 3 repeated words: ${repeated.joinToString(", ")}")
}

fun task4() {
    val array = arrayOf("A-123", "B-7", "AA-12", "C-001", "D-99x")
    println("Task 4: ${array.filter { it.first() in 'A'..'Z' && it[1] == '-' && it.length - 2 <= 3 && it.substring(2).all { char -> char.isDigit() }}}")
}

fun task5() {
    val a = listOf("  Hello   world  ", "A   B    C", "   one")
    val b = a.map{ it.trim() }
    val x = b.map { it.replace(Regex("\\s+"), " ")}
    println("Task 5: ${x}")
}

fun task6() {
    val a = listOf(
        "2026-01-01" to "2026-01-10",
        "2025-12-31" to "2026-01-01",
        "2026-02-01" to "2026-01-22"
    )
    val b = a.map { (x, y) ->
        Math.abs(ChronoUnit.DAYS.between(LocalDate.parse(x), LocalDate.parse(y)))
    }

    println("Task 6: $a")
}

fun task7() {
    val subjects = listOf("math:Ivan", "bio:Olga", "math:Max", "bio:Ivan", "cs:Olga")
    val result = subjects.map { it.split(":") }.groupBy({ it[0] }, { it[1] })
    println("Task 7 (using groupBy): $result")
}

fun task8(){
    val strings = listOf("Start at 2026/01/22 09:14", "No time here", "End: 22-01-2026 18:05")
    val results = mutableListOf<String>()
    val pattern = Regex("""(\d{2,4})[/-](\d{1,2})[/-](\d{2,4})\s+(\d{2}):(\d{2})""")
    for (str in strings) {
        val match = pattern.find(str)
        if (match != null) {
            val (year, month, day, hour, minute) = match.groupValues.drop(1)
            val (finalYear, finalMonth, finalDay) = if (year.length == 4) {
                Triple(year, month, day)
            } else {
                Triple(day, month, year)
            }
            val formatted = String.format(
                "%04d-%02d-%02d %02d:%02d",
                finalYear.toInt(),
                finalMonth.toInt(),
                finalDay.toInt(),
                hour.toInt(),
                minute.toInt()
            )
            results.add(formatted)
        }
    }
    println("Task 8: $results")
}


fun normalize(line: String): Map<String, Any>? {
    val trimmed = line.trim()
    val patternA = Regex("""(\d{4})-(\d{2})-(\d{2})\s+(\d{2}):(\d{2}).*?ID\s*:\s*(\d+).*?STATUS\s*:\s*(\w+)""", RegexOption.IGNORE_CASE)
    val patternB = Regex("""TS\s*=\s*(\d{2})/(\d{2})/(\d{4})-(\d{2}):(\d{2}).*?STATUS?\s*=\s*(\w+).*?#(\d+)""", RegexOption.IGNORE_CASE)
    val patternC = Regex("""\[(\d{2})\.(\d{2})\.(\d{4})\s+(\d{2}):(\d{2})\].*?(\w+).*?ID?\s*:\s*(\d+)""", RegexOption.IGNORE_CASE)

    val matchA = patternA.find(trimmed)
    val matchB = patternB.find(trimmed)
    val matchC = patternC.find(trimmed)
    val match = matchA ?: matchB ?: matchC
    if (match == null) return null
    val year: String
    val month: String
    val day: String
    val hour: String
    val minute: String
    val idStr: String
    val statusStr: String
    when {
        matchA != null -> {
            year = match.groupValues[1]
            month = match.groupValues[2]
            day = match.groupValues[3]
            hour = match.groupValues[4]
            minute = match.groupValues[5]
            idStr = match.groupValues[6]
            statusStr = match.groupValues[7]
        }
        matchB != null -> {
            day = match.groupValues[1]
            month = match.groupValues[2]
            year = match.groupValues[3]
            hour = match.groupValues[4]
            minute = match.groupValues[5]
            statusStr = match.groupValues[6]
            idStr = match.groupValues[7]
        }
        else -> {
            day = match.groupValues[1]
            month = match.groupValues[2]
            year = match.groupValues[3]
            hour = match.groupValues[4]
            minute = match.groupValues[5]
            statusStr = match.groupValues[6]
            idStr = match.groupValues[7]
        }
    }
    val status = when (statusStr.lowercase()) {
        "sent", "delivered" -> statusStr.lowercase()
        else -> return null
    }
    val dt = String.format("%04d-%02d-%02d %02d:%02d",
        year.toInt(), month.toInt(), day.toInt(), hour.toInt(), minute.toInt())
    return mapOf("dt" to dt, "id" to idStr.toInt(), "status" to status)
}

fun main1() {
    val logs = listOf(
        "2026-01-22 09:14 | ID:042 | STATUS:sent",
        "TS=22/01/2026-09:27; status=delivered; #042",
        "2026-01-22 09:10 | ID:043 | STATUS:sent",
        "2026-01-22 09:18 | ID:043 | STATUS:delivered",
        "TS=22/01/2026-09:05; status=sent; #044",
        "[22.01.2026 09:40] delivered (id:044)",
        "2026-01-22 09:20 | ID:045 | STATUS:sent",
        "[22.01.2026 09:33] delivered (id:045)",
        "   ts=22/01/2026-09:50; STATUS=Sent; #046   ",
        " [22.01.2026 10:05]   DELIVERED   (ID:046) "
    )
    val normalizedLogs = mutableListOf<Map<String, Any>>()
    val brokenLogs = mutableListOf<String>()
    for (log in logs) {
        val normalized = normalize(log)
        if (normalized != null) {
            normalizedLogs.add(normalized)
        } else {
            brokenLogs.add(log)
        }
    }
    println("normalized logs: ${normalizedLogs.size}")
    if (brokenLogs.isNotEmpty()) {
        println("broken logs: ${brokenLogs.size}")
    }
    val groups = mutableMapOf<Int, MutableList<Pair<String, String>>>()
    for (log in normalizedLogs) {
        val id = log["id"] as Int
        val status = log["status"] as String
        val dtStr = log["dt"] as String
        if (!groups.containsKey(id)) {
            groups[id] = mutableListOf()
        }
        groups[id]!!.add(status to dtStr)
    }

    val completeOrders = mutableListOf<Triple<Int, Long, String>>()
    val incompleteOrders = mutableListOf<Int>()
    val timeErrorOrders = mutableListOf<Int>()
    for ((id, events) in groups) {
        var sentTime: LocalDateTime? = null
        var deliveredTime: LocalDateTime? = null
        for ((status, dtStr) in events) {
            val parts = dtStr.split(" ")
            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")
            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt()
            val day = dateParts[2].toInt()
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            val dateTime = LocalDateTime.of(year, month, day, hour, minute)
            if (status == "sent") {
                sentTime = dateTime
            } else {
                deliveredTime = dateTime
            }
        }
        if (sentTime == null || deliveredTime == null) {
            incompleteOrders.add(id)
        } else if (deliveredTime.isBefore(sentTime)) {
            timeErrorOrders.add(id)
        } else {
            val minutes = ChronoUnit.MINUTES.between(sentTime, deliveredTime)
            completeOrders.add(Triple(id, minutes, "OK"))
        }
    }
    val sortedOrders = completeOrders.sortedByDescending { it.second }
    println("all IDs")
    for ((id, minutes, _) in sortedOrders) {
        println("ID: $id, Time: $minutes minutes")
    }
    if (sortedOrders.isNotEmpty()) {
        val (longestId, longestMinutes, _) = sortedOrders[0]
        println("\nlongest delivery: ID $longestId ($longestMinutes minutes)")
    }
    val violators = sortedOrders.filter { it.second > 20 }
    println("\nviolations (delivery time > 20 minutes):")
    if (violators.isEmpty()) {
        println("no violations found")
    } else {
        for ((id, minutes, _) in violators) {
            println("ID: $id - $minutes minutes")
        }
    }
    if (incompleteOrders.isNotEmpty()) {
        println("\nincomplete orders: ${incompleteOrders.joinToString(", ")}")
    }
    if (timeErrorOrders.isNotEmpty()) {
        println("\ntime errors: ${timeErrorOrders.joinToString(", ")}")
    }
}
