package uk.ewancroft.inkwell.data.model.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import uk.ewancroft.inkwell.data.model.common.BlobRef
import uk.ewancroft.inkwell.data.model.common.StrongRef

// --- Leaflet Content Model ---

@Serializable
data class LeafletContent(
    @SerialName("\$type") val type: String = "pub.leaflet.content",
    val pages: List<LeafletPage>? = null
)

@Serializable
data class LeafletPage(
    @SerialName("\$type") val type: String = "pub.leaflet.pages.linearDocument",
    val id: String,
    val blocks: List<BlockContainer>? = null
)

@Serializable
data class BlockContainer(
    @SerialName("\$type") val type: String = "pub.leaflet.pages.linearDocument#block",
    val block: LeafletBlock,
    val alignment: String? = null
)

@Serializable
data class LeafletBlock(
    @SerialName("\$type") val type: String,
    // text/header/blockquote
    val plaintext: String? = null,
    val level: Int? = null,
    val facets: List<LeafletFacet>? = null,
    // code
    val language: String? = null,
    // math
    val tex: String? = null,
    // image
    val image: BlobRef? = null,
    val alt: String? = null,
    // lists
    val children: List<ListItem>? = null,
    val startIndex: Int? = null,
    // embeds
    val subject: StrongRef? = null,
    val standardSitePostSubject: String? = null,
    val standardSitePostCID: String? = null,
    val size: String? = null,
    val showPublicationTheme: Boolean? = null,
    val clientHost: String? = null,
    // website
    val url: String? = null,
    val websiteTitle: String? = null,
    val websiteDescription: String? = null,
    // button
    val text: String? = null,
    // iframe
    val height: Double? = null,
    val aspectRatio: String? = null,
    // page
    val pageIndex: Int? = null,
    val pageDocument: String? = null
)

@Serializable
data class ListItem(
    @SerialName("\$type") val type: String,
    val content: LeafletBlock? = null,
    val checked: Boolean? = null,
    val children: List<ListItem>? = null
)

@Serializable
data class LeafletFacet(
    val index: ByteSlice,
    val features: List<FacetFeature>
)

@Serializable
data class ByteSlice(
    val byteStart: Int,
    val byteEnd: Int
)

@Serializable
data class FacetFeature(
    @SerialName("\$type") val type: String,
    val uri: String? = null,
    val did: String? = null
)
