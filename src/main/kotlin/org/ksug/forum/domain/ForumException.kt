package org.ksug.forum.domain

import org.springframework.context.MessageSourceResolvable

abstract class ForumException : RuntimeException, MessageSourceResolvable {

    constructor(message: String) : super(message) { }
    constructor(message: String, cause: Throwable) : super(message, cause) { }

    override fun getArguments(): Array<out Any>? = arrayOf()
    override fun getDefaultMessage(): String?  = message

}

class TopicCreationException(message: String) : ForumException(message) {

    override fun getCodes(): Array<out String> = arrayOf("topicCreationException")

}

class BadPasswordException(val target: Target) : ForumException("비밀번호가 일치하지 않습니다.") {

    override fun getCodes(): Array<out String> = arrayOf("error.badPassword")
    override fun getArguments(): Array<out Any> = arrayOf(target)

    enum class Target {
        TOPIC, POST
    }

}