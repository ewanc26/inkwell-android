# Inkwell for Android

> **⚠️ Experimental** — This is the Android companion to [Inkwell for iOS](https://github.com/ewanc26/inkwell). The iOS version is the primary, production-ready client. This Android port is an experimental work-in-progress building toward feature parity.

A native reader and writer for the [Standard.site](https://standard.site) publishing ecosystem on AT Protocol, built with Jetpack Compose, Material 3, and Kotlin.

**The Writer's Desk — for Android.**

## Features

- **Read** — Reads `site.standard.publication` and `site.standard.document` records from their owning PDS, rendered in a familiar three-tab layout (Read / Discover / Write). Renders Leaflet block content and Markpub/Offprint/pckt markdown formats. Resolves display theme from Leaflet's rich palette through `basicTheme` to Material 3 defaults.
- **Discover** — Searches the cross-platform public index used by the Standard.site ecosystem, then fetches authoritative records directly from the author. Cross-repo comment discovery via Constellation (microcosm.blue). Paginated feed with infinite scroll and prev/next post navigation.
- **Write** — Publishes Standard.site documents with portable metadata and selectable content formats.
- **AT Protocol Native** — OAuth-based authentication with your AT Protocol handle (no app password). Silent session restoration on relaunch.
- **Verification Built In** — Checks publication `.well-known` verification and document `<link rel="site.standard.document">` verification. Background notification polling via WorkManager.

## Relationship to Inkwell iOS

| | iOS (primary) | Android (experimental) |
|---|---|---|
| **Language** | Swift | Kotlin |
| **UI** | SwiftUI | Jetpack Compose + Material 3 |
| **Navigation** | NavigationStack | Navigation Compose |
| **Networking** | URLSession + OAuthenticator | OkHttp |
| **Serialization** | Codable | kotlinx.serialization |
| **DI** | @Environment | Hilt |
| **Background** | BGAppRefreshTask | WorkManager |
| **Status** | Production-ready | Experimental |

Both versions share the same AT Protocol data model shapes, the same Constellation cross-repo discovery pattern, the same publication/document theme resolution cascade, and the same three-tab structure (Read / Discover / Write).

## Architecture

```
app/src/main/java/uk/ewancroft/inkwell/
├── data/
│   ├── model/Models.kt               # AT Protocol data types
│   ├── remote/AtProtoApi.kt          # PDS HTTP client (auth + unauth)
│   └── remote/ConstellationClient.kt # Cross-repo backlink discovery
├── ui/
│   ├── theme/Theme.kt                # Material 3 + standard.site theme resolution
│   ├── navigation/NavGraph.kt        # Bottom nav + screen routes
│   ├── auth/LoginScreen.kt           # OAuth sign-in with onboarding
│   ├── reader/ReaderScreen.kt        # Feed + post detail + block rendering
│   ├── writer/WriterScreen.kt        # Markdown composer
│   └── discover/DiscoverScreen.kt
├── InkwellApp.kt
└── MainActivity.kt
```

## Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/ewanc26/inkwell-android.git
   cd inkwell-android
   ```
2. Open in Android Studio (Hedgehog or later).
3. Build with Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
4. Run on an API 26+ device or emulator.
5. Sign in with your AT Protocol handle via OAuth (the system browser will open for authorization).

## Dependencies

- Jetpack Compose (Material 3)
- Navigation Compose
- OkHttp + kotlinx.serialization
- Hilt (dependency injection)
- WorkManager (background tasks)
- Coil (image loading)

## License

AGPL 3.0 — see [Inkwell iOS](https://github.com/ewanc26/inkwell) for the canonical license.
