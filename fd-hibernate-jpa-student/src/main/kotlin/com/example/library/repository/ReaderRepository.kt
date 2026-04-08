package com.example.library.repository

interface ReaderRepository : JpaRepository<Reader, Long> {
    fun findByEmail(email: String): Reader?
    fun existsByEmail(email: String): Boolean
    @Query("SELECT r FROM Reader r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    fun findByNameContainingIgnoreCase(@Param("name") name: String): List<Reader>
    fun deleteByEmail(email: String)
}