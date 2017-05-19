package org.ksug.forum

import org.ksug.forum.domain.PasswordHelper
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import swagger.Swagger

fun main(args: Array<String>) {
    SpringApplication.run(ForumBootApplication::class.java, *args)
}

@SpringBootApplication
@Import(Swagger::class)
class ForumBootApplication {
    
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        PasswordHelper.passwordEncoder = NoOpPasswordEncoder.getInstance()
        return PasswordHelper.passwordEncoder
    }

}