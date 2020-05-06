package de.egor.culturalfootprint.admin.repository

import de.egor.culturalfootprint.AbstractRepositoryTest
import de.egor.culturalfootprint.admin.model.Cluster
import de.egor.culturalfootprint.record.collector.RawRecord
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

internal class ClusterRepositoryTest : AbstractRepositoryTest() {
    val clusterRepository = ClusterRepository(db)

    @BeforeEach
    internal fun setUpEach() {
        runBlocking {
            db.getCollection<RawRecord>("Clusters").deleteMany("{}")
        }
    }

    @Test
    fun `should find zero results when collection is empty`() {
        runBlocking {
            assertThat(clusterRepository.findClusters())
                .isEmpty()
        }
    }

    @Test
    fun `should return results when collection has some elements`() {
        runBlocking {
            val expected = listOf(
                Cluster(UUID.randomUUID(), "week1"),
                Cluster(UUID.randomUUID(), "week2")
            )
            db.getCollection<Cluster>("Clusters")
                .insertMany(
                    expected
                )

            assertThat(clusterRepository.findClusters())
                .isEqualTo(expected)
        }
    }

}
