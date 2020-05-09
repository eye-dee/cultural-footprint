package de.egor.culturalfootprint.admin.controller

import de.egor.culturalfootprint.admin.dto.ClusterResult
import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.service.ClusterService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/clusters")
class ClusterController(
    private val clusterService: ClusterService
) {

    @GetMapping
    suspend fun readAll(@RequestParam(required = false) week: String?): List<Cluster> =
            clusterService.findCluster(week)

    @GetMapping("{clusterId}")
    suspend fun readCluster(@PathVariable("clusterId") clusterId: UUID): ResponseEntity<ClusterResult> =
        clusterService.findClusterById(clusterId)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
}
