package uk.ewancroft.inkwell.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val content: ContentUnion? = null,
    val textContent: String? = null,
    val coverImage: BlobRef? = null,
    val theme: PublicationTheme? = null,
    val preferences: DocumentPreferences? = null,
    val bskyPostRef: StrongRef? = null
)

@Serializable
data class GraphSubscription(
    @SerialName("\$type") val type: String = "site.standard.graph.subscription",
    val publication: String
)

@Serializable
data class GraphRecommend(
    @SerialName("\$type") val type: String = "site.standard.graph.recommend",
    val document: String
)

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

// --- Theme ---

@Serializable
data class PublicationTheme(
    @SerialName("\$type") val type: String = "pub.leaflet.publication#theme",
    val backgroundColor: ColorValue? = null,
    val pageBackground: ColorValue? = null,
    val primary: ColorValue? = null,
    val accentBackground: ColorValue? = null,
    val accentText: ColorValue? = null,
    val pageWidth: Int? = null,
    val showPageBackground: Boolean? = null,
    val headingFont: String? = null,
    val bodyFont: String? = null
)

@Serializable
data class BasicTheme(
    @SerialName("\$type") val type: String = "site.standard.theme.basic",
    val background: RgbColor,
    val foreground: RgbColor,
    val accent: RgbColor,
    val accentForeground: RgbColor
)

@Serializable
data class ColorValue(
    @SerialName("\$type") val type: String = "pub.leaflet.theme.color#rgb",
    val r: Int,
    val g: Int,
    val b: Int,
    val a: Int? = null
)

@Serializable
data class RgbColor(
    @SerialName("\$type") val type: String = "site.standard.theme.color#rgb",
    val r: Int,
    val g: Int,
    val b: Int
)

// --- Preferences ---

@Serializable
data class PublicationPreferences(
    val showInDiscover: Boolean? = null
)

@Serializable
data class DocumentPreferences(
    val showComments: Boolean? = null,
    val showMentions: Boolean? = null,
    val showRecommends: Boolean? = null,
    val showPrevNext: Boolean? = null,
    val showInDiscover: Boolean? = null
)

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
    val pages: List<LeafletPage>? = null
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

// --- Constellation Backlink ---

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
