package de.egor.culturalfootprint.admin.controller

import de.egor.culturalfootprint.record.RawRecordService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/raw-records")
class RawRecordController(
        private val rawRecordService: RawRecordService
) {

    @PostMapping("{recordId}/approval")
    suspend fun submitApproval(@PathVariable recordId: UUID): ResponseEntity<Void> =
            if (rawRecordService.submitApproval(recordId))
                ResponseEntity.noContent().build()
            else
                ResponseEntity.notFound().build()

    @PostMapping("{recordId}/withdrawal")
    suspend fun withdrawApproval(@PathVariable recordId: UUID): ResponseEntity<Void> =
            if (rawRecordService.withdrawApproval(recordId))
                ResponseEntity.noContent().build()
            else
                ResponseEntity.notFound().build()
}
