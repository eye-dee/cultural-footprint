package de.egor.culturalfootprint.repository

import de.egor.culturalfootprint.AbstractRepositoryTest
import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.model.RawRecord
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class ClusterRepositoryTest : AbstractRepositoryTest() {
    private val clusterRepository = ClusterRepository(db)

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
                Cluster(UUID.randomUUID(), "2020-05"),
                Cluster(UUID.randomUUID(), "2020-04")
            )
            db.getCollection<Cluster>("Clusters")
                .insertMany(expected)

            assertThat(clusterRepository.findClusters())
                .isEqualTo(expected)
        }
    }

    @Test
    fun `should find zero results by week when collection is empty`() {
        runBlocking {
            assertThat(clusterRepository.findClustersByWeek("2020-05"))
                    .isEmpty()
        }
    }

    @Test
    fun `should return results by week when collection has some elements`() {
        runBlocking {
            db.getCollection<Cluster>("Clusters")
                    .insertOne(Cluster(UUID.randomUUID(), "2020-05"))
            val expected = listOf(
                    Cluster(UUID.randomUUID(), "2020-06")
            )
            db.getCollection<Cluster>("Clusters")
                    .insertMany(expected)

            assertThat(clusterRepository.findClustersByWeek("2020-06"))
                    .isEqualTo(expected)
        }
    }

    @Test
    fun `should return cluster by id when it exists`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val insertedCluster = Cluster(clusterId, "week1")

            db.getCollection<Cluster>("Clusters")
                .insertOne(insertedCluster)

            assertThat(clusterRepository.findClusterById(clusterId))
                .isNotNull
                .isEqualTo(insertedCluster)
        }
    }

    @Test
    fun `should return cluster by id doesn't exist`() {
        runBlocking {
            val clusterId = UUID.randomUUID()

            assertThat(clusterRepository.findClusterById(clusterId))
                .isNull()
        }
    }

}
