-- ============================================================================
-- ЗАДАНИЕ ФИНАЛ: Создать таблицу readers (упрощенная версия)
-- ============================================================================

CREATE TABLE readers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE reader_books (
    reader_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    PRIMARY KEY (reader_id, book_id),
    FOREIGN KEY (reader_id) REFERENCES readers(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);