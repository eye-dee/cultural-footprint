package de.egor.culturalfootprint.record.collector

import de.egor.culturalfootprint.yaml.YamlParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import twitter4j.TwitterFactory
import java.time.LocalDateTime
import java.time.ZoneId

internal class TwitterCollectorTest {

    private val collector = buildCollector()

    @Test
    fun getRecords() {
        val records = collector.getRecords()

        assertThat(records).isNotEmpty().hasSize(100)
        val rawRecord = records[0]
        assertThat(rawRecord.data).isNotBlank()
        assertThat(rawRecord.source.tweetId).isNotNegative()
        assertThat(rawRecord.date).isBefore(LocalDateTime.now(ZoneId.of("UTC")))
    }

    private fun buildCollector(): TwitterCollector {
        val yamlParser = YamlParser()
        val twitterProperties = yamlParser.fromResource("twitter.yaml", TwitterProperties::class.java)
        val twitterConfiguration = twitterConfig(twitterProperties)
        val twitterFactory = TwitterFactory(twitterConfiguration)
        return TwitterCollector(twitterFactory.instance, TwitterCollectorProperties())
    }
}
