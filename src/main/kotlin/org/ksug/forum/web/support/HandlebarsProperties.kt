package org.ksug.forum.web.support

import com.github.jknack.handlebars.ValueResolver
import com.github.jknack.handlebars.context.FieldValueResolver
import com.github.jknack.handlebars.context.JavaBeanValueResolver
import com.github.jknack.handlebars.context.MapValueResolver
import com.github.jknack.handlebars.context.MethodValueResolver
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver
import org.springframework.boot.autoconfigure.template.AbstractTemplateViewResolverProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import java.util.*

@ConfigurationProperties(prefix = "handlebars", ignoreUnknownFields = true)
class HandlebarsProperties() : AbstractTemplateViewResolverProperties("classpath:templates/", ".hbs") {

    var registerMessageHelper = true
    var failOnMissingFile = false

    @NestedConfigurationProperty
    var valueResolver = ValueResolverProperties()

    override fun applyToViewResolver(viewResolver: Any) {
        super.applyToViewResolver(viewResolver)

        if (viewResolver is HandlebarsViewResolver) {
            viewResolver.setRegisterMessageHelper(registerMessageHelper)
            viewResolver.setFailOnMissingFile(failOnMissingFile)

            val valueResolvers = buildValueResolvers()
            if (!valueResolvers.isEmpty()) {
                viewResolver.setValueResolvers(*valueResolvers.toTypedArray())
            }
        } else {
            throw IllegalArgumentException("ViewResolver is not an instance of HandlebarsViewResolver : $viewResolver")
        }
    }



    private fun buildValueResolvers(): List<ValueResolver> {
        val valueResolvers = ArrayList<ValueResolver>()

        if (valueResolver.javaBean) {
            valueResolvers.add(JavaBeanValueResolver.INSTANCE)
        }
        if (valueResolver.map) {
            valueResolvers.add(MapValueResolver.INSTANCE)
        }
        if (valueResolver.method) {
            valueResolvers.add(MethodValueResolver.INSTANCE)
        }
        if (valueResolver.field) {
            valueResolvers.add(FieldValueResolver.INSTANCE)
        }

        return valueResolvers
    }


    data class ValueResolverProperties(var javaBean: Boolean = true
                                     , var map: Boolean = true
                                     , var method: Boolean = false
                                     , var field: Boolean = false)

}