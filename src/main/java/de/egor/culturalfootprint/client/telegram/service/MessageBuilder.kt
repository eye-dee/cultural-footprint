package de.egor.culturalfootprint.client.telegram.service

import de.egor.culturalfootprint.admin.dto.ClusterResult
import org.springframework.stereotype.Service

@Service
class MessageBuilder {

    fun buildMessage(clusterResult: ClusterResult): String =
        "*${clusterResult.cluster.name}\n*" +
        clusterResult.records.fold("") { acc, record ->
            acc + "\n_______________________________\n\n" + record.data
        }
}
