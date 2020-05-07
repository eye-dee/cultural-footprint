package de.egor.culturalfootprint.repository

import de.egor.culturalfootprint.model.Cluster
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class ClusterRepository(
    db: CoroutineDatabase
) {

    private val col = db.getCollection<Cluster>("Clusters")

    suspend fun findClusters() =
        col.find()
            .limit(10)
            .toList()

    suspend fun findClusterById(clusterId: UUID): Cluster? =
        col.findOneById(clusterId)
}
