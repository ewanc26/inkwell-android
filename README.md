# Inkwell for Android

A native Android client for the AT Protocol, built with Jetpack Compose and Material 3.

## Architecture

- **Language:** Kotlin
- **UI:** Jetpack Compose with Material 3
- **Navigation:** Navigation Compose (type-safe)
- **Networking:** OkHttp + kotlinx.serialization
- **DI:** Hilt
- **State:** ViewModel + StateFlow
- **Background:** WorkManager
- **Image loading:** Coil

## Features

- OAuth-based AT Protocol authentication
- Reader with paginated feed and full block rendering
- Writer for composing standard.site documents
- Cross-repo comments via Constellation (microcosm.blue)
- Subscriptions and recommends
- Publication-aware theme system
- Material 3 dynamic color support

## Project Structure

```
app/src/main/java/uk/ewancroft/inkwell/
├── data/
│   ├── model/Models.kt       # AT Protocol data types
│   ├── remote/AtProtoApi.kt  # PDS HTTP client
│   └── remote/ConstellationClient.kt  # Cross-repo discovery
├── ui/
│   ├── theme/Theme.kt        # Material 3 + standard.site theme resolution
│   ├── navigation/NavGraph.kt# Bottom nav + screen routes
│   ├── auth/LoginScreen.kt   # OAuth sign-in
│   ├── reader/ReaderScreen.kt# Feed + post detail
│   ├── writer/WriterScreen.kt# Markdown composer
│   └── discover/DiscoverScreen.kt
├── InkwellApp.kt
└── MainActivity.kt
```

## Building

Open in Android Studio Hedgehog or later, sync Gradle, and run on an API 26+ device.

## Mirroring Inkwell iOS

This project mirrors the architecture of [Inkwell for iOS](https://github.com/ewanc26/inkwell):
- Same AT Protocol data models (kotlinx.serialization ↔ Swift Codable)
- Same OAuth + DPoP authentication flow
- Same Constellation cross-repo discovery pattern
- Same publication/document theme resolution cascade
- Same paginated feed with infinite scroll
