package uk.ewancroft.inkwell.data.model.atproto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.ewancroft.inkwell.data.model.common.BlobRef
import uk.ewancroft.inkwell.data.model.common.StrongRef
import uk.ewancroft.inkwell.data.model.content.LeafletPage
import uk.ewancroft.inkwell.data.model.content.LeafletContent

// --- AT Protocol Records ---

@Serializable
data class PublicationRecord(
    @SerialName("\$type") val type: String = "site.standard.publication",
    val url: String,
    val name: String,
    val description: String? = null,
    val icon: BlobRef? = null,
    val theme: PublicationTheme? = null,
    val basicTheme: BasicTheme? = null,
    val preferences: PublicationPreferences? = null
)

@Serializable
data class DocumentRecord(
    @SerialName("\$type") val type: String = "site.standard.document",
    val site: String,
    val title: String,
    val publishedAt: String,
    val path: String? = null,
    val description: String? = null,
    val tags: List<String>? = null,
    val content: uk.ewancroft.inkwell.data.model.common.ContentUnion? = null,
    val textContent: String? = null,
    val coverImage: BlobRef? = null,
    val theme: PublicationTheme? = null,
    val preferences: DocumentPreferences? = null,
    val bskyPostRef: StrongRef? = null
)
