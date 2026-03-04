import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

// Задача 1. HTTP-запросы через HttpURLConnection

fun sendGet(url: String): Pair<Int, String> {
    val c = URL(url).openConnection() as HttpURLConnection
    c.requestMethod = "GET"
    c.setRequestProperty("Accept", "application/json")
    val code = c.responseCode
    val body = if (code in 200..299)
        c.inputStream.bufferedReader().readText()
    else
        c.errorStream.bufferedReader().readText()
    c.disconnect()
    return Pair(code, body)
}

fun disableSslVerification() {
    val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })
    val sc = SSLContext.getInstance("SSL")
    sc.init(null, trustAll, SecureRandom())
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
}

fun sendPost(url: String, json: String): Pair<Int, String> {
    val c = URL(url).openConnection() as HttpURLConnection
    c.requestMethod = "POST"
    c.doOutput = true
    c.setRequestProperty("Content-Type", "application/json")
    c.setRequestProperty("Accept", "application/json")
    c.outputStream.write(json.toByteArray())
    val code = c.responseCode
    val body = if (code in 200..299)
        c.inputStream.bufferedReader().readText()
    else
        c.errorStream.bufferedReader().readText()
    c.disconnect()
    return Pair(code, body)
}

fun main() {
    disableSslVerification()

    // TODO 1: GET /posts/1
    println("=== GET /posts/1 ===")
    val (code1, body1) = sendGet("https://jsonplaceholder.typicode.com/posts/1")
    println("status: $code1")
    println("body: $body1")

    // TODO 2: POST /posts
    println("\n=== POST /posts ===")
    val json = """{"title": "Hello", "body": "World", "userId": 1}"""
    val (code2, body2) = sendPost("https://jsonplaceholder.typicode.com/posts", json)
    println("status: $code2")
    println("body: $body2")

    // TODO 3: GET /posts/9999 (несуществующий ресурс)
    println("\n=== GET /posts/9999 ===")
    val (code3, body3) = sendGet("https://jsonplaceholder.typicode.com/posts/9999")
    if (code3 in 200..299) {
        println("status: $code3")
        println("body: $body3")
    } else {
        println("error, $code3")
        println("body: $body3")
    }
}