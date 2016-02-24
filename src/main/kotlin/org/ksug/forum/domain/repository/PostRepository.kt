package org.ksug.forum.domain.repository

import org.ksug.forum.domain.Post
import org.ksug.forum.domain.Topic
import org.springframework.data.repository.PagingAndSortingRepository

interface PostRepository : PagingAndSortingRepository<Post, Long> {

    fun findByTopic(topic: Topic): List<Post>

}