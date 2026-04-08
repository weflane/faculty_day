package com.example.library.repository

import com.example.library.entity.Genre
import org.springframework.data.domain.Example
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.query.FluentQuery
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.function.Function

// ============================================================================
// ЗАДАНИЕ 1: Создать интерфейс GenreRepository
// ============================================================================
// ИНСТРУКЦИЯ: Убери NoRepositoryBean ниже чтобы этот класс появился как репозиторий, а так же избавься от дефолтной реализации

interface GenreRepository : JpaRepository<Genre, Long> {
}

@Component
class GenreRepositoryImpl : GenreRepository