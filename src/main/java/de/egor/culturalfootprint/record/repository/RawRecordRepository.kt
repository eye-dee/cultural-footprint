package de.egor.culturalfootprint.record.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import de.egor.culturalfootprint.record.collector.RawRecord
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.Optional
import javax.annotation.PostConstruct

@Repository
class RawRecordRepository(
    private val properties: RawRecordRepositoryProperties,
    private val mapper: ObjectMapper
) {

    @PostConstruct
    fun init() {
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.registerModule(KotlinModule())
        val outputFile = outputPath(properties)
        if (Files.notExists(outputFile)) {
            Files.createFile(outputFile)
        }
    }

    fun getLatestRecord(): Optional<RawRecord> {
        return Files.lines(outputPath(properties))
            .parallel()
            .filter { !it.isBlank() }
            .map { mapper.readValue<RawRecord>(it) }
            .max(Comparator.comparingLong<RawRecord> { it.source.tweetId })
    }

    fun save(records: List<RawRecord>) {
        Files.newOutputStream(outputPath(properties), StandardOpenOption.APPEND, StandardOpenOption.WRITE).use {
            for (record in records) {
                it.write(LINE_SEPARATOR)
                it.write(mapper.writeValueAsBytes(record))
            }
        }
    }

    private fun outputPath(properties: RawRecordRepositoryProperties) =
        Paths.get(properties.outputFile)

    companion object {
        private const val LINE_SEPARATOR = 0x0A
    }
}

@ConfigurationProperties(prefix = "raw.record")
data class RawRecordRepositoryProperties(
    var outputFile: String = "raw_records.data"
)
