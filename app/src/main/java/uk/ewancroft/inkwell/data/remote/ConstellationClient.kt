/**
 * Queries the microcosm.blue Constellation API — a global AT Protocol backlink
 * index — for cross-repo discovery of comments, recommends, and mentions.
 *
 * Without this, records in other users' repositories are undiscoverable.
 * Constellation indexes the full AT Protocol firehose so we can ask
 * "which records across the entire network link to URI X?"
 *
 * Mirrors Inkwell iOS ConstellationService: same endpoints, same pagination
 * strategy, same convenience method split (comment / recommend / mention).
 */
package uk.ewancroft.inkwell.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import uk.ewancroft.inkwell.data.model.bluesky.ConstellationBacklink
import uk.ewancroft.inkwell.data.model.bluesky.ConstellationResponse

object ConstellationClient {
    private const val BASE_URL = "https://constellation.microcosm.blue"

    private val json = Json { ignoreUnknownKeys = true }

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // ── Backlink Query ───────────────────────────────────────────────────

    /** Finds all records that link to the given subject via the given source. */
    suspend fun getBacklinks(
        subject: String,
        source: String,
        limit: Int = 50,
        cursor: String? = null
    ): ConstellationResponse = withContext(Dispatchers.IO) {
        val urlBuilder = StringBuilder("$BASE_URL/xrpc/blue.microcosm.links.getBacklinks")
            .append("?subject=").append(subject)
            .append("&source=").append(source)
            .append("&limit=").append(limit)
        cursor?.let { urlBuilder.append("&cursor=").append(it) }

        val request = Request.Builder().url(urlBuilder.toString()).get().build()
        val response = client.newCall(request).execute()
        json.decodeFromString(response.body!!.string())
    }

    // ── Pagination ───────────────────────────────────────────────────────

    /** Paginates through all backlink results. */
    suspend fun paginateBacklinks(
        subject: String,
        source: String,
        maxCount: Int = 200
    ): List<ConstellationBacklink> {
        val all = mutableListOf<ConstellationBacklink>()
        var cursor: String? = null
        while (all.size < maxCount) {
            val result = getBacklinks(subject, source, cursor = cursor)
            all.addAll(result.records.orEmpty())
            cursor = result.cursor
            if (cursor == null) break
        }
        return all.take(maxCount)
    }

    // ── Convenience Methods ──────────────────────────────────────────────

    /** Comments (Leaflet pub.leaflet.comment records) pointing at this document. */
    suspend fun getCommentBacklinks(documentUri: String): List<ConstellationBacklink> =
        paginateBacklinks(documentUri, "pub.leaflet.comment:subject")

    /** Recommends (standard.site graph edges) pointing at this document. */
    suspend fun getRecommendBacklinks(documentUri: String): List<ConstellationBacklink> =
        paginateBacklinks(documentUri, "site.standard.graph.recommend:document")

    /**
     * Mentions in Bluesky posts: searches both link facets and embed.external URIs.
     * Deduplicates by (did, rkey) since a single post could have both a facet
     * link and an embed URI referencing the same document.
     */
    suspend fun getDocumentMentionBacklinks(url: String): List<ConstellationBacklink> {
        val facets = async { paginateBacklinks(
            url, "app.bsky.feed.post:facets[].features[app.bsky.richtext.facet#link].uri"
        ) }
        val embeds = async { paginateBacklinks(
            url, "app.bsky.feed.post:embed.external.uri"
        ) }
        val (f, e) = facets.await() to embeds.await()
        return (f + e).distinctBy { "${it.did}:${it.rkey}" }
    }
}
