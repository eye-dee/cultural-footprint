package de.egor.culturalfootprint.record.repository

import com.fasterxml.jackson.databind.ObjectMapper
import de.egor.culturalfootprint.record.collector.RawRecord
import de.egor.culturalfootprint.record.collector.RecordSource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RawRecordRepositoryTest {

    private val repository = RawRecordRepository(
        RawRecordRepositoryProperties(outputFile = "test.data"), ObjectMapper()
    )

    @BeforeAll
    fun init() {
        repository.init()
    }

    @Test
    internal fun getLastRecord() {
        repository.save(
            listOf(
                generateRawRecordWithId(3),
                generateRawRecordWithId(20),
                generateRawRecordWithId(5)
            )
        )

        val latestRecord = repository.getLatestRecord()
        assertThat(latestRecord)
            .isNotEmpty()
            .map { it.source.tweetId }
            .contains(20L)
    }

    @Test
    internal fun saveRecords() {
        val record = RawRecord(date = LocalDateTime.now(), data = "test data", source = RecordSource(tweetId = 1))
        val record2 = RawRecord(date = LocalDateTime.now(), data = "test data2", source = RecordSource(tweetId = 2))

        repository.save(listOf(record, record2))
    }

    private fun generateRawRecordWithId(tweetId: Long) =
        RawRecord(date = LocalDateTime.now(), data = "test data", source = RecordSource(tweetId))
}
