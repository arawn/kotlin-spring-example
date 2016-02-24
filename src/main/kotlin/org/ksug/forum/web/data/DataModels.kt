package org.ksug.forum.web.data

import org.ksug.forum.domain.Post
import org.ksug.forum.domain.Topic
import java.util.*

data class TopicData( val id: Long?
                    , val title: String
                    , val author: String
                    , val createdAt: Date
                    , val updatedAt: Date) {

    companion object {
        fun of(source: Topic) = TopicData(source.id, source.title, source.author,  source.createdAt, source.updatedAt)
    }

}

class PostData( val id: Long?
              , val text: String
              , val author: String
              , val createdAt: Date
              , val updatedAt: Date) {

    companion object {
        fun of(source: Post) = PostData(source.id, source.text, source.author,  source.createdAt, source.updatedAt)
    }

}