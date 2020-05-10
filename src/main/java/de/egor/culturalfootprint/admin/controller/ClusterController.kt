package de.egor.culturalfootprint.admin.controller

import de.egor.culturalfootprint.admin.dto.ClusterResult
import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.service.ClusterService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
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

    @PostMapping("{clusterId}/approval")
    suspend fun submitApprove(@PathVariable clusterId: UUID): ResponseEntity<Void> =
            if (clusterService.submitApproval(clusterId))
                ResponseEntity.noContent().build()
            else
                ResponseEntity.notFound().build()

    @PostMapping("{clusterId}/declination")
    suspend fun submitDeclination(@PathVariable clusterId: UUID): ResponseEntity<Void> =
            if (clusterService.submitDeclination(clusterId))
                ResponseEntity.noContent().build()
            else
                ResponseEntity.notFound().build()
}
