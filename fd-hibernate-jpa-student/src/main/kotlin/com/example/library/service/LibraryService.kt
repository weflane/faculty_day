package com.example.library.service

import com.example.library.entity.Author
import com.example.library.entity.Book
import com.example.library.entity.Genre
import com.example.library.repository.AuthorRepository
import com.example.library.repository.BookRepository
import com.example.library.repository.GenreRepository
import jakarta.persistence.EntityNotFoundException
import org.hibernate.Hibernate
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LibraryService(
    private val authorRepository: AuthorRepository,
    private val bookRepository: BookRepository,
    private val genreRepository: GenreRepository
) {
    
    // ==================== АВТОРЫ ====================
    
    // ========================================================================
    // ЗАДАНИЕ 2: Исправить N+1 проблему
    // ========================================================================
    // ИНСТРУКЦИЯ: Замени authorRepository.findAll() на authorRepository.findAllWithBooksFetched() и правильно описать этот метод
    @Transactional(readOnly = true)
    fun getAllAuthorsWithBooksCountNPlus1(): List<Map<String, Any?>> {
        return authorRepository.findAllWithBooksFetched().map { author ->
            mapOf(
                "id" to author.id,
                "name" to author.name,
                "booksCount" to author.books.size,
                "bookTitles" to author.books.map { it.title }
            )
        }
    }
    @Transactional
    fun createBookWithRollback(title: String, isbn: String, authorId: Long, genreId: Long): Book {
        val book = createBook(title, isbn, authorId, genreId)

        if (title.contains("error", ignoreCase = true)) {
            throw RuntimeException("Simulated error - transaction should rollback")
        }

        return book
    }

    @Transactional
    fun createAuthor(name: String): Author {
        return authorRepository.save(Author(name = name))
    }

    @Transactional
    fun createBook(title: String, isbn: String, authorId: Long, genreId: Long): Book {
        val author = authorRepository.findById(authorId)
            .orElseThrow { EntityNotFoundException("Author not found with id: $authorId") }
        val genre = genreRepository.findById(genreId)
            .orElseThrow { EntityNotFoundException("Genre not found with id: $genreId") }

        return bookRepository.save(Book(title = title, isbn = isbn, author = author, genre = genre))
    }

    @Transactional(readOnly = true)
    fun getAllGenres(): List<Genre> {
        return genreRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun getBooksPage(page: Int = 0, size: Int = 20): Page<Book> {
        return bookRepository.findAll(PageRequest.of(page, size, Sort.by("title")))
    }

    @Transactional(readOnly = true)
    fun searchBooksByTitle(title: String): List<Map<String, Any?>> {
        return bookRepository.findByTitleContaining(title).map { book ->
            mapOf(
                "id" to book.id,
                "title" to book.title,
                "isbn" to book.isbn,
                "author" to book.author?.name,
                "genre" to book.genre?.name
            )
        }
    }

    @Transactional(readOnly = true)
    fun getAuthorWithBooksFetched(authorId: Long): Map<String, Any?>? {
        return authorRepository.findById(authorId)
            .map { author ->
                Hibernate.initialize(author.books)
                mapOf(
                    "id" to author.id,
                    "name" to author.name,
                    "books" to author.books.map { it.title }
                )
            }
            .orElse(null)
    }
}