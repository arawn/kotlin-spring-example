package org.ksug.forum

import org.ksug.forum.domain.PasswordHelper
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.AdviceMode
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.EnableLoadTimeWeaving
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.EnableTransactionManagement

fun main(args: Array<String>) {
    SpringApplication.run(ForumBootApplication::class.java, *args)
}

@SpringBootApplication
@EnableLoadTimeWeaving
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
open class ForumBootApplication {
    
    @Bean
    open fun passwordEncoder(): PasswordEncoder {
        PasswordHelper.passwordEncoder = BCryptPasswordEncoder()

        return PasswordHelper.passwordEncoder
    }

}