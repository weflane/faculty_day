package com.example.library.entity

import jakarta.persistence.*

// ============================================================================
// ЗАДАНИЕ ФИНАЛ: Создать сущность Reader
// ============================================================================
// ИНСТРУКЦИЯ:
// 1. Добавь @Entity и @Table
// 2. Добавь поля id, name, email

// TODO: Раскомментировать когда будешь делать финальное задание
@Entity
@Table(name = "readers")
data class Reader(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val name: String = "",

    @Column(name = "email", nullable = false, unique = true)
    val email: String = ""
)
