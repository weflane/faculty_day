import kotlin.concurrent.thread
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicInteger
import java.io.IOException
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.Mutex

class BankAccount(val id: String, var balance: Int) {
    fun transfer(to: BankAccount, amount: Int) {
        val firstLock = if (id < to.id) this else to
        val secondLock = if (id < to.id) to else this

        synchronized(firstLock) {
            synchronized(secondLock) {
                if (balance >= amount) {
                    balance -= amount
                    to.balance += amount
                }
            }
        }
    }
}

class SafeCounter {
    private val value = AtomicInteger(0)

    suspend fun increment() {
        delay(1)
        value.incrementAndGet()
    }

    fun getValue(): Int = value.get()

    suspend fun runConcurrentIncrements(
        coroutineCount: Int = 10,
        incrementsPerCoroutine: Int = 1000
    ): Int = coroutineScope {
        val jobs = List(coroutineCount) {
            launch(Dispatchers.Default) {
                repeat(incrementsPerCoroutine) {
                    increment()
                }
            }
        }
        jobs.joinAll()
        getValue()
    }
}

class VisibilityProblemSolved {
    @Volatile
    private var running = true

    fun startWriter(): Thread {
        return Thread {
            repeat(100) {
                Thread.sleep(10)
                Thread.yield()
            }
            running = false
        }.apply { start() }
    }

    fun startReader(): Thread {
        return Thread {
            println("reader: start")
            while (running) {
            }
            println("reader: end")
        }.apply { start() }
    }
}

suspend fun <T, R> parallelTransform(
    items: List<T>,
    transform: suspend (T) -> R
): List<R> = coroutineScope {
    items.map { item ->
        async {
            transform(item)
        }
    }.awaitAll()
}