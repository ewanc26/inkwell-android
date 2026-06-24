# Inkwell for Android

> **⚠️ Experimental** — This is a parallel implementation of [Inkwell for iOS](https://github.com/ewanc26/inkwell). The iOS version is the primary, production-ready client. This Android port mirrors its architecture and feature set but has not yet been tested on device or submitted to any app store.

A native Android client for the [Standard.site](https://standard.site) publishing ecosystem on AT Protocol, built with Jetpack Compose and Material 3.

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
| **Status** | TestFlight-ready | Pre-alpha |

Both versions share the same AT Protocol data model shapes, the same Constellation cross-repo discovery pattern, the same publication/document theme resolution cascade, and the same three-tab structure (Read / Discover / Write).

## Features

Aligns with Inkwell iOS feature set:

- OAuth-based AT Protocol authentication (no app password)
- Reads `site.standard.publication` and `site.standard.document` records from their owning PDS
- Renders Leaflet block content and Markpub/Offprint/pckt markdown formats
- Resolves display theme from Leaflet's rich palette → `basicTheme` → Material 3 defaults
- Publishes standard.site documents with selectable content formats
- Creates and removes `site.standard.graph.subscription` and `site.standard.graph.recommend` records
- Cross-repo comment discovery via Constellation (microcosm.blue)
- Paginated feed with infinite scroll
- Prev/next post navigation
- App Intents for Siri/Shortcuts (iOS) / Assistant (Android)
- Background notification polling

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
3. Sync Gradle and run on an API 26+ device or emulator.

## Dependencies

- Jetpack Compose (Material 3)
- Navigation Compose
- OkHttp + kotlinx.serialization
- Hilt (dependency injection)
- WorkManager (background tasks)
- Coil (image loading)

## License

AGPL 3.0 — see [Inkwell iOS](https://github.com/ewanc26/inkwell) for the canonical license.
