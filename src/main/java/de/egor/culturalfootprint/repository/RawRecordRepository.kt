package de.egor.culturalfootprint.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.egor.culturalfootprint.model.RawRecord
import de.egor.culturalfootprint.repository.MongoUtils.D
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate
import org.litote.kmongo.eq
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Repository
import java.util.UUID

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

    suspend fun getLatestRecordTweetId(): Long? =
        collection.aggregate<MaxTweetIdAggregationResult>(
            """
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
]""".trimIndent()
        ).first()?.maxTweetId

    suspend fun save(records: List<RawRecord>) {
        collection.insertMany(records)
    }

    suspend fun findAllByClusterId(clusterId: UUID): List<RawRecord> =
        collection.find(RawRecord::cluster eq clusterId)
            .toList()

}

object MongoUtils {
    const val D = "$"
}

internal data class MaxTweetIdAggregationResult(val maxTweetId: Long?)

@ConfigurationProperties(prefix = "raw.record")
data class RawRecordRepositoryProperties(
    var collection: String = "RawRecords"
)
