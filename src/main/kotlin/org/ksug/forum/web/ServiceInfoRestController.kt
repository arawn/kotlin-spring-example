package org.ksug.forum.web

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ConfigurationProperties(prefix = "service.info")
class ServiceInfoRestController {

    var name: String = "unknown"
    var version: String = "1.0.0"

    @RequestMapping("/info")
    fun info() = hashMapOf("name" to name, "version" to version)

}