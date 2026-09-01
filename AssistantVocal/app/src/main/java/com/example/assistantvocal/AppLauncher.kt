package com.example.assistantvocal

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Regroupe la logique "ouvrir une appli" et "chercher + lire quelque chose".
 *
 * IMPORTANT (version test) :
 * On peut ouvrir YouTube/Spotify sur une recherche directement, sans clé API.
 * En revanche, sélectionner AUTOMATIQUEMENT le N-ième résultat et lancer sa
 * lecture nécessite d'aller chercher la liste des résultats via une vraie API
 * (YouTube Data API v3, ou Spotify Web API + App Remote SDK) pour connaître
 * l'identifiant exact de la vidéo/morceau à ouvrir. C'est l'étape 2, décrite
 * dans le README du projet.
 */
object AppLauncher {

    fun openApp(context: Context, appName: String) {
        val packageName = packageNameFor(appName) ?: return
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        } else {
            Toast.makeText(context, "$appName n'est pas installée sur ce téléphone", Toast.LENGTH_SHORT).show()
        }
    }

    fun searchAndPlay(context: Context, appName: String, query: String, index: Int?) {
        when (appName) {
            "youtube" -> searchYoutube(context, query, index)
            "spotify" -> searchSpotify(context, query, index)
        }
    }

    private fun packageNameFor(appName: String): String? = when (appName) {
        "youtube" -> "com.google.android.youtube"
        "spotify" -> "com.spotify.music"
        else -> null
    }

    private fun searchYoutube(context: Context, query: String, index: Int?) {
        val encoded = Uri.encode(query)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.youtube.com/results?search_query=$encoded")
            setPackage("com.google.android.youtube")
        }
        openOrFallback(context, intent, "https://www.youtube.com/results?search_query=$encoded")

        if (index != null) {
            // TODO étape 2 : appeler l'API YouTube Data v3 pour récupérer le
            // videoId du N-ième résultat, puis ouvrir directement
            // "https://www.youtube.com/watch?v=<videoId>"
            Toast.makeText(context, "Choisis la vidéo n°$index (sélection auto à venir)", Toast.LENGTH_LONG).show()
        }
    }

    private fun searchSpotify(context: Context, query: String, index: Int?) {
        val encoded = Uri.encode(query)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("spotify:search:$encoded")
            setPackage("com.spotify.music")
        }
        openOrFallback(context, intent, "https://open.spotify.com/search/$encoded")

        if (index != null) {
            // TODO étape 2 : utiliser la Spotify Web API pour chercher, puis
            // le Spotify App Remote SDK pour lire directement l'URI du morceau
            Toast.makeText(context, "Choisis le résultat n°$index (sélection auto à venir)", Toast.LENGTH_LONG).show()
        }
    }

    private fun openOrFallback(context: Context, intent: Intent, webUrl: String) {
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)))
        }
    }
}
