package de.egor.culturalfootprint.record.config

import de.egor.culturalfootprint.record.collector.TwitterProperties
import de.egor.culturalfootprint.record.collector.twitterConfig
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import twitter4j.Twitter
import twitter4j.TwitterFactory

@Configuration
open class TwitterConfig {

    private val log = LoggerFactory.getLogger(TwitterConfig::class.java)

    @Bean
    open fun twitter(twitterFactory: TwitterFactory): Twitter {
        return twitterFactory.instance
    }

    @Bean
    open fun twitterFactory(twitterProperties: TwitterProperties): TwitterFactory {
        log.info("twitterProperties = $twitterProperties")
        val twitterConfiguration = twitterConfig(twitterProperties)
        return TwitterFactory(twitterConfiguration)
    }
}
