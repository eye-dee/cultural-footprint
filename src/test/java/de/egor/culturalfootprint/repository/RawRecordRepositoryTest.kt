package de.egor.culturalfootprint.repository

import de.egor.culturalfootprint.AbstractRepositoryTest
import de.egor.culturalfootprint.model.RawRecord
import de.egor.culturalfootprint.model.RecordSource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.LocalDateTime
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RawRecordRepositoryTest : AbstractRepositoryTest() {

    private val repository = RawRecordRepository(
        RawRecordRepositoryProperties(),
        db
    )

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

    @Test
    fun `should get records when cluster id exists`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val record1 = generateRawRecordWithId(10, clusterId)
            val record2 = generateRawRecordWithId(11, clusterId)

            repository.save(listOf(record1, record2))

            assertThat(repository.findAllByClusterId(clusterId))
                .hasSize(2)
                .usingElementComparatorIgnoringFields("date")
                .containsExactlyInAnyOrder(record1, record2)
        }
    }

    @Test
    fun `should get 0 records when no records with clusterId`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val record1 = generateRawRecordWithId(20)
            val record2 = generateRawRecordWithId(21)

            repository.save(listOf(record1, record2))

            assertThat(repository.findAllByClusterId(clusterId))
                .isEmpty()
        }
    }

    private fun generateRawRecordWithId(tweetId: Long, clusterId: UUID = UUID.randomUUID()) =
        RawRecord(
            date = LocalDateTime.now(),
            data = "test data",
            source = RecordSource(tweetId),
            id = UUID.randomUUID(),
            cluster = clusterId
        )
}
