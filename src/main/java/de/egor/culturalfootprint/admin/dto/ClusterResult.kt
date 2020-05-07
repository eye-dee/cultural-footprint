package de.egor.culturalfootprint.admin.dto

import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.model.RawRecord

data class ClusterResult(
    val cluster: Cluster,
    val records: List<RawRecord>
)
