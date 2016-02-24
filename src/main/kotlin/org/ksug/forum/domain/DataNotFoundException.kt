package org.ksug.forum.domain

import org.springframework.context.MessageSourceResolvable

abstract class DataNotFoundException : RuntimeException, MessageSourceResolvable {

    constructor(message: String) : super(message) { }
    constructor(message: String, cause: Throwable) : super(message, cause) { }

    override fun getArguments(): Array<out Any>? = arrayOf()
    override fun getDefaultMessage(): String?  = message

}

class CategoryNotFoundException(val categoryId: Long) : DataNotFoundException("카테고리(id: $categoryId)를 찾을 수 없습니다.") {

    override fun getCodes(): Array<out String> = arrayOf("error.categoryNotFound")
    override fun getArguments(): Array<out Any> = arrayOf(categoryId)

}

class TopicNotFoundException(val topicId: Long) : DataNotFoundException("$topicId 번 주제를 찾을 수 없습니다.") {

    override fun getCodes(): Array<out String> = arrayOf("error.topicNotFound")
    override fun getArguments(): Array<out Any> = arrayOf(topicId)

}