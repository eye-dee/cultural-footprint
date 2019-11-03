package de.egor.culturalfootprint.record.collector

import de.egor.culturalfootprint.record.repository.RawRecordRepository
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import twitter4j.Paging
import twitter4j.Twitter
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.stream.Collectors.toList

@Service
class TwitterCollector(
    private val twitter: Twitter,
    private val properties: TwitterCollectorProperties,
    private val repository: RawRecordRepository
) {

    fun getRecords(): List<RawRecord> {
        val paging = repository.getLatestRecord()
            .map {
                Paging()
                    .sinceId(it.source.tweetId)
            }
            .orElseGet { Paging() }
        paging.count = properties.requestedPageSize
        return twitter.getHomeTimeline(paging).stream()
            .filter { it.text != null }
            .map { tweet ->
                RawRecord(
                    date = tweet.createdAt?.toInstant()?.atZone(UTC_ZONE)?.toLocalDateTime() ?: LocalDateTime.now(
                        UTC_ZONE
                    ),
                    data = tweet.text,
                    source = RecordSource(tweetId = tweet.id)
                )
            }
            .collect(toList())
    }

    companion object {
        private val UTC_ZONE = ZoneId.of("UTC")
    }
}

data class TwitterProperties(
    val consumerKey: String,
    val consumerSecret: String,
    val accessToken: String,
    val accessTokenSecret: String
)

@ConfigurationProperties(prefix = "twitter")
data class TwitterCollectorProperties(
    var requestedPageSize: Int = 300
)

fun twitterConfig(twitterProperties: TwitterProperties): Configuration {
    return ConfigurationBuilder()
        .setDaemonEnabled(true)
        .setOAuthConsumerKey(twitterProperties.consumerKey)
        .setOAuthConsumerSecret(twitterProperties.consumerSecret)
        .setOAuthAccessToken(twitterProperties.accessToken)
        .setOAuthAccessTokenSecret(twitterProperties.accessTokenSecret)
        .build()
}
