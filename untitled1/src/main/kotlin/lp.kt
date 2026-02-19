import java.util.concurrent.*
import java.math.BigInteger
import kotlinx.coroutines.*
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import java.io.IOException

fun main() {
    val a = CreateThreads
    val b = RaceCondition
    val c = RaceCondition2
    val d = RaceCondition3
    val e = ExecutorServiceExample
    val f = FutureFactorial
    val g = StructuredConcurency
    a.run()
    b.run()
    c.run()
    d.run()
    e.run()
    f.run()
    g.run(3)
    val h = WithContextIO
    h.run(listOf("file1.txt", "file2.txt", "file3.txt"))
    val i = ImageDownloader
    i.run(List(10) { "https://picsum.photos/200/300?random=${it+1}" }, "downloads")
}

object CreateThreads {
    fun run(): List<Thread> {
        val threadNames = listOf("Thread-A", "Thread-B", "Thread-C")
        val threads = threadNames.map { name ->
            Thread {
                repeat(5) {
                    println("hi from: $name")
                    Thread.sleep(500)
                }
            }.apply {
                this.name = name
            }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }
        return threads
    }
}

object RaceCondition {
    fun run(): Int {
        var counter = 0
        val threads = List(10) {
            Thread {
                repeat(1000) {
                    counter += 1
                }
            }.apply { start() }
        }
        threads.forEach { it.join() }
        println("final result: $counter")
        return counter
    }
}

object RaceCondition2 {
    fun run(): Int {
        val counter = AtomicInteger(0)
        val threads = (1..10).map {
            Thread {
                repeat(1000) {
                    counter.incrementAndGet()
                }
            }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }

        println("final result: $counter")
        return counter.get()
    }
}

object RaceCondition3 {
    fun run(): Int {
        var counter = 0
        val lock = Any()
        val threads = (1..10).map {
            Thread {
                repeat(1000) {
                    synchronized(lock) {
                        counter += 1
                    }
                }
            }
        }
        threads.forEach { it.start() }
        threads.forEach { it.join() }

        println("final result: $counter")
        return counter
    }
}


object Deadlock {
    fun run(): Int {
        val x = Any()
        val y = Any()

        val a = Thread {
            synchronized(x){
                Thread.sleep(100)
                synchronized(y){
                    Thread.sleep(100)
                }
            }

        }
        val b = Thread {
            synchronized(y){
                Thread.sleep(100)
                synchronized(x){
                    Thread.sleep(100)
                }
            }
        }
        a.start()
        b.start()
        a.join()
        b.join()
        return 0
    }
}


object ExecutorServiceExample {
    fun run(): List<String> {
        val res = mutableListOf<String>()
        val executor = Executors.newFixedThreadPool(4)
        val tasks = List(20) { taskNumber ->
            Runnable {
                val threadName = Thread.currentThread().name
                val r = "task $taskNumber -> thread $threadName"
                synchronized(res) {
                    res.add(r)
                }
                println(r)
                Thread.sleep(200)
            }
        }
        tasks.forEach { task ->
            executor.submit(task)
        }
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.MINUTES)
        return res
    }
}

object FutureFactorial {
    fun run(): Map<Int, BigInteger> {
        val executor = Executors.newFixedThreadPool(4)
        val f = mutableListOf<Future<Pair<Int, BigInteger>>>()
        for (i in 1..10) {
            val task = Callable<Pair<Int, BigInteger>> {
                i to factorial(i)
            }
            f.add(executor.submit(task))
        }
        val result = mutableMapOf<Int, BigInteger>()
        for (future in f) {
            val (number, factorial) = future.get()
            result[number] = factorial
        }
        executor.shutdown()
        return result
    }
    private fun factorial(n: Int): BigInteger {
        var result = BigInteger.ONE
        for (i in 1..n) {
            result = result.multiply(BigInteger.valueOf(i.toLong()))
        }
        return result
    }
}


