package org.ksug.forum.web

import org.ksug.forum.domain.module.ForumService
import org.ksug.forum.domain.module.TopicForm
import org.ksug.forum.web.data.PostData
import org.ksug.forum.web.data.TopicData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.beanvalidation.SpringValidatorAdapter
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = "/forum")
class ForumRestController @Autowired constructor(var forumService: ForumService, var validator: SpringValidatorAdapter) {

    @RequestMapping(value = "/categories", method = arrayOf(GET))
    fun categories() = forumService.categories()

    @RequestMapping(value = "/categories/{categoryId}/topics", method = arrayOf(GET))
    fun topics(@PathVariable categoryId: Long) : List<TopicData> {
        val category = forumService.loadCategory(categoryId)

        return forumService.loadTopics(category).map { TopicData.of(it) }
    }

    @RequestMapping(value = "/categories/{categoryId}/topics", method = arrayOf(POST), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun writeTopic(@PathVariable categoryId: Long, @RequestBody form: TopicForm, bindingResult: BindingResult) : TopicData {
        form.category = forumService.loadCategory(categoryId)

        validator.validateAndThrow(form, bindingResult, TopicForm.Write::class)

        return TopicData.of(forumService.write(form))
    }

    @RequestMapping(value = "/categories/{categoryId}/topics/{topicId}", method = arrayOf(PUT), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun editTopic(@PathVariable categoryId: Long, @PathVariable topicId: Long, @RequestBody form: TopicForm, bindingResult: BindingResult) : TopicData {
        form.id = topicId
        form.category = forumService.loadCategory(categoryId)

        validator.validateAndThrow(form, bindingResult, TopicForm.Edit::class)

        return TopicData.of(forumService.edit(form))
    }

    @RequestMapping(value = "/categories/{categoryId}/topics/{topicId}", method = arrayOf(DELETE), consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    fun deleteTopic(@PathVariable categoryId: Long, @PathVariable topicId: Long, @RequestBody command: DeleteCommand) {
        forumService.loadCategory(categoryId)
        val topic = forumService.loadTopic(topicId)

        forumService.delete(topic, command.password)
    }

    @RequestMapping(value = "/categories/{categoryId}/topics/{topicId}/posts", method = arrayOf(GET))
    fun posts(@PathVariable categoryId: Long, @PathVariable topicId: Long) : List<PostData> {
        forumService.loadCategory(categoryId)
        val topic = forumService.loadTopic(topicId)

        return forumService.loadPosts(topic).map { PostData.of(it) }
    }

    fun SpringValidatorAdapter.validateAndThrow(target: Any, bindingResult: BindingResult, vararg validationHints: Any) {
        validate(target, bindingResult, validationHints)

        if (bindingResult.hasErrors()) {
            throw BindException(bindingResult)
        }
    }

}

data class DeleteCommand(val password: String = "")