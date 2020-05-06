package de.egor.culturalfootprint.admin.model

import org.bson.codecs.pojo.annotations.BsonId
import java.util.UUID

data class Cluster(
    @BsonId val id: UUID,
    val week: String
)
