package uk.ewancroft.inkwell.data.repository

import io.github.kikin81.atproto.oauth.AtOAuth
import io.github.kikin81.atproto.oauth.OAuthSessionStore
import io.github.kikin81.atproto.runtime.XrpcClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import okhttp3.OkHttpClient
import okhttp3.Request
import uk.ewancroft.inkwell.data.model.bluesky.BlueskyProfile
import uk.ewancroft.inkwell.data.model.common.AtUri
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class UserSessionInfo(
    val handle: String,
    val did: String,
    val pdsUrl: String,
)

@Singleton
class PdsRepository @Inject constructor(
    private val atOAuth: AtOAuth,
    private val sessionStore: OAuthSessionStore,
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private val publicHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val ktorHttpClient = HttpClient(CIO)

    suspend fun getSession(): UserSessionInfo? {
        val session = sessionStore.load() ?: return null
        val pdsUrl = session.pdsUrl ?: return null
        return UserSessionInfo(
            handle = session.handle ?: session.did.orEmpty(),
            did = session.did.orEmpty(),
            pdsUrl = pdsUrl,
        )
    }

    suspend fun listRecords(
        did: String,
        collection: String,
        limit: Int = 25,
        cursor: String? = null,
        pdsUrl: String? = null,
    ): JsonObject {
        val baseUrl = pdsUrl ?: resolvePdsUrl(did) ?: "https://public.api.bsky.app"
        val urlStr = buildString {
            append("$baseUrl/xrpc/com.atproto.repo.listRecords")
            append("?repo=$did")
            append("&collection=$collection")
            append("&limit=$limit")
            cursor?.let { append("&cursor=$it") }
        }
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(urlStr).get().build()
            val response = publicHttpClient.newCall(request).execute()
            json.decodeFromString(response.body!!.string())
        }
    }

    suspend fun getRecord(uri: String, pdsUrl: String? = null): JsonObject {
        val parsed = requireNotNull(AtUri.parse(uri))
        val baseUrl = pdsUrl ?: resolvePdsUrl(parsed.did) ?: "https://public.api.bsky.app"
        val urlStr = "$baseUrl/xrpc/com.atproto.repo.getRecord?repo=${parsed.did}&collection=${parsed.collection}&rkey=${parsed.recordKey}"
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(urlStr).get().build()
            val response = publicHttpClient.newCall(request).execute()
            json.decodeFromString(response.body!!.string())
        }
    }

    suspend fun createRecord(
        collection: String,
        record: JsonObject,
    ): JsonObject {
        val session = sessionStore.load() ?: throw Exception("Not authenticated")
        val authClient = atOAuth.createClient()
        return authClient.procedure(
            nsid = "com.atproto.repo.createRecord",
            params = Unit,
            paramsSerializer = Unit.serializer(),
            input = buildJsonObject {
                put("repo", session.did)
                put("collection", collection)
                put("record", record)
            },
            inputSerializer = JsonObject.serializer(),
            responseSerializer = JsonObject.serializer(),
        )
    }

    suspend fun deleteRecord(collection: String, rkey: String) {
        val session = sessionStore.load() ?: throw Exception("Not authenticated")
        val authClient = atOAuth.createClient()
        authClient.procedure(
            nsid = "com.atproto.repo.deleteRecord",
            params = Unit,
            paramsSerializer = Unit.serializer(),
            input = buildJsonObject {
                put("repo", session.did)
                put("collection", collection)
                put("rkey", rkey)
            },
            inputSerializer = JsonObject.serializer(),
            responseSerializer = JsonObject.serializer(),
        )
    }

    suspend fun resolveHandle(handle: String): String {
        val urlStr = "https://public.api.bsky.app/xrpc/com.atproto.identity.resolveHandle?handle=$handle"
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(urlStr).get().build()
            val response = publicHttpClient.newCall(request).execute()
            val body: JsonObject = json.decodeFromString(response.body!!.string())
            body["did"]!!.jsonPrimitive.content
        }
    }

    suspend fun getProfile(did: String): BlueskyProfile {
        val urlStr = "https://public.api.bsky.app/xrpc/app.bsky.actor.getProfile?actor=$did"
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(urlStr).get().build()
            val response = publicHttpClient.newCall(request).execute()
            json.decodeFromString(response.body!!.string())
        }
    }

    private suspend fun resolvePdsUrl(did: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val urlStr = "https://plc.directory/$did"
                val request = Request.Builder().url(urlStr).get().build()
                val response = publicHttpClient.newCall(request).execute()
                val body = json.parseToJsonElement(response.body!!.string()).jsonObject
                val services = body["service"]?.jsonArray
                    ?: body["services"]?.jsonArray
                services?.firstOrNull { service ->
                    val type = service.jsonObject["type"]?.jsonPrimitive?.content
                    type == "AtprotoPersonalDataServer" || type == "PersonalDataServer"
                }?.jsonObject?.get("url")?.jsonPrimitive?.content
                    ?: body["pdsUrl"]?.jsonPrimitive?.content
            } catch (_: Exception) { null }
        }
    }
}
