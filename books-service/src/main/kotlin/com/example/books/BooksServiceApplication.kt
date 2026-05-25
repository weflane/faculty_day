package com.example.books

import jakarta.persistence.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Entity
@Table(name = "books")
data class Book(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val author: String,

    @Column(name = "\"year\"")
    val year: Int? = null
)

data class BookRequest(
    val title: String,
    val author: String,
    val year: Int? = null
)
interface BookRepository : JpaRepository<Book, Long>

@Service
class BookService(private val repo: BookRepository) {

    fun getAll(): List<Book> = repo.findAll()

    fun getById(id: Long): Book =
        repo.findById(id).orElseThrow { NoSuchElementException("Book $id not found") }

    fun create(req: BookRequest): Book =
        repo.save(Book(title = req.title, author = req.author, year = req.year))

    fun update(id: Long, req: BookRequest): Book {
        val existing = getById(id)
        return repo.save(existing.copy(title = req.title, author = req.author, year = req.year))
    }

    fun delete(id: Long) {
        if (!repo.existsById(id)) throw NoSuchElementException("Book $id not found")
        repo.deleteById(id)
    }
}

@RestController
@RequestMapping("/api/books")
class BookController(private val service: BookService) {

    @GetMapping
    fun getAll(): List<Book> = service.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Book = service.getById(id)

    @PostMapping
    fun create(@RequestBody req: BookRequest): ResponseEntity<Book> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(req))

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody req: BookRequest): Book =
        service.update(id, req)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Not found")))
}


@SpringBootApplication
class BooksServiceApplication

fun main(args: Array<String>) {
    runApplication<BooksServiceApplication>(*args)
}
