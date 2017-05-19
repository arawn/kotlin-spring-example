package org.ksug.forum.domain

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id
import javax.persistence.ManyToOne

@Entity
data class Post(var text: String
              , var author: String
              , val createdAt: Date = Date()) {

    var password: Password = Password.empty()
    var updatedAt: Date = createdAt.clone() as Date


    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null

    @ManyToOne(optional = false)
    private var topic: Topic? = null


    constructor(text: String, author: String, topic: Topic) : this(text, author) {
        this.topic = topic
    }

    fun checkPassword(rawPassword: String) =
            if (!password.matches(rawPassword)) throw BadPasswordException(BadPasswordException.Target.POST) else this

    fun edit(text: String, rawPassword: String): Post {
        checkPassword(rawPassword)

        this.text = text
        this.updatedAt = Date()

        return this
    }

}