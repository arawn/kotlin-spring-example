package org.ksug.forum.domain.repository

import org.ksug.forum.domain.Category
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<Category, Long> {

    fun findByName(name: String): Category?

}