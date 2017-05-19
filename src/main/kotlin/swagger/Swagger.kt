package swagger

import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.ConfigurableWebApplicationContext
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import org.springframework.web.servlet.DispatcherServlet

@Configuration
class Swagger {

    @Bean
    fun swagger(): ServletRegistrationBean {
        val registrationBean = ServletRegistrationBean(SwaggerDispatcherServlet(), "/swagger/*")
        registrationBean.setName("swagger")

        return registrationBean
    }


    internal inner class SwaggerDispatcherServlet : DispatcherServlet() {
        init {
            val webApplicationContext = AnnotationConfigWebApplicationContext()
            webApplicationContext.register(SwaggerUIConfig::class.java)

            setApplicationContext(webApplicationContext)
        }

        override fun initWebApplicationContext(): WebApplicationContext {
            val wac = webApplicationContext
            if (wac is ConfigurableWebApplicationContext) {
                if (!wac.isActive) {
                    configureAndRefreshWebApplicationContext(wac)
                }
            }

            return wac
        }

    }

}
