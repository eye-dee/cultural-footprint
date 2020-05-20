package de.egor.culturalfootprint.record.collector

import de.egor.culturalfootprint.model.RawRecord
import de.egor.culturalfootprint.model.RecordSource
import de.egor.culturalfootprint.model.RecordSourceRepresentation
import de.egor.culturalfootprint.repository.RawRecordRepository
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import twitter4j.Paging
import twitter4j.Twitter
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import java.util.stream.Collectors.toList

@Service
class TwitterCollector(
    private val twitter: Twitter,
    private val properties: TwitterCollectorProperties,
    private val repository: RawRecordRepository
) {

    suspend fun getRecords(): List<RawRecord> {
        val paging = repository.getLatestRecordTweetId()
            ?.let { Paging().sinceId(it) }
            ?: Paging()
        paging.count = properties.requestedPageSize
        return twitter.getHomeTimeline(paging).stream()
            .filter { it.text != null }
            .map { tweet ->
                RawRecord(
                    id = UUID.randomUUID(),
                    date = tweet.createdAt?.toInstant()?.atZone(UTC_ZONE)?.toLocalDateTime() ?: LocalDateTime.now(
                        UTC_ZONE
                    ),
                    data = tweet.text,
                    source = RecordSource(
                        tweetId = tweet.id,
                        sourceRepresentation = RecordSourceRepresentation(
                            name = tweet.user.name,
                            username = tweet.user.screenName,
                            url = tweet.user.url
                        )
                    )
                )
            }
            .collect(toList())
    }

    companion object {
        private val UTC_ZONE = ZoneId.of("UTC")
    }
}

@ConfigurationProperties(prefix = "twitter")
data class TwitterProperties(
    var consumerKey: String?,
    var consumerSecret: String?,
    var accessToken: String?,
    var accessTokenSecret: String?
)

@ConfigurationProperties(prefix = "twitter.collector")
data class TwitterCollectorProperties(
    var requestedPageSize: Int = 300
)

fun twitterConfig(twitterProperties: TwitterProperties): Configuration =
    ConfigurationBuilder()
        .setDaemonEnabled(true)
        .setOAuthConsumerKey(twitterProperties.consumerKey)
        .setOAuthConsumerSecret(twitterProperties.consumerSecret)
        .setOAuthAccessToken(twitterProperties.accessToken)
        .setOAuthAccessTokenSecret(twitterProperties.accessTokenSecret)
        .build()
