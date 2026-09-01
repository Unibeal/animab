# Assistant Vocal (Android)

App de test : un assistant vocal type "Alexa" qui comprend des commandes
en français et ouvre YouTube / Spotify.

## Ce qui marche déjà (version test)

- Appui sur le bouton 🎤 → écoute la voix (reconnaissance vocale d'Android)
- `"ouvre youtube"` → ouvre l'appli YouTube
- `"ouvre spotify"` → ouvre l'appli Spotify
- `"recherche PLATINY"` → ouvre YouTube/Spotify directement sur les résultats de recherche "PLATINY"
- `"recherche PLATINY joue la 5"` ou `"... joue la cinquième"` → ouvre la recherche et affiche un message
  indiquant de choisir le résultat n°5 (la sélection **automatique** du N-ième résultat n'est pas encore branchée,
  voir "Prochaine étape" ci-dessous)

## Ouvrir le projet

1. Installer [Android Studio](https://developer.android.com/studio)
2. `File > Open` → sélectionner le dossier `AssistantVocal`
3. Laisser Gradle synchroniser (ça peut prendre quelques minutes la première fois)
4. Brancher un téléphone Android en USB (mode débogage activé) ou utiliser un émulateur
5. Cliquer sur ▶️ Run

## Mettre le projet sur GitHub

Depuis le dossier `AssistantVocal` :

```bash
git init
git add .
git commit -m "Première version : app de test assistant vocal"
git branch -M main
git remote add origin https://github.com/TON-PSEUDO/assistant-vocal.git
git push -u origin main
```

(Remplace l'URL par celle de ton propre repo GitHub, créé au préalable sur github.com)

## Structure du projet

```
AssistantVocal/
├── app/
│   └── src/main/
│       ├── java/com/example/assistantvocal/
│       │   ├── MainActivity.kt      → capture la voix, affiche le résultat
│       │   ├── CommandParser.kt     → comprend la phrase (appli / recherche / n°)
│       │   └── AppLauncher.kt       → ouvre YouTube/Spotify et lance les recherches
│       ├── res/layout/activity_main.xml
│       └── AndroidManifest.xml      → permissions micro + internet
├── build.gradle.kts
└── settings.gradle.kts
```

## Prochaine étape : jouer automatiquement le N-ième résultat

Aujourd'hui l'app ouvre juste la page de recherche : c'est toi qui choisis la vidéo/le
morceau à la main. Pour que l'app choisisse elle-même le 5e résultat, il faut :

### YouTube
1. Créer une clé API sur [Google Cloud Console](https://console.cloud.google.com/) (activer "YouTube Data API v3")
2. Appeler l'endpoint `search` de l'API avec la requête → elle renvoie une liste ordonnée de vidéos avec leur `videoId`
3. Prendre le `videoId` du N-ième élément et ouvrir directement :
   `https://www.youtube.com/watch?v=<videoId>` → ça lance la lecture directement, sans passer par la liste

### Spotify
1. Créer une app sur le [Spotify Developer Dashboard](https://developer.spotify.com/dashboard) pour obtenir un `Client ID`
2. Utiliser la Spotify Web API (`/search`) pour récupérer la liste ordonnée de morceaux avec leur `uri` (ex: `spotify:track:xxxx`)
3. Utiliser le **Spotify App Remote SDK** (Android) pour lancer directement la lecture du morceau choisi
   (nécessite que l'utilisateur ait l'appli Spotify installée, et idéalement un compte Premium pour le contrôle total)

Ces deux clés/identifiants ne doivent pas être codés en dur dans une app publique (ils seraient visibles
en décompilant l'apk) — pour une app perso/test c'est acceptable, mais pour une app distribuée il vaudrait
mieux passer par un petit serveur intermédiaire qui garde la clé secrète.

## Idée pour la suite (vrai "Alexa-like")

- Ajouter une phrase de réveil ("Dis Assistant...") avec un service en arrière-plan
- Faire parler l'app en retour (`TextToSpeech`) pour confirmer les commandes
- Étendre `CommandParser` avec d'autres actions (musique suivante, pause, volume...)
