package de.egor.culturalfootprint.repository

import de.egor.culturalfootprint.model.Cluster
import de.egor.culturalfootprint.repository.MongoUtils.D
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.aggregate
import org.springframework.stereotype.Repository

@Repository
open class WeekRepository(
        db: CoroutineDatabase
) {

    private val col = db.getCollection<Cluster>("Clusters")

    suspend fun findWeeks(): List<String> =
        col.aggregate<WeeksAggregationResult>(
                """
    [
        { ${D}group: { _id: "${D}week" } },
        { ${D}sort: { _id: -1} },
        { ${D}limit : 5 }
    ]
    """.trimIndent()
        )
                .toList()
                .map { it._id }


}

internal data class WeeksAggregationResult(val _id: String)
