package de.egor.culturalfootprint.client.telegram.service

import de.egor.culturalfootprint.admin.dto.ClusterResult
import org.springframework.stereotype.Service

@Service
class MessageBuilder {

    fun buildMessage(clusterResult: ClusterResult): String =
        "*${clusterResult.cluster.name}*\n\n" +
            clusterResult.records.joinToString(
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

    private fun escapeMarkdown(input: String): String {
        var escapedString = input
        for (character in charactersToEscape) {
            escapedString = escapedString.replace(character, "\\$character")
        }
        return escapedString
    }

    companion object {
        val charactersToEscape = listOf("\\", "`", "*", "_", "[", "]");

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
