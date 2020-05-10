package de.egor.culturalfootprint.model

import org.bson.codecs.pojo.annotations.BsonId
import java.util.UUID

data class Cluster(
    @BsonId val id: UUID,
    val week: String,
    val status: ClusterStatus? = null
)

enum class ClusterStatus {
    APPROVED,
    DECLINED
}
