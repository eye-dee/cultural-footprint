package de.egor.culturalfootprint.admin.controller

import de.egor.culturalfootprint.admin.dto.ClusterResult
import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.service.ClusterService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/clusters")
class ClusterController(
    private val clusterService: ClusterService
) {

    @GetMapping
    suspend fun readAll(): List<Cluster> = clusterService.findCluster()

    @GetMapping("{clusterId}")
    suspend fun readCluster(@PathVariable("clusterId") clusterId: UUID): ResponseEntity<ClusterResult> =
        clusterService.findClusterById(clusterId)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
