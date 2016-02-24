package org.ksug.forum.domain.module

import org.hibernate.validator.constraints.NotEmpty
import org.ksug.forum.domain.*
import org.ksug.forum.domain.repository.CategoryRepository
import org.ksug.forum.domain.repository.PostRepository
import org.ksug.forum.domain.repository.TopicRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.validation.constraints.NotNull

@Service
@Transactional
class ForumService @Autowired constructor(var categoryRepository: CategoryRepository, var topicRepository: TopicRepository, var postRepository: PostRepository) {

    fun categories() = categoryRepository.findAll()
    fun loadCategory(categoryId: Long) = categoryRepository.findOne(categoryId) ?: throw CategoryNotFoundException(categoryId)


    fun loadTopics(category: Category) = topicRepository.findByCategory(category)
    fun loadTopic(topicId: Long) = topicRepository.findOne(topicId) ?: throw TopicNotFoundException(topicId)

    fun write(form: TopicForm) = topicRepository.save(form.create())
    fun edit(form: TopicForm) = form.update(loadTopic(form.id ?: Long.MIN_VALUE))
    fun delete(topic: Topic, rawPassword: String) = topicRepository.delete(topic.checkPassword(rawPassword))


    fun loadPosts(topic: Topic) = postRepository.findByTopic(topic)

}

class TopicForm {

    interface Write
    interface Edit

    @NotNull(groups = arrayOf(Edit::class))
    var id: Long? = null

    @NotEmpty(groups = arrayOf(Write::class, Edit::class))
    var title: String = ""

    @NotEmpty(groups = arrayOf(Write::class, Edit::class))
    var password: String = ""

    @NotEmpty(groups = arrayOf(Write::class))
    var author: String = ""

    @NotNull(groups = arrayOf(Write::class, Edit::class))
    var category: Category? = null


    internal fun create() = Topic(title, author, password, category ?: throw TopicCreationException("카테고리가 없습니다."))
    internal fun update(target: Topic): Topic {
        if (author.isEmpty()) {
            target.edit(title, password)
        } else {
            target.edit(title, author, password)
        }

        return target
    }

}