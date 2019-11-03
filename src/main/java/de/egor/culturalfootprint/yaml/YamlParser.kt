package de.egor.culturalfootprint.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths

@Component
class YamlParser(private val yamlObjectMapper: ObjectMapper) {

    init {
        yamlObjectMapper.registerModule(KotlinModule())
    }

    fun <T> fromResource(resource: String, clazz: Class<T>): T {
        val stream = clazz.classLoader.getResourceAsStream(resource)
                ?: Files.newInputStream(Paths.get(resource))
        return stream.use {
            yamlObjectMapper.readValue(it, clazz)
        }
    }

}

@Configuration
open class YamlParserConfiguration {

    @Bean
    open fun yamlObjectMapper(): ObjectMapper = ObjectMapper(YAMLFactory())
}
