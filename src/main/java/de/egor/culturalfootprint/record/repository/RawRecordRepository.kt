package de.egor.culturalfootprint.record.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.egor.culturalfootprint.record.collector.RawRecord
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RawRecordRepository(
        properties: RawRecordRepositoryProperties,
        db: CoroutineDatabase
) {

    private val mapper: ObjectMapper = ObjectMapper()
    private val collection: CoroutineCollection<RawRecord> = db.getCollection(properties.collection)

    init {
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.registerModule(KotlinModule())
    }

    fun getLatestRecordTweetId(): Optional<Long> {

        return runBlocking {
            Optional.ofNullable(collection.aggregate<MaxTweetIdAggregationResult>("""
[
    {
        ${D}match: {
            "source.tweetId": { "${D}ne": null }
        }},
    {   
        ${D}group: {
            _id: "test",
            maxTweetId: {
                ${D}max: "${D}source.tweetId"
            }
        }
    }
]""".trimIndent()).first()?.maxTweetId)
        }
    }

    fun save(records: List<RawRecord>) {
        runBlocking {
            collection.insertMany(records)
        }
    }

    companion object {
        private const val D = "$"
    }
}

internal data class MaxTweetIdAggregationResult(val maxTweetId: Long?)

@ConfigurationProperties(prefix = "raw.record")
data class RawRecordRepositoryProperties(
        var collection: String = "RawRecords"
)
