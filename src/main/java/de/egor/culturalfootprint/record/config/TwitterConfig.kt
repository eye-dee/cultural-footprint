package de.egor.culturalfootprint.record.config

import de.egor.culturalfootprint.record.collector.TwitterProperties
import de.egor.culturalfootprint.record.collector.twitterConfig
import de.egor.culturalfootprint.yaml.YamlParser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import twitter4j.Twitter
import twitter4j.TwitterFactory

@Configuration
open class TwitterConfig {

    @Bean
    open fun twitter(twitterFactory: TwitterFactory): Twitter {
        return twitterFactory.instance
    }

    @Bean
    open fun twitterFactory(yamlParser: YamlParser): TwitterFactory {
        val twitterYamlPath = System.getenv("TWITTER_YAML_PATH") ?: "twitter.yaml"
        val twitterProperties = yamlParser.fromResource(twitterYamlPath, TwitterProperties::class.java)
        val twitterConfiguration = twitterConfig(twitterProperties)
        return TwitterFactory(twitterConfiguration)
    }
}
