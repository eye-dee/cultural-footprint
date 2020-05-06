package de.egor.culturalfootprint.admin.controller

import de.egor.culturalfootprint.admin.service.ClusterService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/clusters")
class ClusterController(
    private val clusterService: ClusterService
) {

    @GetMapping
    suspend fun readAll() = clusterService.findCluster()
}
