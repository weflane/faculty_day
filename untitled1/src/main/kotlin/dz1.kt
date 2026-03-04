import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.Base64
import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

// ===========================================
// Задача 3. JWT — авторизация
// ===========================================
// Цель: понять структуру JWT, собрать и декодировать токен, отправить запрос с Bearer-авторизацией.
// API: https://httpbin.org/bearer (возвращает 200 если есть Bearer, 401 если нет)
//
// TODO 1: Собрать JWT из трёх частей (header, payload, signature) в Base64URL
// TODO 2: Декодировать JWT обратно — вывести header и payload как JSON
// TODO 3: Отправить GET https://httpbin.org/bearer с заголовком Authorization: Bearer <token>
// TODO 4: Отправить тот же запрос БЕЗ токена — убедиться, что вернулся 401
// TODO 5: Подменить payload (role: student → admin), объяснить почему сервер отвергнет
//
// Подсказки:
//   Base64.getUrlEncoder().withoutPadding().encodeToString(bytes) — кодирование
//   Base64.getUrlDecoder().decode(string)                        — декодирование
//   JWT = base64(header) + "." + base64(payload) + "." + base64(signature)
//
// Вопросы после выполнения:
//   - Из каких 3 частей состоит JWT?
//   - Можно ли подменить payload и использовать токен? Почему нет?
//   - Что такое access token и refresh token?

fun main() {
    //disableSslVerification()
    val encoder = Base64.getUrlEncoder().withoutPadding()
    val decoder = Base64.getUrlDecoder()

    // TODO 1: Собрать JWT
    println("=== Сборка JWT ===")
    val header = """{"alg":"HS256","typ":"JWT"}"""
    val payload = """{"sub":"1","name":"Ivan Petrov","role":"student","iat":1234567890}"""
    val fakeSignature = "dummysignature"
    // Закодировать каждую часть в Base64URL и склеить через "."
    val token = "" // TODO: собрать токен
    val encodedHeader = encoder.encodeToString(header.toByteArray())
    val encodedPayload = encoder.encodeToString(payload.toByteArray())
    val encodedSignature = encoder.encodeToString(fakeSignature.toByteArray())
    val token1 = "$encodedHeader.$encodedPayload.$encodedSignature"
    println("Header (original): $header")
    println("Header (encoded): $encodedHeader")
    println("Payload (original): $payload")
    println("Payload (encoded): $encodedPayload")
    println("Signature (encoded): $encodedSignature")
    println("Full JWT Token: $token1")


    // TODO 2: Декодировать JWT
    println("\n=== Декодирование JWT ===")
    // Разделить token по ".", декодировать header и payload, вывести
    val parts = token.split(".")
    val decodedHeader = String(decoder.decode(parts[0]))
    val decodedPayload = String(decoder.decode(parts[1]))
    val decodedSignature = String(decoder.decode(parts[2]))
    println("decoded header: $decodedHeader")
    println("decoded payload: $decodedPayload")
    println("decoded signature: $decodedSignature")

    // TODO 3: GET /bearer с токеном
    println("\n=== GET /bearer (с токеном) ===")
    // Отправить GET на https://httpbin.org/bearer
    // Добавить заголовок: connection.setRequestProperty("Authorization", "Bearer $token")
    // Вывести код и тело ответа
    val (codeWithToken, responseWithToken) = sendBearerRequest("https://httpbin.org/bearer", token)
    println("status code: $codeWithToken")
    println("response: $responseWithToken")

    // TODO 4: GET /bearer без токена
    println("\n=== GET /bearer (без токена) ===")
    val (code4, body4) = sendBearer("https://httpbin.org/bearer", null)
    println("status: $code4\nbody: $body4")

    // TODO 5: Подмена payload
    println("\n=== Подмена payload ===")
    val fakePayload = """{"sub":"1","name":"Ivan Petrov","role":"admin","iat":1234567890}"""
    val tamperedToken = "${parts[0]}.${encoder.encodeToString(fakePayload.toByteArray())}.${parts[2]}"
    println("$tamperedToken")
    val (code5, body5) = sendBearer("https://httpbin.org/bearer", tamperedToken)
    println("statud: $code5\nbody: $body5")
}

// Задача 6. Клиент для сервера заметок

const val BASE = "http://localhost:8080/api/notes"

fun request(url: String, method: String, body: String? = null): Pair<Int, String> {
    val connection = URI(url).toURL().openConnection() as HttpURLConnection
    connection.requestMethod = method
    connection.setRequestProperty("Accept", "application/json")

    if (body != null) {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        connection.outputStream.write(body.toByteArray())
    }

    val code = connection.responseCode
    val responseBody = if (code in 200..299)
        connection.inputStream.bufferedReader().readText()
    else
        connection.errorStream?.bufferedReader()?.readText() ?: "No body"

    connection.disconnect()
    return Pair(code, responseBody)
}

fun main1() {
    // Шаг 1: получить все заметки
    println("=== 1. GET /api/notes — все заметки ===")
    val (a1, b1) = request(BASE, "GET")
    println("Статус: $a1\n$b1")

    // Шаг 2: создать новую заметку
    println("\n=== 2. POST /api/notes — создать заметку ===")
    val newNote = """{"title":"Домашка","content":"Сделать задание по сетям","tag":"учёба"}"""
    val (a2, b2) = request(BASE, "POST", newNote)
    println("Статус: $a2\n$b2")

    // Шаг 3: получить заметку по id
    println("\n=== 3. GET /api/notes/1 — одна заметка ===")
    val (a3, b3) = request("$BASE/1", "GET")
    println("Статус: $a3\n$b3")

    // Шаг 4: обновить заметку
    println("\n=== 4. PUT /api/notes/1 — обновить заметку ===")
    val updated = """{"title":"Покупки (обновлено)","content":"Молоко, хлеб, яйца, сыр","tag":"личное"}"""
    val (a4, b4) = request("$BASE/1", "PUT", updated)
    println("Статус: $a4\n$b4")

    // Шаг 5: фильтр по тегу
    println("\n=== 5. GET /api/notes?tag=учёба — фильтр по тегу ===")
    val tag = java.net.URLEncoder.encode("учёба", "UTF-8")
    val (a5, b5) = request("$BASE?tag=$tag", "GET")
    println("Статус: $a5\n$b5")

    // Шаг 6: удалить заметку
    println("\n=== 6. DELETE /api/notes/1 — удалить заметку ===")
    val (a6, b6) = request("$BASE/1", "DELETE")
    println("Статус: $a6\n$b6")

    // Шаг 7: запросить несуществующую заметку (ожидаем 404)
    println("\n=== 7. GET /api/notes/999 — несуществующая заметка ===")
    val (a7, b7) = request("$BASE/999", "GET")
    println("Статус: $a7\n$b7")

    // Шаг 8: финальное состояние
    println("\n=== 8. GET /api/notes — финальное состояние ===")
    val (a8, b8) = request(BASE, "GET")
    println("Статус: $a8\n$b8")
}