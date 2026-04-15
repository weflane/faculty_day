import com.example.library.entity.Author
import com.example.library.entity.Book
import com.example.library.entity.Genre
import com.example.library.exception.EntityNotFoundException
import com.example.library.repository.AuthorRepository
import com.example.library.repository.BookRepository
import com.example.library.repository.GenreRepository
import com.example.library.repository.ReaderRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import java.util.Optional

/**
 * ШАБЛОН ЗАНЯТИЯ (без готового кода тестов).
 *
 * Как работать:
 * 1. Сними [@Disabled] с класса ниже, когда начнёшь писать код.
 * 2. Для каждого теста следуй блоку «ИНСТРУКЦИЯ» — шаг за шагом.
 *
 * Подключи в начале файла импорты по мере необходимости, например:
 * - io.mockk: mockk, every, verify, slot, capture, any, eq, match
 * - org.junit.jupiter.api.Assertions.* (assertEquals, assertThrows — для проверки **результата** сервиса)
 * - Матчеры **eq** / **any** / **match** используются внутри `every { }` и `verify { }` для **аргументов** мока
 * - сущности и репозитории из com.example.library.*
 * - java.util.Optional — для findById у Spring Data
 * - org.springframework.data.domain.* — для постраничности
 */

class LibraryServiceMockkTest {

    private val authorRepository: AuthorRepository = mockk()
    private val bookRepository: BookRepository = mockk()
    private val genreRepository: GenreRepository = mockk()
    private val readerRepository: ReaderRepository = mockk()
    private lateinit var service: LibraryService

    @BeforeEach
    fun setUp() {
        service = LibraryService(authorRepository, bookRepository, genreRepository, readerRepository)
    }

    @Test
    fun `createAuthor возвращает того же автора что вернул save`() {
        val unsavedAuthor = Author(name = "ko")
        val savedAuthor = Author(id = 1, name = "ko")

        every { authorRepository.save(unsavedAuthor) } returns savedAuthor

        val result = service.createAuthor("ko")

        assertEquals(savedAuthor.id, result.id)
        assertEquals(savedAuthor.name, result.name)
        verify(exactly = 1) { authorRepository.save(unsavedAuthor) }
    }

    @Test
    fun `getAllGenres возвращает список из genreRepository findAll`() {
        val genres = listOf(
            Genre(id = 1, name = "Fantasy"),
            Genre(id = 2, name = "Science Fiction")
        )
        every { genreRepository.findAll() } returns genres

        val result = service.getAllGenres()

        assertEquals(2, result.size)
        assertEquals("Fantasy", result[0].name)
        verify(exactly = 1) { genreRepository.findAll() }
    }

    @Test
    fun `createBook бросает EntityNotFoundException если автор не найден`() {
        val authorId = 99L
        every { authorRepository.findById(authorId) } returns Optional.empty()

        val exception = assertThrows(EntityNotFoundException::class.java) {
            service.createBook("Title", "ISBN123", authorId, 1L)
        }

        assertEquals("Author not found with id: $authorId", exception.message)
        verify(exactly = 1) { authorRepository.findById(authorId) }
        verify(exactly = 0) { bookRepository.save(any()) }
    }

    @Test
    fun `createBook передаёт в save книгу с нужным названием и ISBN slot ловит аргумент`() {
        val authorId = 1L
        val genreId = 2L
        val author = Author(id = authorId, name = "Tolkien")
        val genre = Genre(id = genreId, name = "Fantasy")

        every { authorRepository.findById(authorId) } returns Optional.of(author)
        every { genreRepository.findById(genreId) } returns Optional.of(genre)

        val bookSlot = slot<Book>()
        every { bookRepository.save(capture(bookSlot)) } answers { bookSlot.captured.copy(id = 100L) }

        val result = service.createBook("The Hobbit", "978-0547928227", authorId, genreId)

        assertEquals("The Hobbit", bookSlot.captured.title)
        assertEquals("978-0547928227", bookSlot.captured.isbn)
        assertEquals(author, bookSlot.captured.author)
        assertEquals(genre, bookSlot.captured.genre)
        assertEquals(100L, result.id)

        verify(exactly = 1) {
            bookRepository.save(match { it.title == "The Hobbit" && it.isbn == "978-0547928227" })
        }
    }

    @Test
    fun `getBooksPage делегирует в bookRepository findAll с постраничностью`() {
        val author = Author(id = 1, name = "J.R.R. Tolkien")
        val genre = Genre(id = 1, name = "Fantasy")
        val books = listOf(
            Book(id = 1, title = "The Fellowship of the Ring", isbn = "123", author = author, genre = genre)
        )
        val pageRequest = PageRequest.of(0, 20, Sort.by("title"))
        val page = PageImpl(books, pageRequest, 1)

        every { bookRepository.findAll(eq(pageRequest)) } returns page

        val result = service.getBooksPage(page = 0, size = 20)

        assertEquals(1, result.content.size)
        assertEquals("The Fellowship of the Ring", result.content[0].title)
        verify(exactly = 1) { bookRepository.findAll(eq(pageRequest)) }
    }
}