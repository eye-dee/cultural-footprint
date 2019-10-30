package de.egor.culturalfootprint

import de.egor.culturalfootprint.record.RawRecordService
import de.egor.culturalfootprint.record.collector.TwitterCollector
import de.egor.culturalfootprint.record.collector.TwitterCollectorProperties
import de.egor.culturalfootprint.record.collector.TwitterProperties
import de.egor.culturalfootprint.record.collector.twitterConfig
import de.egor.culturalfootprint.record.repository.RawRecordRepository
import de.egor.culturalfootprint.record.repository.RawRecordRepositoryProperties
import de.egor.culturalfootprint.yaml.YamlParser
import org.slf4j.LoggerFactory
import twitter4j.TwitterFactory

object Runner {

    private val log = LoggerFactory.getLogger(Runner::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        println("Running")
        val yamlParser = YamlParser()
        val twitterYamlPath = System.getenv("TWITTER_YAML_PATH") ?: "twitter.yaml"
        log.info("Twitter properties will be written from {}", twitterYamlPath)
        val twitterProperties = yamlParser.fromResource(twitterYamlPath, TwitterProperties::class.java)
        val twitterConfiguration = twitterConfig(twitterProperties)
        val twitterFactory = TwitterFactory(twitterConfiguration)
        val repositoryProperties = RawRecordRepositoryProperties(outputFile = """data/output.data""")
        val recordRepository = RawRecordRepository(repositoryProperties)
        val twitterCollector = TwitterCollector(twitterFactory.instance, TwitterCollectorProperties(), recordRepository)
        val rawRecordService = RawRecordService(twitterCollector, recordRepository)
        rawRecordService.startPolling()
    }

}
