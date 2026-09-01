package com.example.assistantvocal

sealed class Command {
    data class OpenApp(val appName: String) : Command()
    data class SearchAndPlay(val appName: String, val query: String, val index: Int?) : Command()
    object Unknown : Command()
}

/**
 * Analyse une phrase en français comme :
 *  - "ouvre youtube"
 *  - "ouvre spotify"
 *  - "recherche PLATINY"
 *  - "recherche PLATINY joue la 5"
 *  - "recherche PLATINY joue la cinquième"
 *
 * C'est une version simple (basée sur des mots-clés) pensée pour être
 * facile à faire évoluer. Pour des phrases plus naturelles/variées,
 * on pourra plus tard brancher un vrai moteur de NLU.
 */
object CommandParser {

    private val numberWords = mapOf(
        "première" to 1, "premier" to 1, "un" to 1,
        "deuxième" to 2, "deux" to 2,
        "troisième" to 3, "trois" to 3,
        "quatrième" to 4, "quatre" to 4,
        "cinquième" to 5, "cinq" to 5,
        "sixième" to 6, "six" to 6,
        "septième" to 7, "sept" to 7,
        "huitième" to 8, "huit" to 8,
        "neuvième" to 9, "neuf" to 9,
        "dixième" to 10, "dix" to 10
    )

    fun parse(raw: String): Command {
        val text = raw.lowercase().trim()

        val appName = when {
            text.contains("youtube") -> "youtube"
            text.contains("spotify") -> "spotify"
            else -> null
        }

        // Cas simple : juste ouvrir l'appli, sans recherche ni lecture
        val wantsOpenOnly = text.contains("ouvre") && !text.contains("recherche") && !text.contains("joue")
        if (appName != null && wantsOpenOnly) {
            return Command.OpenApp(appName)
        }

        if (appName == null) return Command.Unknown

        // Extrait la requête entre "recherche" et "joue" (si présent)
        val query = extractQuery(text) ?: return Command.Unknown

        val index = extractIndex(text)

        return Command.SearchAndPlay(appName, query, index)
    }

    private fun extractQuery(text: String): String? {
        val start = text.indexOf("recherche")
        if (start == -1) return null

        var afterKeyword = text.substring(start + "recherche".length).trim()

        val joueIndex = afterKeyword.indexOf(" joue")
        if (joueIndex != -1) {
            afterKeyword = afterKeyword.substring(0, joueIndex)
        }

        return afterKeyword.trim().ifEmpty { null }
    }

    private fun extractIndex(text: String): Int? {
        // Ex: "joue la 5" / "joue le 5"
        val digitMatch = Regex("joue (?:la|le) (\\d+)").find(text)
        if (digitMatch != null) {
            return digitMatch.groupValues[1].toIntOrNull()
        }

        // Ex: "joue la cinquième" / "cinquième vidéo" / "le cinquième"
        val afterJoue = text.substringAfter("joue", "")
        for ((word, num) in numberWords) {
            if (afterJoue.contains(word)) {
                return num
            }
        }

        return null
    }
}
