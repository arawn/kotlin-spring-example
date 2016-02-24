package org.ksug.forum.domain.repository

import org.ksug.forum.domain.Category
import org.ksug.forum.domain.Topic
import org.springframework.data.jpa.repository.JpaRepository

interface TopicRepository : JpaRepository<Topic, Long> {

    fun findByCategory(category: Category): List<Topic>

}