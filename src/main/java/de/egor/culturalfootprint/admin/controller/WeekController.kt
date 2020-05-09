package de.egor.culturalfootprint.admin.controller

import de.egor.culturalfootprint.service.WeekService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/weeks")
class WeekController(
    private val weekService: WeekService
) {

    @GetMapping
    suspend fun readAll(): List<String> = weekService.findWeeks()

}
