package org.ksug.forum.domain

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id

@Entity
data class Category( var name: String
                   , val createdAt: Date = Date()) {

    var updatedAt: Date = createdAt


    @Id @GeneratedValue(strategy = IDENTITY)
    val id: Long = 0


    fun rename(name: String) : Category {
        this.name = name
        this.updatedAt = Date()

        return this
    }

}