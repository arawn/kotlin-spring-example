package org.ksug.forum.domain

import org.ksug.forum.domain.BadPasswordException.Target.TOPIC
import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType.ALL
import javax.persistence.GenerationType.IDENTITY

@Entity
data class Topic(var title: String
               , var author: String
               , val createdAt: Date = Date()) {

    var password: Password = Password.empty()
    var updatedAt: Date = createdAt.clone() as Date


    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long? = null

    @ManyToOne(optional = false)
    private var category: Category? = null

    @OneToMany(mappedBy = "topic", cascade = arrayOf(ALL), orphanRemoval = true)
    private var posts: MutableList<Post> = ArrayList()


    constructor(title: String, author: String, rawPassword: String, category: Category) : this(title, author) {
        this.password = Password.wrap(rawPassword)
        this.category = category
    }

    fun checkPassword(rawPassword: String) =
            if (!password.matches(rawPassword)) throw BadPasswordException(TOPIC) else this

    fun edit(title: String, author: String, rawPassword: String) : Topic {
        checkPassword(rawPassword)

        this.title = title
        this.author = author
        this.updatedAt = Date()

        return this
    }

    fun edit(title: String, rawPassword: String): Topic = edit(title, this.author, rawPassword)

    fun writePost(text: String, author: String): Post {
        val newPost = Post(text, author, this)
        this.posts.add(newPost)
        this.updatedAt = Date()

        return newPost
    }


    // for hibernate
    private constructor() : this("", "")

}