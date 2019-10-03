package de.egor.culturalfootprint.collector

import twitter4j.Twitter
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.stream.Collectors.toList

class TwitterCollector(private val twitter: Twitter) {

    fun getRecords(): List<RawRecord> {
        return twitter.getHomeTimeline().stream()
                .filter { it.text != null }
                .map { tweet -> RawRecord(
                        date = tweet.createdAt?.toInstant()?.atZone(UTC_ZONE)?.toLocalDateTime() ?: LocalDateTime.now(UTC_ZONE),
                        data = tweet.text,
                        source = RecordSource(tweetId = tweet.id),
                        title = null
                ) }
                .collect(toList())
    }

    companion object {
        private val UTC_ZONE = ZoneId.of("UTC")
    }
}

data class TwitterProperties(val consumerKey: String, val consumerSecret: String, val accessToken: String, val accessTokenSecret: String)

fun twitterConfig(twitterProperties: TwitterProperties): Configuration {
    return ConfigurationBuilder()
            .setDaemonEnabled(true)
            .setOAuthConsumerKey(twitterProperties.consumerKey)
            .setOAuthConsumerSecret(twitterProperties.consumerSecret)
            .setOAuthAccessToken(twitterProperties.accessToken)
            .setOAuthAccessTokenSecret(twitterProperties.accessTokenSecret)
            .build()
}
