package de.egor.culturalfootprint

import de.egor.culturalfootprint.collector.TwitterCollector
import de.egor.culturalfootprint.collector.TwitterProperties
import de.egor.culturalfootprint.yaml.YamlParser
import twitter4j.TwitterFactory
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder

object Runner {

    @JvmStatic
    fun main(args: Array<String>) {
        println("Running")
        val yamlParser = YamlParser()
        val twitterProperties = yamlParser.fromResource("twitter.yaml", TwitterProperties::class.java)
        val twitterConfiguration = twitterConfig(twitterProperties)
        val twitterFactory = TwitterFactory(twitterConfiguration)
        val twitterCollector = TwitterCollector(twitterFactory.instance)
    }

    private fun twitterConfig(twitterProperties: TwitterProperties): Configuration? {
        return ConfigurationBuilder()
                .setDaemonEnabled(true)
                .setOAuthConsumerKey(twitterProperties.consumerKey)
                .setOAuthConsumerSecret(twitterProperties.consumerSecret)
                .setOAuthAccessToken(twitterProperties.accessToken)
                .setOAuthAccessTokenSecret(twitterProperties.accessTokenSecret)
                .build()
    }
}
