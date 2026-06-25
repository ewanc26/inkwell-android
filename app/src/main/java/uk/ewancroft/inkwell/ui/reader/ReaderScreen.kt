/**
 * Reader feed and post detail screens.
 *
 * Two-tab feed (Following / Yours) with paginated post cards and a
 * post-detail view for full content rendering. Mirrors Inkwell iOS
 * BrowseDocumentsView and PostDetailView.
 *
 * The feed is currently populated with sample cards — the full
 * implementation connects to a ViewModel with PaginationState that loads
 * site.standard.document records from the user's PDS and subscribed
 * publications via Constellation.
 */
package uk.ewancroft.inkwell.ui.reader

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*

// ── Reader Screen ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Following", "Yours")

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
            }
        }

        when (selectedTab) {
            0 -> FeedContent(feedType = "following")
            1 -> FeedContent(feedType = "yours")
        }
    }
}

// ── Feed Content ─────────────────────────────────────────────────────────

@Composable
private fun FeedContent(feedType: String) {
    // Future: replace sample data with ViewModel-driven PaginationState
    val posts = remember { List(6) { it } }

    if (posts.isEmpty()) {
        // Empty state with contextual copy per tab
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Book, contentDescription = null, modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Text(
                    if (feedType == "following") "Nothing to read yet" else "No published posts",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    if (feedType == "following") "Subscribe to publications to see their posts here."
                    else "Posts you publish will appear here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(posts) {
                PostCard(
                    title = "Sample Post Title",
                    description = "A brief excerpt from the post to give readers context...",
                    publicationName = "Sample Publication",
                    date = "23 Jun 2026",
                    coverUrl = null
                )
            }
        }
    }
}

// ── Post Card ────────────────────────────────────────────────────────────

/** Single post card in the feed: cover image, title, description, metadata. */
@Composable
fun PostCard(
    title: String,
    description: String?,
    publicationName: String?,
    date: String,
    coverUrl: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                    contentScale = ContentScale.Crop
                )
            }

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (description != null) {
                    Text(description, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(date, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (publicationName != null) {
                        Text("·", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(publicationName, style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

// ── Post Detail Screen ───────────────────────────────────────────────────

/**
 * Full document view for a single post.
 * Loads the site.standard.document record, resolves its theme, and renders
 * the content blocks (Leaflet, Markdown, etc.). Placeholder until the
 * block-rendering engine is wired in.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(uri: String) {
    Scaffold(topBar = { TopAppBar(title = { Text("Post") }) }) { padding ->
        Column(Modifier.padding(padding).padding(20.dp)) {
            Text("Post content for: $uri", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))
            Text("Block rendering, comments, and interactions are rendered here in the full implementation.")
        }
    }
}
