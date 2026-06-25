package uk.ewancroft.inkwell.data.model.bluesky

import kotlinx.serialization.Serializable

// --- Bluesky Profile ---

@Serializable
data class BlueskyProfile(
    val did: String,
    val handle: String,
    val displayName: String? = null,
    val description: String? = null,
    val avatar: String? = null,
    val banner: String? = null,
    val followersCount: Int? = null,
    val followsCount: Int? = null,
    val postsCount: Int? = null
)

// --- Constellation Backlinks ---

@Serializable
data class ConstellationBacklink(
    val did: String,
    val collection: String,
    val rkey: String
) {
    val recordUri: String get() = "at://$did/$collection/$rkey"
}

@Serializable
data class ConstellationResponse(
    val records: List<ConstellationBacklink>? = null,
    val cursor: String? = null
)
