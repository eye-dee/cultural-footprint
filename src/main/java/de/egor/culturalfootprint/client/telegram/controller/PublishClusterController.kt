package de.egor.culturalfootprint.client.telegram.controller

import de.egor.culturalfootprint.client.telegram.service.PublisherService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RequestMapping("/api/clients/telegram")
@RestController
class PublishClusterController(
        private val publisherService: PublisherService
) {

    @PostMapping("/clusters/{clusterId}/publish")
    suspend fun publishCluster(@PathVariable("clusterId") clusterId: UUID,
                               @RequestParam("preview", required = false) preview: Boolean?):
            ResponseEntity<Void> =
            publisherService.publishClusterForAllUsers(clusterId, preview ?: true)
                    .let { ResponseEntity.accepted().build() }
}
