package org.ksug.forum.domain

import org.springframework.security.crypto.password.PasswordEncoder
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Password private constructor(@Column(name = "password") val encodedPassword: String) {

    fun matches(rawPassword: String) = PasswordHelper.matches(rawPassword, encodedPassword)


    companion object {

        fun wrap(rawPassword: CharSequence) = Password(PasswordHelper.encode(rawPassword))
        fun empty() = Password("")

    }

}

object PasswordHelper {

    lateinit var passwordEncoder: PasswordEncoder

    fun encode(rawPassword: CharSequence) = passwordEncoder.encode(rawPassword)
    fun matches(rawPassword: CharSequence, encodedPassword: String) = passwordEncoder.matches(rawPassword, encodedPassword)

}