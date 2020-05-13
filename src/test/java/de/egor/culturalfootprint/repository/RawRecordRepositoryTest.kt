package de.egor.culturalfootprint.repository

import de.egor.culturalfootprint.AbstractRepositoryTest
import de.egor.culturalfootprint.model.RawRecord
import de.egor.culturalfootprint.model.RecordSource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
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

    @Test
    internal fun `should return true if approved field is set to true`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val rawRecord = generateRawRecordWithId(100, clusterId)
            db.getCollection<RawRecord>("RawRecords")
                .insertOne(rawRecord)
            assertThat(repository.findAllByClusterId(clusterId))
                .hasSize(1)
                .first()
                .satisfies { assertThat(it.approved).isIn(null, false) }

            val result = repository.updateApproval(rawRecord.id, true)

            assertThat(result).isTrue()
            assertThat(repository.findAllByClusterId(clusterId))
                .hasSize(1)
                .first()
                .satisfies { assertThat(it.approved).isTrue() }
        }
    }

    @Test
    internal fun `should return false if cluster is not found and status is not updated`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val rawRecord = generateRawRecordWithId(100, clusterId)
            db.getCollection<RawRecord>("RawRecords")
                .insertOne(rawRecord)
            assertThat(repository.findAllByClusterId(clusterId))
                .hasSize(1)
                .first()
                .satisfies { assertThat(it.approved).isIn(null, false) }

            val result = repository.updateApproval(UUID.randomUUID(), true)

            assertThat(result).isFalse()
            assertThat(repository.findAllByClusterId(clusterId))
                .hasSize(1)
                .first()
                .satisfies { assertThat(it.approved).isIn(null, false) }
        }
    }

    @Test
    fun `should return only approved and clusterId records`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val expected = generateRawRecordWithId(201, clusterId, true)
            db.getCollection<RawRecord>("RawRecords")
                .insertMany(
                    listOf(
                        generateRawRecordWithId(200, clusterId, false),
                        expected,
                        generateRawRecordWithId(202, UUID.randomUUID(), true)
                    )
                )

            assertThat(repository.findAllByClusterIdAndApproved(clusterId))
                .hasSize(1)
                .usingElementComparatorIgnoringFields("date")
                .containsExactly(expected)
        }
    }

    private fun generateRawRecordWithId(
        tweetId: Long,
        clusterId: UUID = UUID.randomUUID(),
        approved: Boolean = false
    ) =
        RawRecord(
            date = LocalDateTime.now(),
            data = "test data",
            source = RecordSource(tweetId),
            id = UUID.randomUUID(),
            cluster = clusterId,
            approved = approved
        )
}
