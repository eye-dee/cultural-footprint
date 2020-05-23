package de.egor.culturalfootprint.repository

import de.egor.culturalfootprint.AbstractRepositoryTest
import de.egor.culturalfootprint.client.telegram.model.UserEntity
import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.model.ClusterStatus
import de.egor.culturalfootprint.model.RawRecord
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ClusterRepositoryTest : AbstractRepositoryTest() {

    private val clusterRepository = ClusterRepository(db, ClusterRepositoryProperties())

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

    @Test
    internal fun `should return true if status is updated`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            db.getCollection<Cluster>("Clusters")
                .insertOne(Cluster(clusterId, "2020-05"))
            assertThat(clusterRepository.findClusterById(clusterId)!!.status)
                .isNull()

            val result = clusterRepository.updateStatus(clusterId, ClusterStatus.APPROVED)

            assertThat(result).isTrue()
            assertThat(clusterRepository.findClusterById(clusterId)!!.status)
                .isEqualTo(ClusterStatus.APPROVED)
        }
    }

    @Test
    internal fun `should return false if cluster is not found and status is not updated`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            db.getCollection<Cluster>("Clusters")
                .insertOne(Cluster(clusterId, "2020-05"))
            assertThat(clusterRepository.findClusterById(clusterId)!!.status)
                .isNull()

            val result = clusterRepository.updateStatus(UUID.randomUUID(), ClusterStatus.APPROVED)

            assertThat(result).isFalse()
            assertThat(clusterRepository.findClusterById(clusterId)!!.status)
                .isNull()
        }
    }

    @Test
    internal fun `should return true if name is updated`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            db.getCollection<Cluster>("Clusters")
                .insertOne(Cluster(clusterId, "2020-05"))
            assertThat(clusterRepository.findClusterById(clusterId)!!.name)
                .isNull()

            val result = clusterRepository.updateName(clusterId, "updated name")

            assertThat(result).isTrue()
            assertThat(clusterRepository.findClusterById(clusterId)!!.name)
                .isEqualTo("updated name")
        }
    }

    @Test
    internal fun `should return false if cluster is not found and name is not updated`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            db.getCollection<Cluster>("Clusters")
                .insertOne(Cluster(clusterId, "2020-05"))
            assertThat(clusterRepository.findClusterById(clusterId)!!.name)
                .isNull()

            val result = clusterRepository.updateName(UUID.randomUUID(), "updated name")

            assertThat(result).isFalse()
            assertThat(clusterRepository.findClusterById(clusterId)!!.name)
                .isNull()
        }
    }

    @Test
    internal fun `should return true if published is updated`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val cluster = Cluster(
                id = clusterId,
                week = "2020-05",
                status = ClusterStatus.APPROVED,
                name = "test"
            )
            db.getCollection<Cluster>("Clusters")
                .insertOne(cluster)
            assertThat(clusterRepository.findClusterById(clusterId)!!.published)
                .isFalse()

            val result = clusterRepository.makePublished(clusterId)

            assertThat(result).isTrue()
            assertThat(clusterRepository.findClusterById(clusterId)!!.published)
                .isTrue()
        }
    }

    @Test
    internal fun `should return true if cluster is not approved and published is not updated updated`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val cluster = Cluster(
                id = clusterId,
                week = "2020-05",
                name = "test"
            )
            db.getCollection<Cluster>("Clusters")
                .insertOne(cluster)
            assertThat(clusterRepository.findClusterById(clusterId)!!.published)
                .isFalse()

            val result = clusterRepository.makePublished(clusterId)

            assertThat(result).isFalse()
            assertThat(clusterRepository.findClusterById(clusterId)!!.published)
                .isFalse()
        }
    }

    @Test
    internal fun `should return true if cluster is not named and published is not updated updated`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val cluster = Cluster(
                id = clusterId,
                week = "2020-05",
                status = ClusterStatus.APPROVED
            )
            db.getCollection<Cluster>("Clusters")
                .insertOne(cluster)
            assertThat(clusterRepository.findClusterById(clusterId)!!.published)
                .isFalse()

            val result = clusterRepository.makePublished(clusterId)

            assertThat(result).isFalse()
            assertThat(clusterRepository.findClusterById(clusterId)!!.published)
                .isFalse()
        }
    }

    @Test
    internal fun `should return false if cluster is not found and published is not updated`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            db.getCollection<Cluster>("Clusters")
                .insertOne(Cluster(clusterId, "2020-05"))
            assertThat(clusterRepository.findClusterById(clusterId)!!.published)
                .isFalse()

            val result = clusterRepository.updateName(UUID.randomUUID(), "updated name")

            assertThat(result).isFalse()
            assertThat(clusterRepository.findClusterById(clusterId)!!.published)
                .isFalse()
        }
    }

    @Test
    fun `should add user to liked when empty list`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val week = "2020-05"
            val insertedCluster = Cluster(clusterId, week)
            val userEntity = UserEntity(userId, 1)
            db.getCollection<Cluster>("Clusters")
                .insertOne(insertedCluster)

            val expected = insertedCluster.copy(likedBy = listOf(userId))
            assertThat(clusterRepository.likedBy(clusterId, userEntity))
                .isNotNull

            assertThat(clusterRepository.findClusterById(clusterId))
                .isEqualTo(expected)
        }
    }

    @Test
    fun `should add user to liked when liked has element`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val week = "2020-05"
            val insertedCluster = Cluster(
                id = clusterId, week = week, likedBy = listOf(UUID.randomUUID())
            )
            val userEntity = UserEntity(userId, 1)
            db.getCollection<Cluster>("Clusters")
                .insertOne(insertedCluster)

            val expected = insertedCluster.copy(likedBy = insertedCluster.likedBy.plus(userId))
            assertThat(clusterRepository.likedBy(clusterId, userEntity))
                .isNotNull

            assertThat(clusterRepository.findClusterById(clusterId))
                .isEqualTo(expected)
        }
    }

    @Test
    fun `should remove user from disliked when likedBy()`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val week = "2020-05"
            val insertedCluster = Cluster(
                id = clusterId, week = week, dislikedBy = listOf(userId)
            )
            val userEntity = UserEntity(userId, 1)
            db.getCollection<Cluster>("Clusters")
                .insertOne(insertedCluster)

            val expected = insertedCluster.copy(
                likedBy = listOf(userId),
                dislikedBy = emptyList()
            )
            assertThat(clusterRepository.likedBy(clusterId, userEntity))
                .isNotNull

            assertThat(clusterRepository.findClusterById(clusterId))
                .isEqualTo(expected)
        }
    }

    @Test
    fun `should add user to disliked when empty list`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val week = "2020-05"
            val insertedCluster = Cluster(clusterId, week)
            val userEntity = UserEntity(userId, 1)
            db.getCollection<Cluster>("Clusters")
                .insertOne(insertedCluster)

            val expected = insertedCluster.copy(dislikedBy = listOf(userId))
            assertThat(clusterRepository.dislikedBy(clusterId, userEntity))
                .isNotNull

            assertThat(clusterRepository.findClusterById(clusterId))
                .isEqualTo(expected)
        }
    }

    @Test
    fun `should add user to disliked when disliked has element`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val week = "2020-05"
            val insertedCluster = Cluster(
                id = clusterId, week = week, dislikedBy = listOf(UUID.randomUUID())
            )
            val userEntity = UserEntity(userId, 1)
            db.getCollection<Cluster>("Clusters")
                .insertOne(insertedCluster)

            val expected = insertedCluster.copy(dislikedBy = insertedCluster.dislikedBy.plus(userId))
            assertThat(clusterRepository.dislikedBy(clusterId, userEntity))
                .isNotNull

            assertThat(clusterRepository.findClusterById(clusterId))
                .isEqualTo(expected)
        }
    }

    @Test
    fun `should remove user from liked when dislikedBy()`() {
        runBlocking {
            val clusterId = UUID.randomUUID()
            val userId = UUID.randomUUID()
            val week = "2020-05"
            val insertedCluster = Cluster(
                id = clusterId, week = week, likedBy = listOf(userId)
            )
            val userEntity = UserEntity(userId, 1)
            db.getCollection<Cluster>("Clusters")
                .insertOne(insertedCluster)

            val expected = insertedCluster.copy(
                likedBy = emptyList(),
                dislikedBy = listOf(userId)
            )
            assertThat(clusterRepository.dislikedBy(clusterId, userEntity))
                .isNotNull

            assertThat(clusterRepository.findClusterById(clusterId))
                .isEqualTo(expected)
        }
    }

}

