package de.egor.culturalfootprint.record.config

import de.egor.culturalfootprint.record.collector.TwitterProperties
import de.egor.culturalfootprint.record.collector.twitterConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import twitter4j.Twitter
import twitter4j.TwitterFactory

@Configuration
open class TwitterConfig {

    @Bean
    open fun twitter(twitterFactory: TwitterFactory): Twitter = twitterFactory.instance

    @Bean
    open fun twitterFactory(twitterProperties: TwitterProperties): TwitterFactory =
        TwitterFactory(twitterConfig(twitterProperties))
}