object CoroutineLaunch {
    fun run(): List<String> = runBlocking {
        val results = mutableListOf<String>()
        repeat(3) { coroutineIndex ->
            launch {
                repeat(5) { iterationIndex ->
                    val message = "coroutine ${coroutineIndex + 1} iteration ${iterationIndex + 1}"
                    results.add(message)
                    println(message)
                    delay(500)
                }
            }
        }
        delay(3000)
        return@runBlocking results
    }
}

object AsyncAwait {
    fun run(): Long = runBlocking {
        val total = 1_000_000L
        val numParts = 4
        val chunkSize = total / numParts
        val d = List(numParts) { part ->
            async {
                val start = part * chunkSize + 1
                val end = if (part == numParts - 1) total
                else (part + 1) * chunkSize
                sumRange(start.toInt(), end.toInt())
            }
        }
        d.awaitAll().sum()
    }
    private fun sumRange(start: Int, end: Int): Long {
        var sum = 0L
        for (i in start..end) {
            sum += i
        }
        return sum
    }
}

object StructuredConcurency {
    fun run(failingCoroutineIndex: Int): Int = runBlocking {
        val completedJobs = mutableListOf<Int>()
        try {
            coroutineScope {
                List(5) { index ->
                    launch {
                        try {
                            delay(100L * (index + 1))
                            if (index == failingCoroutineIndex) {
                                throw IOException("$index -> error")
                            }

                            synchronized(completedJobs) {
                                completedJobs.add(index)
                            }
                            println("$index completed")

                        } catch (e: kotlinx.coroutines.CancellationException) {
                            println("$index canceled")
                            throw e
                        }
                    }
                }
            }
        } catch (e: IOException) {
            println(" ${e.message}")
        }
        completedJobs.size
    }
}


object WithContextIO {
    fun run(filePaths: List<String>): Map<String, String> = runBlocking {
        val d = filePaths.map { filePath ->
            async(Dispatchers.IO) {
                filePath to readFileContent(filePath)
            }
        }
        d.awaitAll().toMap()
    }
    private fun readFileContent(filePath: String): String {
        return try {
            File(filePath).readText()
        } catch (e: Exception) {
            "error ${e.message}"
        }
    }
}


data class DownloadResult(
    val index: Int,
    val success: Boolean,
    val error: String? = null
)

data class DownloadStats(
    val totalTime: Long,
    val successful: Int,
    val failed: Int,
    val failedIndices: List<Int>
)

object ImageDownloader {
    fun run(urls: List<String>, outputDir: String): DownloadStats = runBlocking {
        val outputDirFile = File(outputDir).also { it.mkdirs() }
        val startTime = System.currentTimeMillis()
        var completed = 0
        val mutex = Mutex()

        val results = urls.mapIndexed { index, url ->
            async(Dispatchers.IO) {
                val result = downloadImage(index + 1, url, outputDirFile)
                mutex.withLock {
                    completed++
                    val detail = result.error?.let { " (error: $it)" } ?: ""
                    println("Downloaded $completed/${urls.size} -> image ${result.index}$detail")
                }
                result
            }
        }.awaitAll()

        val totalTime = System.currentTimeMillis() - startTime
        val successful = results.count { it.success }
        val failed = results.count { !it.success }
        val failedIndices = results.filter { !it.success }.map { it.index }

        DownloadStats(totalTime, successful, failed, failedIndices)
    }

    private suspend fun downloadImage(index: Int, urlString: String, outputDir: File): DownloadResult =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = 10_000
                    readTimeout = 10_000
                    instanceFollowRedirects = true
                }
                connection.connect()

                if (connection.responseCode !in 200..299) {
                    return@withContext DownloadResult(index, false)
                }

                val file = File(outputDir, "image_${index}.jpg")
                connection.inputStream.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                connection.disconnect()
                DownloadResult(index, true)
            } catch (e: Exception) {
                DownloadResult(index, false, e.message)
            }
        }
}