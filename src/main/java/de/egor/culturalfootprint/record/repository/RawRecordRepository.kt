package de.egor.culturalfootprint.record.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import de.egor.culturalfootprint.record.collector.RawRecord
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class RawRecordRepository(
    private val properties: RawRecordRepositoryProperties,
    private val db: CoroutineDatabase
) {

    private val mapper: ObjectMapper = ObjectMapper()
    private var collection: CoroutineCollection<RawRecord>? = null

    init {
        mapper.registerModule(JavaTimeModule())
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        mapper.registerModule(KotlinModule())
        runBlocking {
            collection = db.getCollection<RawRecord>(properties.collection)
        }
    }

    fun getLatestRecord(): Optional<RawRecord> {
        return Optional.empty()
    }

    fun save(records: List<RawRecord>) {
        runBlocking {
            collection?.insertMany(records)
        }
    }
}

@ConfigurationProperties(prefix = "raw.record")
data class RawRecordRepositoryProperties(
    var collection: String = "RawRecords"
)
