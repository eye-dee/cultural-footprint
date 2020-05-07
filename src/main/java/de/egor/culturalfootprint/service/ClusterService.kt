package de.egor.culturalfootprint.service

import de.egor.culturalfootprint.admin.dto.ClusterResult
import de.egor.culturalfootprint.repository.ClusterRepository
import de.egor.culturalfootprint.repository.RawRecordRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ClusterService(
    private val clusterRepository: ClusterRepository,
    private val rawRecordRepository: RawRecordRepository
) {

    suspend fun findCluster() = clusterRepository.findClusters()

    suspend fun findClusterById(clusterId: UUID): ClusterResult? =
        clusterRepository.findClusterById(clusterId)
            ?.let {
                ClusterResult(
                    cluster = it,
                    records = rawRecordRepository.findAllByClusterId(clusterId)
                )
            }
}
