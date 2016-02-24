package org.ksug.forum.web.support

import org.ksug.forum.domain.DataNotFoundException
import org.ksug.forum.domain.ForumException
import org.ksug.forum.web.support.ErrorResponseEntity.Companion.badReqeust
import org.ksug.forum.web.support.ErrorResponseEntity.Companion.notFound
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.*

@ControllerAdvice
class ExceptionHandlers @Autowired constructor(var messageSource: MessageSource) {

    @ExceptionHandler(ForumException::class)
    fun forumException(exception: ForumException, locale: Locale) =
            badReqeust(messageSource.getMessage(exception, locale))

    @ExceptionHandler(DataNotFoundException::class)
    fun resourceNotFoundException(exception: DataNotFoundException, locale: Locale) =
            notFound(messageSource.getMessage(exception, locale))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException, locale: Locale) =
            badReqeust("입력값이 올바르지 않습니다.", mapBindingResult(exception.bindingResult, locale));

    @ExceptionHandler(BindException::class)
    fun bindException(exception: BindException, locale: Locale) =
            badReqeust("입력값이 올바르지 않습니다.", mapBindingResult(exception.bindingResult, locale));

    fun mapBindingResult(bindingResult: BindingResult, locale: Locale) =
            bindingResult.allErrors.map { messageSource.getMessage(it, locale) }

}