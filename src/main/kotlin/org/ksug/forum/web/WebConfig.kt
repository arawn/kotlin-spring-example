package org.ksug.forum.web

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletRequest

@Configuration
class WebConfig : WebMvcConfigurerAdapter() {

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.ignoreAcceptHeader(true)
                  .favorPathExtension(false)
                  .favorParameter(false)
                  .defaultContentType(MediaType.APPLICATION_JSON)
    }

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(object : HandlerMethodArgumentResolver {
            override fun supportsParameter(parameter: MethodParameter): Boolean =
                parameter.parameterType.isAssignableFrom(ServletRequestAttributes::class.java)

            override fun resolveArgument(parameter: MethodParameter, mavContainer: ModelAndViewContainer, webRequest: NativeWebRequest, dataBindFactory: WebDataBinderFactory): Any =
                ServletRequestAttributes(webRequest.getNativeRequest(HttpServletRequest::class.java))

        })
    }

    @Autowired
    fun setUpObjectMapper(mapper: ObjectMapper) {
        mapper.dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

}
