package uk.ewancroft.inkwell.data.model.graph

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// --- Graph subscriptions & recommendations ---

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
