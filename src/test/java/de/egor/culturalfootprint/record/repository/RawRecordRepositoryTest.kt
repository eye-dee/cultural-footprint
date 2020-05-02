package de.egor.culturalfootprint.record.repository

import de.egor.culturalfootprint.record.collector.RawRecord
import de.egor.culturalfootprint.record.collector.RecordSource
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.time.LocalDateTime
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RawRecordRepositoryTest {

    private lateinit var db: CoroutineDatabase
    private lateinit var repository: RawRecordRepository
    private lateinit var mongodExecutable: MongodExecutable

    @BeforeEach
    internal fun setUpEach() {
        runBlocking {
            db.getCollection<RawRecord>("RawRecords").deleteMany("{}")
        }
    }

    @BeforeAll
    fun setUp() {
        val ip = "localhost"
        val port = 27017

        val mongodConfig = MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(Net(ip, port, Network.localhostIsIPv6()))
                .build()

        val starter = MongodStarter.getDefaultInstance()
        mongodExecutable = starter.prepare(mongodConfig)
        mongodExecutable.start()
        val mongoClient = KMongo.createClient().coroutine
        db = mongoClient.getDatabase("cultural")
        repository = RawRecordRepository(RawRecordRepositoryProperties(), db)
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
