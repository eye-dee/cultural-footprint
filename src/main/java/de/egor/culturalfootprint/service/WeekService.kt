package de.egor.culturalfootprint.service

import de.egor.culturalfootprint.repository.ClusterRepository
import de.egor.culturalfootprint.repository.WeekRepository
import org.springframework.stereotype.Service

@Service
class WeekService(
        private val clusterRepository: WeekRepository
) {

    suspend fun findWeeks() : List<String> = clusterRepository.findWeeks()

}
