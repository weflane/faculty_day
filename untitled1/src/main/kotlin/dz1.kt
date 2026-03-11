import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.Base64
import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

//3

fun main() {
    val encoder = Base64.getUrlEncoder().withoutPadding()
    val decoder = Base64.getUrlDecoder()
    val header = """{"alg":"HS256","typ":"JWT"}"""
    val payload = """{"sub":"1","name":"Ivan Petrov","role":"student","iat":1234567890}"""
    val fakeSignature = "dummysignature"
    val encodedHeader = encoder.encodeToString(header.toByteArray())
    val encodedPayload = encoder.encodeToString(payload.toByteArray())
    val encodedSignature = encoder.encodeToString(fakeSignature.toByteArray())
    val token = "$encodedHeader.$encodedPayload.$encodedSignature"
    println("header (original): $header")
    println("header (encoded): $encodedHeader")
    println("payload (original): $payload")
    println("payload (encoded): $encodedPayload")
    println("signature (encoded): $encodedSignature")
    println("full JWT Token: $token")
    val parts = token.split(".")
    val decodedHeader = String(decoder.decode(parts[0]))
    val decodedPayload = String(decoder.decode(parts[1]))
    val decodedSignature = String(decoder.decode(parts[2]))
    println("decoded header: $decodedHeader")
    println("decoded payload: $decodedPayload")
    println("decoded signature: $decodedSignature")

    val (codeWithToken, responseWithToken) = sendBearerRequest("https://httpbin.org/bearer", token)
    println("status code: $codeWithToken")
    println("response: $responseWithToken")

    val (code4, body4) = sendBearerRequest("https://httpbin.org/bearer", null)
    println("status: $code4\nbody: $body4")

    val fakePayload = """{"sub":"1","name":"Ivan Petrov","role":"admin","iat":1234567890}"""
    val tamperedToken = "${parts[0]}.${encoder.encodeToString(fakePayload.toByteArray())}.${parts[2]}"
    println("$tamperedToken")

    val (code5, body5) = sendBearerRequest("https://httpbin.org/bearer", tamperedToken)
    println("statud: $code5\nbody: $body5")

    notesClient()
}

fun sendBearerRequest(urlString: String, token: String?): Pair<Int, String> {
    val url = URI(urlString).toURL()
    val connection = url.openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.setRequestProperty("Accept", "application/json")
    if (token != null) {
        connection.setRequestProperty("Authorization", "Bearer $token")
    }
    val code = connection.responseCode
    val responseBody = if (code in 200..299) {
        connection.inputStream.bufferedReader().readText()
    } else {
        connection.errorStream?.bufferedReader()?.readText() ?: "no body"
    }
    connection.disconnect()
    return Pair(code, responseBody)
}

// task 6

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
        connection.errorStream?.bufferedReader()?.readText() ?: "no body"

    connection.disconnect()
    return Pair(code, responseBody)
}

fun notesClient() {
    val (a1, b1) = request(BASE, "GET")
    println("status $a1\n$b1")
    val newNote = """{"title":"Домашка","content":"Сделать задание по сетям","tag":"учёба"}"""
    val (a2, b2) = request(BASE, "POST", newNote)
    println("status: $a2\n$b2")
    val (a3, b3) = request("$BASE/1", "GET")
    println("status $a3\n$b3")
    val updated = """{"title":"Покупки (обновлено)","content":"Молоко, хлеб, яйца, сыр","tag":"личное"}"""
    val (a4, b4) = request("$BASE/1", "PUT", updated)
    println("status: $a4\n$b4")

    val tag = java.net.URLEncoder.encode("учёба", "UTF-8")
    val (a5, b5) = request("$BASE?tag=$tag", "GET")
    println("status: $a5\n$b5")

    val (a6, b6) = request("$BASE/1", "DELETE")
    println("status: $a6\n$b6")

    val (a7, b7) = request("$BASE/999", "GET")
    println("statjsСтатус: $a7\n$b7")

    val (a8, b8) = request(BASE, "GET")
    println("status: $a8\n$b8")
}