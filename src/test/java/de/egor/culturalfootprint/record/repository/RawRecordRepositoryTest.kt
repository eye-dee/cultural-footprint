package de.egor.culturalfootprint.record.repository

import de.egor.culturalfootprint.record.collector.RawRecord
import de.egor.culturalfootprint.record.collector.RecordSource
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class RawRecordRepositoryTest {

    private val repository = RawRecordRepository(RawRecordRepositoryProperties(outputFile = "test.data"))

    @Test
    internal fun saveRecords() {
        val record = RawRecord(date = LocalDateTime.now(), data = "test data", source = RecordSource(tweetId = 23123))
        val record2 = RawRecord(date = LocalDateTime.now(), data = "test data2", source = RecordSource(tweetId = 23124))

        repository.save(listOf(record, record2))
    }
}
