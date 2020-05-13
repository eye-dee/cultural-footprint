package de.egor.culturalfootprint.client.telegram.converter

interface Converter<S, T> {

    fun convert(source: S): T
}
