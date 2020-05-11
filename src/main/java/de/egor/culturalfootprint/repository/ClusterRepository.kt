package de.egor.culturalfootprint.repository

import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.model.ClusterStatus
import org.litote.kmongo.SetTo
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class ClusterRepository(
    db: CoroutineDatabase
) {

  private val col = db.getCollection<Cluster>("Clusters")

  suspend fun findClusters() =
      col.find()
          .limit(50)
          .sort(descending(Cluster::week))
          .toList()

  suspend fun findClustersByWeek(week: String): List<Cluster> =
      col.find(Cluster::week eq week)
          .toList()

  suspend fun findClusterById(clusterId: UUID): Cluster? =
      col.findOneById(clusterId)

  suspend fun updateStatus(clusterId: UUID, status: ClusterStatus): Boolean =
      col.updateOne(
          Cluster::id eq clusterId,
          set(SetTo(Cluster::status, status))
      ).matchedCount > 0

  suspend fun updateName(clusterId: UUID, name: String): Boolean =
      col.updateOne(
          Cluster::id eq clusterId,
          set(SetTo(Cluster::name, name))
      ).matchedCount > 0
}
