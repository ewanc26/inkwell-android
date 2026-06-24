package uk.ewancroft.inkwell.data.remote

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Low-level AT Protocol HTTP client.
 *
 * Handles authenticated (OAuth DPoP) and unauthenticated requests to PDS
 * servers and the public Bluesky API. Mirror's Inkwell iOS LoginStateManager's
 * authenticatedData/unauthenticatedData methods.
 */
class AtProtoClient(
    private val pdsUrl: String? = null,
    private val bearerToken: String? = null
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val jsonMediaType = "application/json".toMediaType()

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    /** Fetches a single page of records from a repository collection. */
    suspend fun listRecords(
        did: String,
        collection: String,
        limit: Int = 25,
        cursor: String? = null
    ): ListRecordsResponse<kotlinx.serialization.json.JsonObject> {
        val base = if (did == currentDid()) pdsUrl else resolvePdsUrl(did)
        val url = "$base/xrpc/com.atproto.repo.listRecords".toHttpUrl {
            addQueryParameter("repo", did)
            addQueryParameter("collection", collection)
            addQueryParameter("limit", limit.toString())
            cursor?.let { addQueryParameter("cursor", it) }
        }

        val request = Request.Builder().url(url).get()
            .addAuthIfOwn(did)
            .build()

        val response = client.newCall(request).execute()
        return json.decodeFromString(response.body!!.string())
    }

    /** Fetches a profile from the public Bluesky API (no auth). */
    suspend fun getProfile(did: String): BlueskyProfile {
        val url = "https://public.api.bsky.app/xrpc/app.bsky.actor.getProfile?actor=$did"
        val response = client.newCall(Request.Builder().url(url).get().build()).execute()
        return json.decodeFromString(response.body!!.string())
    }

    /** Resolves a handle to a DID via the public API. */
    suspend fun resolveHandle(handle: String): String {
        val url = "https://public.api.bsky.app/xrpc/com.atproto.identity.resolveHandle?handle=$handle"
        val response = client.newCall(Request.Builder().url(url).get().build()).execute()
        val body = json.decodeFromString<kotlinx.serialization.json.JsonObject>(response.body!!.string())
        return body["did"]!!.toString().trim('"')
    }

    /** Creates a record in the user's repository (authenticated). */
    suspend fun createRecord(
        collection: String,
        record: kotlinx.serialization.json.JsonObject,
        repo: String
    ): StrongRef {
        val base = pdsUrl ?: throw IllegalStateException("Not authenticated")
        val url = "$base/xrpc/com.atproto.repo.createRecord"
        val body = json.encodeToString(kotlinx.serialization.json.JsonObject::class.java, buildJsonObject {
            put("repo", repo)
            put("collection", collection)
            put("record", record)
        })

        val request = Request.Builder().url(url).post(body.toRequestBody(jsonMediaType))
            .header("Authorization", "Bearer $bearerToken")
            .build()

        val response = client.newCall(request).execute()
        return json.decodeFromString(response.body!!.string())
    }

    /** Deletes a record by collection + rkey (authenticated). */
    suspend fun deleteRecord(collection: String, rkey: String, repo: String) {
        val base = pdsUrl ?: throw IllegalStateException("Not authenticated")
        val url = "$base/xrpc/com.atproto.repo.deleteRecord"
        val body = json.encodeToString(kotlinx.serialization.json.JsonObject::class.java, buildJsonObject {
            put("repo", repo)
            put("collection", collection)
            put("rkey", rkey)
        })

        val request = Request.Builder().url(url).post(body.toRequestBody(jsonMediaType))
            .header("Authorization", "Bearer $bearerToken")
            .build()

        client.newCall(request).execute().close()
    }

    /** Fetches a single record by AT-URI. */
    suspend fun getRecord(uri: String): RecordEntry<kotlinx.serialization.json.JsonObject> {
        val parsed = requireNotNull(AtUri.parse(uri))
        val base = if (parsed.did == currentDid()) pdsUrl else resolvePdsUrl(parsed.did)
        val url = "$base/xrpc/com.atproto.repo.getRecord".toHttpUrl {
            addQueryParameter("repo", parsed.did)
            addQueryParameter("collection", parsed.collection)
            addQueryParameter("rkey", parsed.recordKey)
        }

        val request = Request.Builder().url(url).get()
            .addAuthIfOwn(parsed.did)
            .build()

        val response = client.newCall(request).execute()
        return json.decodeFromString(response.body!!.string())
    }

    private fun currentDid(): String? = null // Set by session state
    private fun resolvePdsUrl(did: String): String {
        // In production: resolve via plc.directory. For now, use public API domain.
        return "https://public.api.bsky.app"
    }

    private fun Request.Builder.addAuthIfOwn(did: String): Request.Builder {
        if (did == currentDid() && bearerToken != null) {
            header("Authorization", "Bearer $bearerToken")
        }
        return this
    }
}

private fun String.toHttpUrl(block: HttpUrl.Builder.() -> Unit): HttpUrl {
    val builder = requireNotNull(HttpUrl.parse(this)) { "Invalid base URL: $this" }
        .newBuilder()
    builder.block()
    return builder.build()
}

// Re-exports
typealias BlueskyProfile = uk.ewancroft.inkwell.data.model.BlueskyProfile
typealias ListRecordsResponse = uk.ewancroft.inkwell.data.model.ListRecordsResponse<JsonObject>
typealias RecordEntry = uk.ewancroft.inkwell.data.model.RecordEntry<JsonObject>
typealias StrongRef = uk.ewancroft.inkwell.data.model.StrongRef
typealias AtUri = uk.ewancroft.inkwell.data.model.AtUri
