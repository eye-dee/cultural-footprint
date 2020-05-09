package de.egor.culturalfootprint.repository

import de.egor.culturalfootprint.AbstractRepositoryTest
import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.model.RawRecord
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class WeekRepositoryTest : AbstractRepositoryTest() {
    private val weekRepository = WeekRepository(db)

    @BeforeEach
    internal fun setUpEach() {
        runBlocking {
            db.getCollection<RawRecord>("Clusters").deleteMany("{}")
        }
    }

    @Test
    fun `should find zero results when collection is empty`() {
        runBlocking {
            assertThat(weekRepository.findWeeks())
                .isEmpty()
        }
    }

    @Test
    fun `should return results when collection has some elements`() {
        runBlocking {
            db.getCollection<Cluster>("Clusters")
                    .insertOne(Cluster(UUID.randomUUID(), "2020-01"))
            val expected = listOf(
                    Cluster(UUID.randomUUID(), "2020-60"),
                    Cluster(UUID.randomUUID(), "2020-50"),
                    Cluster(UUID.randomUUID(), "2020-40"),
                    Cluster(UUID.randomUUID(), "2020-30"),
                    Cluster(UUID.randomUUID(), "2020-05")
            )
            db.getCollection<Cluster>("Clusters")
                .insertMany(expected)

            assertThat(weekRepository.findWeeks())
                .isEqualTo(expected.map { it.week })
        }
    }

}
