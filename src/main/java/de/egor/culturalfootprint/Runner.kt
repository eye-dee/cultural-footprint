package de.egor.culturalfootprint

import de.egor.culturalfootprint.record.collector.TwitterCollectorProperties
import de.egor.culturalfootprint.record.repository.RawRecordRepositoryProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(
    RawRecordRepositoryProperties::class, TwitterCollectorProperties::class
)
@SpringBootApplication
open class Runner {

//    private val log = LoggerFactory.getLogger(Runner::class.java)

}

fun main(args: Array<String>) {
    println("Running")
    runApplication<Runner>(*args)
}

