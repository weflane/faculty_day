import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.*
import java.security.SecureRandom
import java.security.cert.X509Certificate

// Задача 2. REST — полный CRUD

val BASE_URL = "https://jsonplaceholder.typicode.com/posts"

// TODO 1: Универсальная функция отправки запросов
fun sendRequest(urlStr: String, method: String, body: String? = null): Pair<Int, String> {
    val connection = URL(urlStr).openConnection() as HttpURLConnection
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
        connection.errorStream?.bufferedReader()?.readText() ?: "No error body"

    connection.disconnect()
    return Pair(code, responseBody)
}

// TODO 2a: GET /posts — получить все посты
fun getPosts(): String {
    val (code, body) = sendRequest(BASE_URL, "GET")
    return "Статус: $code\n$body"
}

// TODO 2b: GET /posts/{id} — получить пост по ID
fun getPost(id: Int): String {
    val (code, body) = sendRequest("$BASE_URL/$id", "GET")
    return "Статус: $code\n$body"
}

// TODO 2c: POST /posts — создать новый пост
fun createPost(json: String): String {
    val (code, body) = sendRequest(BASE_URL, "POST", json)
    return "Статус: $code\n$body"
}

// TODO 2d: PUT /posts/{id} — полностью обновить пост
fun updatePost(id: Int, json: String): String {
    val (code, body) = sendRequest("$BASE_URL/$id", "PUT", json)
    return "Статус: $code\n$body"
}

// TODO 2e: DELETE /posts/{id} — удалить пост, вернуть статус-код
fun deletePost(id: Int): Int {
    val (code, _) = sendRequest("$BASE_URL/$id", "DELETE")
    return code
}

fun main() {
    disableSslVerification()

    // TODO 3: вызвать каждую функцию и вывести результат

    val allPosts = getPosts()
    println(allPosts.take(300) + "...")

    println(getPost(1))

    val newPost = """{"title": "Мой пост", "body": "Текст поста", "userId": 1}"""
    println(createPost(newPost))

    val updatedPost = """{"id": 1, "title": "Обновлённый заголовок", "body": "Новый текст", "userId": 1}"""
    println(updatePost(1, updatedPost))

    val deleteCode = deletePost(1)
    println("status: $deleteCode")
}