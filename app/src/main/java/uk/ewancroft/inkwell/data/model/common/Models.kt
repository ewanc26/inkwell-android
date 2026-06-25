package uk.ewancroft.inkwell.data.model.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- Primitives ---

@Serializable
data class BlobRef(
    @SerialName("\$link") val link: String,
    val size: Int = 0,
    @SerialName("\$type") val type: String = "blob",
    val mimeType: String? = null
)

@Serializable
data class StrongRef(
    val uri: String,
    val cid: String? = null
)

@Serializable
data class ContentUnion(
    @SerialName("\$type") val type: String,
    val pages: List<uk.ewancroft.inkwell.data.model.content.LeafletPage>? = null
)

// --- AT Protocol API Types ---

@Serializable
data class AtUri(val did: String, val collection: String, val recordKey: String) {
    companion object {
        fun parse(uri: String): AtUri? {
            val regex = Regex("""^at://([^/]+)/([^/]+)/(.+)$""")
            val match = regex.find(uri) ?: return null
            return AtUri(match.groupValues[1], match.groupValues[2], match.groupValues[3])
        }
    }
    val uri: String get() = "at://$did/$collection/$recordKey"
}

@Serializable
data class RecordEntry<T>(
    val uri: String,
    val cid: String? = null,
    val value: T
)

@Serializable
data class ListRecordsResponse<T>(
    val records: List<RecordEntry<T>>,
    val cursor: String? = null
)
