package de.egor.culturalfootprint.admin.service

import de.egor.culturalfootprint.admin.repository.ClusterRepository
import org.springframework.stereotype.Service

@Service
class ClusterService(
    private val clusterRepository: ClusterRepository
) {

    suspend fun findCluster() = clusterRepository.findClusters()
}
