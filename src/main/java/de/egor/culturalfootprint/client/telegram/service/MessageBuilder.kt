package de.egor.culturalfootprint.client.telegram.service

import de.egor.culturalfootprint.admin.dto.ClusterResult
import de.egor.culturalfootprint.model.RawRecord
import org.springframework.stereotype.Service

@Service
class MessageBuilder {

    fun buildMessage(clusterResult: ClusterResult): List<String> =
        clusterResult.records.windowed(recordsPerMessage, 1, true)
            .withIndex()
            .map {
                formatTitle(clusterResult, it) +
                    it.value.joinToString(
                        "\n\n"
                    ) { rawRecord ->
                        var preparedMessage = rawRecord.data.replace("\n", " ")
                        preparedMessage = escapeMarkdown(preparedMessage)
                        rawRecord.source.sourceRepresentation?.let {
                            val escapedName = escapeMarkdown(it.name)
                            val userRepresentationLine =
                                "[$escapedName](https://twitter.com/${it.username})\n"
                            return@joinToString userRepresentationLine + preparedMessage
                        }
                        return@joinToString preparedMessage
                    }
            }

    private fun formatTitle(clusterResult: ClusterResult, it: IndexedValue<List<RawRecord>>) =
        if (clusterResult.records.size > recordsPerMessage)
            "*${resolvePunctuation(clusterResult.cluster.name)} Часть ${it.index + 1}*\n\n"
        else
            "*${clusterResult.cluster.name}*\n\n"

    private fun resolvePunctuation(name: String?): String? =
        name?.let {
            if (listOf('.', '?', '!').contains(name.last())) name else "$name."
        }


    private fun escapeMarkdown(input: String): String {
        var escapedString = input
        for (character in charactersToEscape) {
            escapedString = escapedString.replace(character, "\\$character")
        }
        return escapedString
    }

    companion object {
        val charactersToEscape = listOf("\\", "`", "*", "_", "[", "]")
        const val recordsPerMessage = 20

//        \   backslash
//        `   backtick
//        *   asterisk
//        _   underscore
//        {}  curly braces
//        []  square brackets
//        ()  parentheses
//        #   hash mark
//        +   plus sign
//        -   minus sign (hyphen)
//        .   dot
//        !   exclamation mark
    }
}
