package de.egor.culturalfootprint.admin.repository

import de.egor.culturalfootprint.admin.model.Cluster
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.springframework.stereotype.Repository

@Repository
open class ClusterRepository(
    db: CoroutineDatabase
) {

    private val col = db.getCollection<Cluster>("Clusters")

    suspend fun findClusters() =
        col.find()
            .limit(10)
            .toList()
}
