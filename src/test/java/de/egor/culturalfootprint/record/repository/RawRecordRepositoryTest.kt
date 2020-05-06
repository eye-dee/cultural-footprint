package de.egor.culturalfootprint.record.repository

import de.egor.culturalfootprint.AbstractRepositoryTest
import de.egor.culturalfootprint.record.collector.RawRecord
import de.egor.culturalfootprint.record.collector.RecordSource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RawRecordRepositoryTest: AbstractRepositoryTest() {

    private val repository = RawRecordRepository(RawRecordRepositoryProperties(), db)

    @BeforeEach
    internal fun setUpEach() {
        runBlocking {
            db.getCollection<RawRecord>("RawRecords").deleteMany("{}")
        }
    }

    @AfterAll
    internal fun tearDown() {
        mongodExecutable.stop()
    }

    @Test
    internal fun getLastRecord() {
        runBlocking {
            repository.save(
                    listOf(
                            generateRawRecordWithId(3),
                            generateRawRecordWithId(20),
                            generateRawRecordWithId(5)
                    )
            )

            val latestRecord = repository.getLatestRecordTweetId()
            Assertions.assertThat(latestRecord).isEqualTo(20)
        }
    }

    private fun generateRawRecordWithId(tweetId: Long) =
            RawRecord(date = LocalDateTime.now(), data = "test data", source = RecordSource(tweetId), id = UUID.randomUUID())
}
