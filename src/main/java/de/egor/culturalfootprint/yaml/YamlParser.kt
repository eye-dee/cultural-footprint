package de.egor.culturalfootprint.yaml

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule

class YamlParser {

    private val mapper: ObjectMapper

    constructor(mapper: ObjectMapper = ObjectMapper(YAMLFactory())) {
        this.mapper = mapper
        mapper.registerModule(KotlinModule())
    }

    fun <T> fromResource(resource: String, clazz: Class<T>): T {
        return clazz.classLoader.getResourceAsStream(resource).use {
            mapper.readValue(it, clazz)
        }
    }

}