package de.egor.culturalfootprint.service

import de.egor.culturalfootprint.admin.dto.ClusterResult
import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.model.ClusterStatus
import de.egor.culturalfootprint.repository.ClusterRepository
import de.egor.culturalfootprint.repository.RawRecordRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ClusterService(
    private val clusterRepository: ClusterRepository,
    private val rawRecordRepository: RawRecordRepository
) {

    suspend fun findCluster(week: String?): List<Cluster> =
            week?.let { clusterRepository.findClustersByWeek(week) }
                    ?: clusterRepository.findClusters()

    suspend fun findClusterById(clusterId: UUID): ClusterResult? =
        clusterRepository.findClusterById(clusterId)
            ?.let {
                ClusterResult(
                    cluster = it,
                    records = rawRecordRepository.findAllByClusterId(clusterId)
                )
            }

    suspend fun submitApproval(clusterId: UUID): Boolean =
        clusterRepository.updateStatus(clusterId, ClusterStatus.APPROVED)

    suspend fun submitDeclination(clusterId: UUID): Boolean =
        clusterRepository.updateStatus(clusterId, ClusterStatus.DECLINED)
}
