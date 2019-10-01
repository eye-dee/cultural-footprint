package de.egor.culturalfootprint.yaml

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class YamlParserTest {

    @Test
    fun parse() {
        val parser = YamlParser()

        val parsedObject = parser.fromResource("parsing.yaml", ParsingClass1::class.java)

        Assertions.assertThat(parsedObject.key1).isEqualTo("value1")
        Assertions.assertThat(parsedObject.key2.key3).isEqualTo("value2")
    }

}

data class ParsingClass1 constructor(val key1: String, val key2: ParsingClass2)

data class ParsingClass2 constructor(val key3: String)
