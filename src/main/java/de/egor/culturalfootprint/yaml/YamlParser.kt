package de.egor.culturalfootprint.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.nio.file.Files
import java.nio.file.Paths

class YamlParser {

    private val mapper: ObjectMapper

    constructor(mapper: ObjectMapper = ObjectMapper(YAMLFactory())) {
        this.mapper = mapper
        mapper.registerModule(KotlinModule())
    }

    fun <T> fromResource(resource: String, clazz: Class<T>): T {
        val stream = clazz.classLoader.getResourceAsStream(resource)
                ?: Files.newInputStream(Paths.get(resource))
        return stream.use {
            mapper.readValue(it, clazz)
        }
    }

}
